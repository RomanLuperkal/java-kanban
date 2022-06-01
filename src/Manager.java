import Tasks.*;

import java.lang.NullPointerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manager {
    private final Map<Integer, SimpleTask> listSimpleTasks;
    private final Map<Integer, EpicTask> listEpicTasks;
    private int tasksId;

    public Manager() {
        listEpicTasks = new HashMap<>();
        listSimpleTasks = new HashMap<>();
        this.tasksId = 0;
    }

    public ArrayList<SimpleTask> getSimpleTasks() throws NullPointerException {
        if (listSimpleTasks.values().size() == 0) {
            throw new NullPointerException("Список простых задач пуст");
        } else {
            return new ArrayList<>(listSimpleTasks.values());
        }
    }

    public ArrayList<EpicTask> getEpicTasks() throws  NullPointerException {
        if (listEpicTasks.values().size() == 0) {
            throw new NullPointerException("Список эпических задач пуст");
        } else {
            return new ArrayList<>(listEpicTasks.values());
        }
    }

    public ArrayList<Subtask> getSubtasks() throws NullPointerException {
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (EpicTask epicTask : listEpicTasks.values()) {
            listSubtasks.addAll(epicTask.getSubtasks());
        }
        if (listSubtasks.size() == 0) {
            throw new NullPointerException("Подзадач нет");
        } else {
            return listSubtasks;
        }
    }

    public void deleteSimpleTasks() {
        listSimpleTasks.clear();
    }

    public void deleteEpicTasks() {
        listEpicTasks.clear();
    }

    public void deleteSubtasks() {
        if (listEpicTasks.size() == 0) {
            System.out.println("Подзадач нет");
            return;
        }
        for (EpicTask epicTask : listEpicTasks.values()) {
            epicTask.deleteSubtasks();
        }
    }

    public SimpleTask getSimpleTask(int id) throws NullPointerException {
        if (listSimpleTasks.get(id) == null) {
            throw new NullPointerException("Такой задачи нет");
        } else {
            return listSimpleTasks.get(id);
        }

    }

    public EpicTask getEpicTask(int id) throws  NullPointerException {
        if (listEpicTasks.get(id) == null) {
            throw new NullPointerException("Такой задачи нет");
        } else {
            return listEpicTasks.get(id);
        }
    }

    public Subtask getSubtask(int id) throws NullPointerException {
        Map<Integer, Subtask> subtasks;
        for (EpicTask epicTask : listEpicTasks.values()) {
            subtasks = epicTask.getMapSubtasks();
            if (subtasks.containsKey(id)) {
                return subtasks.get(id);
            }
        }
        throw new NullPointerException("Такой подзадачи нет");
    }

    public void createSimpleTask(SimpleTask simpleTask) {
        simpleTask.setId(tasksId);
        listSimpleTasks.put(tasksId++, simpleTask);
    }

    public void createEpicTask(EpicTask epicTask) {
        epicTask.setId(tasksId);
        listEpicTasks.put(tasksId++, epicTask);
    }

    public void createSubtask(int EpicTaskId, Subtask subtask) throws NullPointerException {
        if (listEpicTasks.get(EpicTaskId) == null) {
            throw new NullPointerException("Такой эпической задачи нет");
        }
        EpicTask epicTask = listEpicTasks.get(EpicTaskId);
        epicTask.createSubtask(tasksId++, subtask);
    }

    public void updateSimpleTask(int id, SimpleTask simpleTask) throws  NullPointerException {
        if (!listSimpleTasks.containsKey(id)) {
            throw new NullPointerException("Ошибка обновления задачи! Такой задачи нет.");
        }
        simpleTask.setId(id);
        listSimpleTasks.put(id, simpleTask);
    }

    public void updateSubtask(int idEpicTask, int idSubtask, Subtask subtask) throws NullPointerException {
        if (!listEpicTasks.containsKey(idEpicTask)) {
            throw new NullPointerException("Ошибка обновления подзадачи! Эпической задачи с такой подзадачей нет.");
        }
        EpicTask epicTask = listEpicTasks.get(idEpicTask);
        Map<Integer, Subtask> listSubtasks = epicTask.getMapSubtasks();
        if (!listSubtasks.containsKey(idSubtask)) {
            throw new NullPointerException("Ошибка обновления подзачади! В эпической задаче такой подзадачи нет.");
        }
        subtask.setId(idSubtask);
        listSubtasks.put(idSubtask, subtask);
        epicTask.checkSubtasksStatus();
    }

    public void updateEpicTask(int epicId, EpicTask epicTask) throws NullPointerException {
        if (!listEpicTasks.containsKey(epicId)) {
            throw new NullPointerException("Ошибка обновления эпической задачи! Такой эпической задачи нет.");
        }
        epicTask.checkSubtasksStatus();
        listEpicTasks.put(epicId, epicTask);
    }

    public void deleteSimpleTask(int id) throws NullPointerException {
        if (!listSimpleTasks.containsKey(id)) {
            throw new NullPointerException("Ошибка удаления задачи! Такой задачи нет");
        }
        listSimpleTasks.remove(id);
    }

    public void deleteEpicTask(int id) throws NullPointerException {
        if (!listEpicTasks.containsKey(id)) {
            throw new NullPointerException("Ошибка удаления эпической задачи! Такой эпической задачи нет");
        }
        listEpicTasks.remove(id);
    }

    public void deleteSubtask(int epicTaskId, int subtaskId) throws NullPointerException {
        if (!listEpicTasks.containsKey(epicTaskId)) {
            throw new NullPointerException("Ошибка удаления подзадачи! Эпической задачи с такой подзадачей нет.");
        }
        EpicTask epicTask = listEpicTasks.get(epicTaskId);
        Map<Integer, Subtask> subtasks= epicTask.getMapSubtasks();
        if (!subtasks.containsKey(subtaskId)) {
            throw new NullPointerException("Ошибка удаления подзадачи! Такой подзадачи в эпической задачи нет.");
        }
        subtasks.remove(subtaskId);
        epicTask.checkSubtasksStatus();
    }

    public ArrayList<Subtask> getListSubtasksByEpicTaskId(int id) {
        if (!listEpicTasks.containsKey(id)) {
            throw new NullPointerException("Ошибка получения списка подзадач! Эпической задачи с таким id нет.");
        }
        EpicTask epicTask = listEpicTasks.get(id);
        return epicTask.getSubtasks();
    }
}
