package managers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import exceptions.ManagerLoadException;
import servers.KVTaskClient;
import servers.adapters.*;
import tasks.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HTTPTaskManager extends FileBackedTasksManager implements TaskManager {
    private final KVTaskClient kvTaskClient;
    private final static Gson gson;

    static {
        TaskAdapter deserializerTasks = new TaskAdapter("type");
        deserializerTasks.registerBarnType(TaskType.TASK.toString(), SimpleTask.class);
        deserializerTasks.registerBarnType(TaskType.EPIC.toString(), Epic.class);
        deserializerTasks.registerBarnType(TaskType.SUBTASK.toString(), Subtask.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, deserializerTasks)
                .registerTypeAdapter(DateTimeFormatter.class, new DateTimeFormatterAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .serializeNulls()
                .create();
    }

    public HTTPTaskManager(String url) throws InterruptedException, IOException {
        super(url);
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    protected void save() {
        try {
            try {
                kvTaskClient.put("SimpleTasks", gson.toJson(super.getSimpleTasks()));
            } catch (IllegalStateException e) {
                kvTaskClient.put("SimpleTasks", "null");
            }
            try {
                List<Epic> temporaryEpics = getEpicTasks();
                List<Epic> listEpics = new ArrayList<>();
                for (Epic epic : temporaryEpics) {
                    listEpics.add((Epic) epic.clone());
                }
                kvTaskClient.put("Epic", gson.toJson(listEpics));
            } catch (IllegalStateException e) {
                kvTaskClient.put("Epic", "null");
            }
            try {
                List<Subtask> subtasks = super.getSubtasks();
                kvTaskClient.put("Subtask", gson.toJson(subtasks));
            } catch (IllegalStateException e) {
                kvTaskClient.put("Subtask", "null");
            }
            try {
                List<Task> history = super.getHistory();
                kvTaskClient.put("History", gson.toJson(history));
            } catch (IllegalStateException e) {
                kvTaskClient.put("History", "null");
            }
        } catch (InterruptedException | IOException | CloneNotSupportedException e) {
            System.out.println("Произошла ошибка сохранения.");
        }
    }


    public static HTTPTaskManager loadManager(String url) throws InterruptedException, IOException {
        HTTPTaskManager manager = new HTTPTaskManager(url);
        final KVTaskClient kvTaskClient = new KVTaskClient(url);
        final Map<Integer, Task> tasks = new TreeMap<>();
        final Map<String, String> data = new HashMap<>();
        data.put("SimpleTasks", kvTaskClient.load("SimpleTasks"));
        data.put("Epic", kvTaskClient.load("Epic"));
        data.put("Subtask", kvTaskClient.load("Subtask"));
        String history = kvTaskClient.load("History");
        for (String jsonObj : data.values()) {
            JsonElement jsonElement;
            try {
                jsonElement = JsonParser.parseString(jsonObj);
            } catch (JsonSyntaxException e) {
                continue;
            }
            if (jsonElement.isJsonArray()) {
                List<Task> deserializedTasks = gson.fromJson(jsonObj, new TypeToken<List<Task>>() {
                }.getType());
                for (Task task : deserializedTasks) {
                    tasks.put(task.getId(), task);
                }
            }
        }
        for (Task task : tasks.values()) {
            manager.restoringTasks(task);
        }
        List<Task> historyTasks = null;
        try {
            JsonElement jsonElement = JsonParser.parseString(history);
            if (jsonElement.isJsonArray()) {
                historyTasks = gson.fromJson(history, new TypeToken<List<Task>>() {
                }.getType());
            }
        } catch (JsonSyntaxException e) {
        }


        if (historyTasks != null) {
            for (Task task : historyTasks) {
                try {
                    manager.loadTaskHistory(task.getId());
                    continue;
                } catch (IllegalStateException e) {
                    //Обработка исключения не нужна т.к. нужно просто пропустить метод
                    // если задача с этим id не является Task
                }
                try {
                    manager.loadEpicHistory(task.getId());
                    continue;
                } catch (IllegalStateException e) {
                    //Обработка исключения не нужна т.к. нужно просто пропустить метод
                    // если задача с этим id не является Epic
                }
                try {
                    manager.loadSubtaskHistory(task.getId());
                } catch (IllegalStateException e) {
                    System.out.println("Ошибка восстановления истории! Задачи с таким id=" + task.getId() + " не существует.");
                }
            }
        }
        if (!tasks.isEmpty()) {
            Optional<Integer> managerId = tasks.keySet().stream().max(Integer::compare);
            manager.recoverIdManager(managerId.orElseThrow(()
                    -> new ManagerLoadException("Ошибка восстановления id менеджера")) + 1);
        }
        return manager;

    }
}
