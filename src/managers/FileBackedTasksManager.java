package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager implements TestObjects {
    private final File save;

    public FileBackedTasksManager(File file) {
        this.save = file;
    }

    public FileBackedTasksManager(String url) {
        save = null;
    }


    @Override
    public void deleteSimpleTasks() {
        super.deleteSimpleTasks();
        save();
    }

    @Override
    public void deleteEpicTasks() {
        super.deleteEpicTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public SimpleTask getSimpleTask(int id) throws IllegalStateException {
        SimpleTask task = super.getSimpleTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicTask(int id) throws IllegalStateException {
        Epic epic = super.getEpicTask(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) throws IllegalStateException {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void createSimpleTask(SimpleTask task) {
        super.createSimpleTask(task);
        save();
    }

    @Override
    public void createEpicTask(Epic epic) {
        super.createEpicTask(epic);
        save();
    }

    @Override
    public void createSubtask(Integer epicTaskId, Subtask subtask) throws IllegalStateException {
        super.createSubtask(epicTaskId, subtask);
        save();
    }

    @Override
    public void deleteSimpleTask(int id) throws IllegalStateException {
        super.deleteSimpleTask(id);
        save();
    }

    @Override
    public void deleteEpicTask(int id) throws IllegalStateException {
        super.deleteEpicTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) throws IllegalStateException {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void updateSimpleTask(SimpleTask simpleTask) {
        super.updateSimpleTask(simpleTask);
        save();
    }

    @Override
    public void updateEpicTask(Epic epic) {
        super.updateEpicTask(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }


    public static FileBackedTasksManager loadManager(File file) {
        List<String> content;
        try (Stream<String> lines = Files.lines(Path.of(file.getAbsolutePath()))) {
            content = lines.skip(1).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.err.println("Ошибка! Не удалось прочитать файл.");
            return null;
        }
        if (!checkFile(content)) {
            throw new ManagerLoadException("Ошибка загрузки из файла. Некоректный файл.");
        }

        var fileBackedTasksManager = new FileBackedTasksManager(file);
        Map<Integer, Task> tasks = new TreeMap<>();
        for (int i = 0; i < content.size() - 2; i++) {
            tasks.putAll(FileBackedTasksManager.taskFromString(content.get(i)));
        }
        for (Task task : tasks.values()) {
            fileBackedTasksManager.restoringTasks(task);
        }
        List<Integer> historyData = FileBackedTasksManager.historyFromString(content.get(content.size() - 1));
        if (historyData != null) {
            for (Integer id : historyData) {
                try {
                    fileBackedTasksManager.loadTaskHistory(id);
                    continue;
                } catch (IllegalStateException e) {
                    //Обработка исключения не нужна т.к. нужно просто пропустить метод
                    // если задача с этим id не является Task
                }
                try {
                    fileBackedTasksManager.loadEpicHistory(id);
                    continue;
                } catch (IllegalStateException e) {
                    //Обработка исключения не нужна т.к. нужно просто пропустить метод
                    // если задача с этим id не является Epic
                }
                try {
                    fileBackedTasksManager.loadSubtaskHistory(id);
                } catch (IllegalStateException e) {
                    System.out.println("Ошибка восстановления истории! Задачи с таким id=" + id + " не существует.");
                }
            }
        }
        Optional<Integer> managerId = tasks.keySet().stream().max(Integer::compare);
        fileBackedTasksManager.recoverIdManager(managerId.orElseThrow(()
                -> new ManagerLoadException("Ошибка восстановления id менеджера")) + 1);
        return fileBackedTasksManager;
    }

    protected void save() {
        StringBuilder sb = new StringBuilder("id,type,name,status,description,epic,startTime,duration\n");
        try {
            for (Task task : super.getSimpleTasks()) {
                sb.append(taskToString(task)).append("\n");
            }
        } catch (IllegalStateException e) {
            //Обработка исключения не нужна т.к. нужно просто пропустить цикл по причине отсутсвия задач
        }

        try {
            for (Task epic : super.getEpicTasks()) {
                sb.append(taskToString(epic)).append("\n");
            }
        } catch (IllegalStateException e) {
            //Обработка исключения не нужна т.к. нужно просто пропустить цикл по причине отсутсвия епиков
        }

        try {
            for (Task subtask : super.getSubtasks()) {
                sb.append(taskToString(subtask)).append("\n");
            }
        } catch (IllegalStateException e) {
            //Обработка исключения не нужна т.к. нужно просто пропустить цикл по причине отсутсвия сабок
        }
        sb.append("\n");
        try {
            sb.append(historyToString(super.getHistoryManager()));
        } catch (IllegalStateException e) {
            sb.append("\n");
            //Обработка исключения не нужна т.к. нужно просто пропустить метод по причине того, что история пуста
        }

        try (Writer file = new FileWriter(save.getAbsolutePath(), StandardCharsets.UTF_8)) {
            file.write(sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    private String taskToString(Task task) {
        return task.toString();
    }

    private static String historyToString(HistoryManager manager) throws IllegalStateException {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static Map<Integer, Task> taskFromString(final String value) {
        String[] data = value.split(",");
        Map<Integer, Task> tasks = new TreeMap<>();
        if (data[6].equals("null")) {
            if (data[1].equals(TaskType.TASK.toString())) {
                tasks.put(Integer.parseInt(data[0]), new SimpleTask(Integer.parseInt(data[0]), data[2], Status.parseStatus(data[3]), data[4]));
            } else if (data[1].equals(TaskType.EPIC.toString())) {
                tasks.put(Integer.parseInt(data[0]), new Epic(Integer.parseInt(data[0]), data[2], Status.parseStatus(data[3]), data[4]));
            } else if (data[1].equals(TaskType.SUBTASK.toString())) {
                tasks.put(Integer.parseInt(data[0]), new Subtask(Integer.parseInt(data[0]), data[2], Status.parseStatus(data[3]), data[4]
                        , Integer.parseInt(data[5])));
            }
        } else {
            if (data[1].equals(TaskType.TASK.toString())) {
                tasks.put(Integer.parseInt(data[0]), new SimpleTask(Integer.parseInt(data[0]), data[2], Status.parseStatus(data[3]), data[4]
                        , data[5], Integer.parseInt(data[6])));
            } else if (data[1].equals(TaskType.EPIC.toString())) {
                tasks.put(Integer.parseInt(data[0]), new Epic(Integer.parseInt(data[0]), data[2], Status.parseStatus(data[3]), data[4]));
            } else if (data[1].equals(TaskType.SUBTASK.toString())) {
                tasks.put(Integer.parseInt(data[0]), new Subtask(Integer.parseInt(data[0]), data[2], Status.parseStatus(data[3]), data[4]
                        , data[6], Integer.parseInt(data[7]), Integer.parseInt(data[5])));
            }
        }
        return tasks;
    }

    private static List<Integer> historyFromString(final String value) {
        if (value.isBlank()) {
            return null;
        }
        return Arrays.stream(value.split(",")).map(Integer::parseInt)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    protected void restoringTasks(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            Epic epic = epics.get(subtask.getEpicID());
            validator.validation(subtask);
            epic.createSubtask(subtask.getId(), subtask);
            epic.checkSubtasksStatus();
            sortedTasks.add(subtask);
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            epics.put(epic.getId(), epic);
            validator.validation(epic);
        } else {
            SimpleTask simpleTask = (SimpleTask) task;
            tasks.put(task.getId(), simpleTask);
            sortedTasks.add(simpleTask);
            validator.validation(task);
        }
    }

    protected void loadTaskHistory(Integer id) {
        super.getSimpleTask(id);
    }

    protected void loadEpicHistory(Integer id) {
        super.getEpicTask(id);
    }

    protected void loadSubtaskHistory(Integer id) {
        super.getSubtask(id);
    }

    private static boolean checkFile(List<String> data) {
        if (data.isEmpty() || data.get(0).isBlank()) {
            throw new ManagerLoadException("Ошибка загрузки из файла. Файл пустой.");
        }
        for (int i = 0; i < data.size() - 2; i++) {
            if (!data.get(i).matches("^(\\d+,)(.+,)+(.+)$")) {
                return false;
            }
            String[] line = data.get(i).split(",");
            if (line.length < 7 || line.length > 8) {
                return false;
            }
            if (line.length == 7) {
                if (!line[0].matches("\\d+") || !(line[1].matches("TASK|EPIC"))
                        || !(line[3].matches("NEW|IN_PROGRESS|DONE")) || !((line[5]
                        .matches("([0-2][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}\\s([01][0-9]|2[0-3]):[0-5][0-9]"))
                        || line[5].equals("null")) || !((line[6].matches("\\d+")) || line[6].equals("null"))) {
                    return false;
                }
            }
            if (line.length == 8) {
                if (!line[0].matches("\\d+") || !(line[1].matches("SUBTASK"))
                        || !(line[3].matches("NEW|IN_PROGRESS|DONE"))
                        || !line[5].matches("\\d+") || !((line[6]
                        .matches("([0-2][0-9]|3[01])\\.(0[0-9]|1[0-2])\\.\\d{4}\\s([01][0-9]|2[0-3]):[0-5][0-9]"))
                        || line[6].equals("null")) || !((line[7].matches("\\d+")) || line[7].equals("null"))) {
                    return false;
                }
            }
        }
        if (!data.get(data.size() - 1).isBlank()) {
            return data.get(data.size() - 1).matches("(\\d|(\\d+,)+(\\d+)$)");
        }
        return true;
    }

    protected void recoverIdManager(Integer id) {
        this.taskId = id;
    }

    public static void main(String[] args) {
        File file = new File("resources" + File.separator + "save.csv");
        var fileBackedTasksManager1 = new FileBackedTasksManager(file);
        fileBackedTasksManager1.createSimpleTask(TestObjects.task);
        fileBackedTasksManager1.createEpicTask(TestObjects.epicTask1);
        fileBackedTasksManager1.createSubtask(TestObjects.epicTask1.getId(), TestObjects.subtask1ForEpicTask1);
        fileBackedTasksManager1.createEpicTask(TestObjects.epicTask2);
        fileBackedTasksManager1.createSubtask(TestObjects.epicTask2.getId(), TestObjects.subtask3ForEpicTask1);
        fileBackedTasksManager1.createSubtask(TestObjects.epicTask1.getId(), TestObjects.subtask2ForEpicTask1);
        subtask1ForEpicTask1.changeStatus(Status.IN_PROGRESS);
        fileBackedTasksManager1.updateSubtask(subtask1ForEpicTask1);
        fileBackedTasksManager1.getSimpleTask(TestObjects.task.getId());
        fileBackedTasksManager1.getEpicTask(TestObjects.epicTask1.getId());
        fileBackedTasksManager1.getSubtask(TestObjects.subtask3ForEpicTask1.getId());
        fileBackedTasksManager1.getSubtask(TestObjects.subtask2ForEpicTask1.getId());

        var fileBackedTasksManager2 = FileBackedTasksManager.loadManager(file);
        System.out.println(fileBackedTasksManager2.getPrioritizedTasks());
        System.out.println("e");
        for (Task task : fileBackedTasksManager2.getHistory()) {
            System.out.println(task);
        }
    }
}
