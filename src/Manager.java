import Tasks.*;
import java.lang.NullPointerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    private final Map<Integer, SimpleTask> tasks;
    private final Map<Integer, EpicTask> epics;
    private int tasksId;

    public Manager() {
        epics = new HashMap<>();
        tasks = new HashMap<>();
        this.tasksId = 0;
    }

    public List<SimpleTask> getSimpleTasks() throws NullPointerException {
        if (tasks.isEmpty()) {
            throw new NullPointerException("Список простых задач пуст");
        } else {
            return new ArrayList<>(tasks.values());
        }
    }

    public List<EpicTask> getEpicTasks() throws  NullPointerException {
        if (epics.isEmpty()) {
            throw new NullPointerException("Список эпических задач пуст");
        } else {
            return new ArrayList<>(epics.values());
        }
    }

    public List<Subtask> getSubtasks() throws NullPointerException {
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (EpicTask epicTask : epics.values()) {
            listSubtasks.addAll(epicTask.getSubtasks());
        }
        if (listSubtasks.isEmpty()) {
            throw new NullPointerException("Подзадач нет");
        } else {
            return listSubtasks;
        }
    }

    public void deleteSimpleTasks() {
        tasks.clear();
    }

    public void deleteEpicTasks() {
        epics.clear();
    }

    public void deleteSubtasks() {
        if (epics.isEmpty()) {
            System.out.println("Подзадач нет");
            return;
        }
        for (EpicTask epicTask : epics.values()) {
            epicTask.deleteSubtasks();
        }
    }

    public SimpleTask getSimpleTask(int id) throws NullPointerException {
        if (tasks.get(id) == null) {
            throw new NullPointerException("Такой задачи нет");
        } else {
            return tasks.get(id);
        }

    }

    public EpicTask getEpicTask(int id) throws  NullPointerException {
        if (epics.get(id) == null) {
            throw new NullPointerException("Такой задачи нет");
        } else {
            return epics.get(id);
        }
    }

    public Subtask getSubtask(int id) throws NullPointerException {
        Map<Integer, Subtask> subtasks;
        for (EpicTask epicTask : epics.values()) {
            subtasks = epicTask.getMapSubtasks();
            if (subtasks.containsKey(id)) {
                return subtasks.get(id);
            }
        }
        throw new NullPointerException("Такой подзадачи нет");
    }

    public void createSimpleTask(SimpleTask simpleTask) {
        simpleTask.setId(getTaskId());
        tasks.put(simpleTask.getId(), simpleTask);
    }

    public void createEpicTask(EpicTask epicTask) {
        epicTask.setId(getTaskId());
        epics.put(epicTask.getId(), epicTask);
    }

    public void createSubtask(int epicTaskId, Subtask subtask) throws NullPointerException {
        if (epics.get(epicTaskId) == null) {
            throw new NullPointerException("Такой эпической задачи нет");
        }
        EpicTask epicTask = epics.get(epicTaskId);
        subtask.setId(getTaskId());
        epicTask.createSubtask(subtask.getId(), subtask);
    }

    public void updateSimpleTask(int id, SimpleTask simpleTask) throws  NullPointerException {
        if (!tasks.containsKey(id)) {
            throw new NullPointerException("Ошибка обновления задачи! Такой задачи нет.");
        }
        simpleTask.setId(id);
        tasks.put(id, simpleTask);
    }

    public void updateSubtask(int idEpicTask, int idSubtask, Subtask subtask) throws NullPointerException {
        if (!epics.containsKey(idEpicTask)) {
            throw new NullPointerException("Ошибка обновления подзадачи! Эпической задачи с такой подзадачей нет.");
        }
        EpicTask epicTask = epics.get(idEpicTask);
        Map<Integer, Subtask> listSubtasks = epicTask.getMapSubtasks();
        if (!listSubtasks.containsKey(idSubtask)) {
            throw new NullPointerException("Ошибка обновления подзачади! В эпической задаче такой подзадачи нет.");
        }
        listSubtasks.put(idSubtask, subtask);
        epicTask.checkSubtasksStatus();
    }

    public void updateEpicTask(int epicId, EpicTask epicTask) throws NullPointerException {
        if (!epics.containsKey(epicId)) {
            throw new NullPointerException("Ошибка обновления эпической задачи! Такой эпической задачи нет.");
        }
        epicTask.checkSubtasksStatus();
        epics.put(epicId, epicTask);
    }

    public void deleteSimpleTask(int id) throws NullPointerException {
        if (!tasks.containsKey(id)) {
            throw new NullPointerException("Ошибка удаления задачи! Такой задачи нет");
        }
        tasks.remove(id);
    }

    public void deleteEpicTask(int id) throws NullPointerException {
        if (!epics.containsKey(id)) {
            throw new NullPointerException("Ошибка удаления эпической задачи! Такой эпической задачи нет");
        }
        epics.remove(id);
    }

    public void deleteSubtask(int epicTaskId, int subtaskId) throws NullPointerException {
        if (!epics.containsKey(epicTaskId)) {
            throw new NullPointerException("Ошибка удаления подзадачи! Эпической задачи с такой подзадачей нет.");
        }
        EpicTask epicTask = epics.get(epicTaskId);
        Map<Integer, Subtask> subtasks= epicTask.getMapSubtasks();
        if (!subtasks.containsKey(subtaskId)) {
            throw new NullPointerException("Ошибка удаления подзадачи! Такой подзадачи в эпической задачи нет.");
        }
        subtasks.remove(subtaskId);
        epicTask.checkSubtasksStatus();
    }

    public List<Subtask> getListSubtasksByEpicTaskId(int id) {
        if (!epics.containsKey(id)) {
            throw new NullPointerException("Ошибка получения списка подзадач! Эпической задачи с таким id нет.");
        }
        EpicTask epicTask = epics.get(id);
        return epicTask.getSubtasks();
    }

    private int getTaskId() {
        return tasksId++;
    }
}
