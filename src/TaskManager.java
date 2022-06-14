import tasks.*;
import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<EpicTask> getEpicTasks();

    List<Subtask> getSubtasks();

    void deleteTasks();

    void deleteEpicTasks();

    void deleteSubtasks();

    Task getTask(int id);

    EpicTask getEpicTask(int id);

    Subtask getSubtask(int id);

    void createTask(Task task);

    void createEpicTask(EpicTask epicTask);

    void createSubtask(int epicTaskId, Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpicTask(EpicTask epicTask);

    void deleteTask(int id);

    void deleteEpicTask(int id);

    void deleteSubtask(int id);

    List<Subtask> getListSubtasksByEpicTaskId(int id);

    List<Task> getHistory();

}