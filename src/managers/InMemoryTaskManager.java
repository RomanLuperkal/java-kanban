package managers;

import exceptions.TaskDateDurationException;
import tasks.*;

import java.lang.IllegalStateException;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, SimpleTask> tasks;
    protected final Map<Integer, Epic> epics;
    protected final HistoryManager history;
    protected int taskId;
    protected final Set<Task> sortedTasks;
    protected final ValidationTaskManager validator;

    public InMemoryTaskManager() {
        epics = new HashMap<>();
        tasks = new HashMap<>();
        sortedTasks = new TreeSet<>();
        history = Managers.getDefaultHistory();
        validator = new ValidationTaskManager();
        this.taskId = 0;
    }

    @Override
    public List<SimpleTask> getSimpleTasks() throws IllegalStateException {
        if (tasks.isEmpty()) {
            throw new IllegalStateException("Список простых задач пуст.");
        } else {
            return new ArrayList<>(tasks.values());
        }
    }

    @Override
    public List<Epic> getEpicTasks() throws IllegalStateException {
        if (epics.isEmpty()) {
            throw new IllegalStateException("Список эпических задач пуст");
        } else {
            return new ArrayList<>(epics.values());
        }
    }

    @Override
    public List<Subtask> getSubtasks() throws IllegalStateException {
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (Epic epic : epics.values()) {
            listSubtasks.addAll(epic.getSubtasks());
        }
        if (listSubtasks.isEmpty()) {
            throw new IllegalStateException("Список подзадач пуст");
        } else {
            return listSubtasks;
        }
    }

    @Override
    public void deleteSimpleTasks() {
        for (Map.Entry<Integer, SimpleTask> entry : tasks.entrySet()) {
            history.remove(entry.getKey());
            sortedTasks.remove(entry.getValue());
            validator.deleteTaskDateTime(entry.getValue());
        }
        tasks.clear();
    }

    @Override
    public void deleteEpicTasks() {
        Map<Integer, Subtask> subtasks;
        for (Epic epic : epics.values()) {
            subtasks = epic.getMapSubtasks();
            for (int key : subtasks.keySet()) {
                history.remove(key);
            }
            history.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void deleteSubtasks() {
        if (epics.isEmpty()) {
            System.out.println("Подзадач нет");
            return;
        }
        Map<Integer, Subtask> subtasks;
        for (Epic epic : epics.values()) {
            subtasks = epic.getMapSubtasks();
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                history.remove(entry.getKey());
                sortedTasks.remove(entry.getValue());
                validator.deleteTaskDateTime(entry.getValue());
            }
            epic.deleteSubtasks();
        }
    }

    @Override
    public SimpleTask getSimpleTask(int id) throws IllegalStateException {
        if (tasks.isEmpty()) {
            throw new IllegalStateException("Список простых задач пуст.");
        }
        if (tasks.get(id) == null || !tasks.containsKey(id)) {
            throw new IllegalStateException("Такой задачи нет");
        } else {
            history.add(tasks.get(id));
            return tasks.get(id);
        }

    }

    @Override
    public Epic getEpicTask(int id) throws IllegalStateException {
        if (epics.isEmpty()) {
            throw new IllegalStateException("Список эпических задач пуст.");
        }
        if (epics.get(id) == null || !epics.containsKey(id)) {
            throw new IllegalStateException("Такой эпической задачи нет");
        } else {
            history.add(epics.get(id));
            return epics.get(id);
        }
    }

    @Override
    public Subtask getSubtask(int id) throws IllegalStateException {
        Map<Integer, Subtask> subtasks;
        for (Epic epic : epics.values()) {
            subtasks = epic.getMapSubtasks();
            if (subtasks.containsKey(id)) {
                history.add(subtasks.get(id));
                return subtasks.get(id);
            }
        }
        throw new IllegalStateException("Такой подзадачи нет");
    }

    @Override
    public void createSimpleTask(SimpleTask task) {
        if (tasks.containsKey(task.getId())) {
            throw new IllegalStateException("Ошибка создания простой задачи! Такая задача уже есть.");
        }
        if (task.getStartTime() != null && LocalDateTime.now().isAfter(task.getStartTime())) {
            throw new IllegalStateException("Время начала не может быть раньше нынешнего момента времени");
        }
        if (validator.validation(task)) {
            throw new TaskDateDurationException("Время начала задач не может пересекаться");
        }
        task.setId(getTaskId());
        tasks.put(task.getId(), task);
        sortedTasks.add(task);
    }

    @Override
    public void createEpicTask(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            throw new IllegalStateException("Ошибка создания эпической задачи! Такая задача уже есть.");
        }
        epic.setId(getTaskId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Integer epicTaskId, Subtask subtask) throws IllegalStateException {
        if (epics.get(epicTaskId) == null || epicTaskId == null) {
            throw new IllegalStateException("Ошибка создания подзадачи. Такой эпической задачи нет");
        }
        Epic epic = epics.get(epicTaskId);
        Map<Integer, Subtask> subtasks = epic.getMapSubtasks();
        if (subtasks.containsKey(subtask.getId())) {
            throw new IllegalStateException("Ошибка создания подзадачи! Такая задача в эпике с id="
                    + epicTaskId + " уже есть.");
        }
        if (subtask.getStartTime() != null && LocalDateTime.now().isAfter(subtask.getStartTime())) {
            throw new IllegalStateException("Время начала не может быть раньше нынешнего момента времени");
        }
        if (validator.validation(subtask)) {
            throw new TaskDateDurationException("Время начала подзадач не может пересекаться");
        }
        subtask.setId(getTaskId());
        epic.createSubtask(subtask.getId(), subtask);
        epic.checkSubtasksStatus();
        sortedTasks.add(subtask);
    }

    @Override
    public void updateSimpleTask(SimpleTask task) throws IllegalStateException {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalStateException("Ошибка обновления задачи! Такой задачи нет.");
        }
        if (task.getStartTime() != null && LocalDateTime.now().isAfter(task.getStartTime())) {
            throw new IllegalStateException("Время начала не может быть раньше нынешнего момента времени");
        }
        if (validator.validationUpdate(task, tasks.get(task.getId()))) {
            throw new TaskDateDurationException("Время начала задач не может пересекаться");
        }
        sortedTasks.remove(tasks.get(task.getId()));
        sortedTasks.add(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) throws IllegalStateException {
        Map<Integer, Subtask> subtasks;
        for (Epic epic : epics.values()) {
            subtasks = epic.getMapSubtasks();
            if (subtasks.containsKey(subtask.getId())) {
                if (subtask.getStartTime() != null && LocalDateTime.now().isAfter(subtask.getStartTime())) {
                    throw new IllegalStateException("Время начала не может быть раньше нынешнего момента времени");
                }
                if (validator.validationUpdate(subtask, subtasks.get(subtask.getId()))) {
                    throw new TaskDateDurationException("Время начала задач не может пересекаться");
                }
                sortedTasks.remove(subtasks.get(subtask.getId()));
                sortedTasks.add(subtask);
                subtasks.put(subtask.getId(), subtask);
                epic.checkSubtasksStatus();
                return;
            }
        }
        throw new IllegalStateException("Ошибка обновления подзачади! В эпических задачах такой подзадачи нет.");
    }

    @Override
    public void updateEpicTask(Epic epic) throws IllegalStateException {
        if (!epics.containsKey(epic.getId())) {
            throw new IllegalStateException("Ошибка обновления эпической задачи! Такой эпической задачи нет.");
        } else {
            epics.put(epic.getId(), epic);
            epic.checkSubtasksStatus();
        }
    }

    @Override
    public void deleteSimpleTask(int id) throws IllegalStateException {
        if (!tasks.containsKey(id)) {
            throw new IllegalStateException("Ошибка удаления задачи! Такой задачи нет");
        }
        validator.deleteTaskDateTime(tasks.get(id));
        sortedTasks.remove(tasks.get(id));
        history.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicTask(int id) throws IllegalStateException {
        if (!epics.containsKey(id)) {
            throw new IllegalStateException("Ошибка удаления эпической задачи! Такой эпической задачи нет");
        }
        List<Subtask> subtasks = getListSubtasksByEpicTaskId(id);
        for (Subtask sub : subtasks) {
            history.remove(sub.getId());
        }
        history.remove(id);
        epics.remove(id);
    }

    @Override
    public void deleteSubtask(int id) throws IllegalStateException {
        Map<Integer, Subtask> subtasks;
        for (Epic epic : epics.values()) {
            subtasks = epic.getMapSubtasks();
            if (subtasks.containsKey(id)) {
                validator.deleteTaskDateTime(subtasks.get(id));
                sortedTasks.remove(subtasks.get(id));
                history.remove(id);
                subtasks.remove(id);
                epic.checkSubtasksStatus();
                return;
            }
        }
        throw new IllegalStateException("Ошибка удаления подзадачи! Эпической задачи с такой подзадачей нет.");
    }

    @Override
    public List<Subtask> getListSubtasksByEpicTaskId(int id) throws IllegalStateException {
        if (!epics.containsKey(id)) {
            throw new IllegalStateException("Ошибка получения списка подзадач! Эпической задачи с id=" + id + " нет.");
        }
        Epic epic = epics.get(id);
        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        if (!sortedTasks.isEmpty()) {
            return new ArrayList<>(sortedTasks);
        }
        throw new IllegalStateException("Список отсортированых задач и подзадач пуст");
    }

    private int getTaskId() {
        return taskId++;
    }

    protected HistoryManager getHistoryManager() {
        return history;
    }
}
