package managers;

import Exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager, TestObjects {
    private final File save;

    public FileBackedTasksManager(File file) {
        this.save = file;
    }


    @Override
    public void deleteTasks() {
        super.deleteTasks();
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
    public Task getTask(int id) throws IllegalStateException {
        Task task = super.getTask(id);
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
    public void createTask(Task task) {
        super.createTask(task);
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
    public void deleteTask(int id) throws IllegalStateException {
        super.deleteTask(id);
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


    public static FileBackedTasksManager loadFromFile(File file) {
        List<String> content;
        try (Stream<String> lines = Files.lines(Path.of(file.getName()))) {
            content = lines.skip(1).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.out.println("Ошибка! Не удалось прочитать файл.");
            return null;
        }
        if (!checkFile(content)) {
            System.out.println("Некорректный файл!");
            return null;
        }

        var fileBackedTasksManager = new FileBackedTasksManager(file);
        for (int i = 0; i < content.size() - 2; i++) {
            Task task = FileBackedTasksManager.taskFromString(content.get(i));
            if (task instanceof Subtask) {
                String[] data = content.get(i).split(",");
                fileBackedTasksManager.loadSubtaskFromFile(Integer.parseInt(data[data.length - 1]), (Subtask) task);
            } else if (task instanceof Epic) {
                fileBackedTasksManager.loadEpicFromFile((Epic) task);
            } else {
                fileBackedTasksManager.loadTaskFromFile(task);
            }
        }
        List<Integer> historyData = FileBackedTasksManager.historyFromString(content.get(content.size() - 1));
        if (historyData != null) {
            for (Integer id : historyData) {
                try {
                    fileBackedTasksManager.loadTaskHistoryFromFile(id);
                    continue;
                } catch (IllegalStateException e) {
                    //Обработка исключения не нужна т.к. нужно просто пропустить метод
                    // если задача с этим id не является Task
                }
                try {
                    fileBackedTasksManager.loadEpicHistoryFromFile(id);
                    continue;
                } catch (IllegalStateException e) {
                    //Обработка исключения не нужна т.к. нужно просто пропустить метод
                    // если задача с этим id не является Epic
                }
                try {
                    fileBackedTasksManager.loadSubtaskHistoryFromFile(id);
                } catch (IllegalStateException e) {
                    System.out.println("Ошибка восстановления истории! Задачи с таким id=" + id + " не существует.");
                }
            }
        }
        return fileBackedTasksManager;
    }

    private void save() {
        StringBuilder sb = new StringBuilder("id,type,name,status,description,epic\n");
        taskToString(sb);
        sb.append("\n");
        try {
            sb.append(historyToString(super.getHistoryManager()));
        } catch (IllegalStateException e) {
            //Обработка исключения не нужна т.к. нужно просто пропустить метод по причине того, что история пуста
        }

        try (Writer file = new FileWriter(save.getName(), StandardCharsets.UTF_8)) {
            file.write(sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    private void taskToString(StringBuilder sb) {
        try {
            for (Task task : super.getTasks()) {
                sb.append(task.toString()).append("\n");
            }
        } catch (IllegalStateException e) {
            //Обработка исключения не нужна т.к. нужно просто пропустить цикл по причине отсутсвия задач
        }

        try {
            for (Task epic : super.getEpicTasks()) {
                sb.append(epic.toString()).append("\n");
            }
        } catch (IllegalStateException e) {
            //Обработка исключения не нужна т.к. нужно просто пропустить цикл по причине отсутсвия епиков
        }

        try {
            for (Task subtask : super.getSubtasks()) {
                sb.append(subtask.toString()).append("\n");
            }
        } catch (IllegalStateException e) {
            //Обработка исключения не нужна т.к. нужно просто пропустить цикл по причине отсутсвия сабок
        }
    }

    private static String historyToString(HistoryManager manager) throws IllegalStateException {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static Task taskFromString(final String value) {
        String[] data = value.split(",");
        if (data[1].equals(Tasks.TASK.toString())) {
            return new Task(Integer.parseInt(data[0]), data[2], Status.parseStatus(data[3]), data[4]);
        } else if (data[1].equals(Tasks.EPIC.toString())) {
            return new Epic(Integer.parseInt(data[0]), data[2], Status.parseStatus(data[3]), data[4]);
        } else if (data[1].equals(Tasks.SUBTASK.toString())) {
            return new Subtask(Integer.parseInt(data[0]), data[2], Status.parseStatus(data[3]), data[4]
                    , Integer.parseInt(data[5]));
        }
        return null;
    }

    private static List<Integer> historyFromString(final String value) {
        if (value.isBlank()) {
            return null;
        }
        return Arrays.stream(value.split(",")).map(Integer::parseInt)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void loadTaskFromFile(Task task) {
        super.createTask(task);
    }

    private void loadEpicFromFile(Epic epic) {
        super.createEpicTask(epic);
    }

    private void loadSubtaskFromFile(Integer epicId, Subtask subtask) {
        super.createSubtask(epicId, subtask);
    }

    private void loadTaskHistoryFromFile(Integer id) {
        super.getTask(id);
    }

    private void loadEpicHistoryFromFile(Integer id) {
        super.getEpicTask(id);
    }

    private void loadSubtaskHistoryFromFile(Integer id) {
        super.getSubtask(id);
    }

    private static boolean checkFile(List<String> data) {
        if (data.isEmpty() || data.get(0).isBlank()) {
            return false;
        }
        for (int i = 0; i < data.size() - 2; i++) {
            if (!data.get(i).matches("(.+,)(.+)$")) {
                return false;
            }
            String[] line = data.get(i).split(",");
            if (line.length < 5 || line.length > 6) {
                return false;
            }
            if (line.length == 5) {
                if (!line[0].matches("\\d+") || !(line[1].matches("TASK|EPIC"))
                        || !(line[3].matches("NEW|IN_PROGRESS|DONE"))) {
                    return false;
                }
            }
            if (line.length == 6) {
                if (!line[0].matches("\\d+") || !(line[1].matches("SUBTASK"))
                        || !(line[3].matches("NEW|IN_PROGRESS|DONE")) || !line[5].matches("\\d+")) {
                    return false;
                }
            }
        }
        if (!data.get(data.size() - 1).isBlank()) {
            return data.get(data.size() - 1).matches("(\\d+,)+(\\d+)$");
        }
        return true;
    }

    public static void main(String[] args) {
        File file = new File("save.csv");
        var fileBackedTasksManager1 = new FileBackedTasksManager(file);
        fileBackedTasksManager1.createTask(TestObjects.task);
        fileBackedTasksManager1.createEpicTask(TestObjects.epicTask1);
        fileBackedTasksManager1.createEpicTask(TestObjects.epicTask2);
        fileBackedTasksManager1.createSubtask(TestObjects.epicTask1.getId(), TestObjects.subtask1ForEpicTask1);
        fileBackedTasksManager1.createSubtask(TestObjects.epicTask1.getId(), TestObjects.subtask2ForEpicTask1);
        fileBackedTasksManager1.createSubtask(TestObjects.epicTask2.getId(), TestObjects.subtask3ForEpicTask1);
        TestObjects.epicTask1.changeStatus(Status.IN_PROGRESS);
        fileBackedTasksManager1.getTask(TestObjects.task.getId());
        fileBackedTasksManager1.getEpicTask(TestObjects.epicTask1.getId());
        fileBackedTasksManager1.getSubtask(TestObjects.subtask3ForEpicTask1.getId());
        fileBackedTasksManager1.getSubtask(TestObjects.subtask2ForEpicTask1.getId());

        var fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(file);
        System.out.println("e");
        for (Task task : fileBackedTasksManager2.getHistory()) {
            System.out.println(task);
        }
    }
}
