package managers;

import tasks.*;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpicTasks();

    List<Subtask> getSubtasks();

    void deleteTasks();

    void deleteEpicTasks();

    void deleteSubtasks();

    Task getTask(int id);

    Epic getEpicTask(int id);

    Subtask getSubtask(int id);

    void createTask(Task task);

    void createEpicTask(Epic epic);

    void createSubtask(Integer epicTaskId, Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpicTask(Epic epic);

    void deleteTask(int id);

    void deleteEpicTask(int id);

    void deleteSubtask(int id);

    List<Subtask> getListSubtasksByEpicTaskId(int id);

    List<Task> getHistory();

}
