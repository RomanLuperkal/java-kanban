package managers;

import tasks.*;

import java.util.List;

public interface TaskManager {
    List<SimpleTask> getSimpleTasks();

    List<Epic> getEpicTasks();

    List<Subtask> getSubtasks();

    void deleteSimpleTasks();

    void deleteEpicTasks();

    void deleteSubtasks();

    SimpleTask getSimpleTask(int id);

    Epic getEpicTask(int id);

    Subtask getSubtask(int id);

    void createSimpleTask(SimpleTask simpleTask);

    void createEpicTask(Epic epic);

    void createSubtask(Integer epicTaskId, Subtask subtask);

    void updateSimpleTask(SimpleTask simpleTask);

    void updateSubtask(Subtask subtask);

    void updateEpicTask(Epic epic);

    void deleteSimpleTask(int id);

    void deleteEpicTask(int id);

    void deleteSubtask(int id);

    List<Subtask> getListSubtasksByEpicTaskId(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

}
