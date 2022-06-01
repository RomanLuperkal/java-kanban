import Tasks.EpicTask;
import Tasks.SimpleTask;
import Tasks.Status;
import Tasks.Subtask;

public interface TestObjects {
    EpicTask epicTask1 = new EpicTask("EpicTask 1", "Description epic task");
    EpicTask epicTask2 = new EpicTask("EpicTask 2", "Description epic task");
    Subtask subtask1ForEpicTask1 = new Subtask("sub1 for ep1", "Description sub1 for ep 1");
    Subtask subtask2ForEpicTask1 = new Subtask("sub2 for ep1", "Description sub2 for ep 1");
    Subtask subtask1ForEpicTask2 = new Subtask("sub1 for ep2", "Description sub1 for ep 2");
    SimpleTask simpleTask = new SimpleTask("SimpleTask", "Description SimpleTask");
    SimpleTask updateSimpleTask1 = new SimpleTask("SimpleTask", "Description SimpleTask",
            Status.IN_PROGRESS);
    SimpleTask updateSimpleTask2 = new SimpleTask("SimpleTask", "Description SimpleTask",
            Status.DONE);
    Subtask update1Subtask1ForEpicTask1 = new Subtask("sub1 for ep1",
            "Обновление 1 подзадачи для 1 эпической задачи до IN_PROGRESS", Status.IN_PROGRESS);
    Subtask update1Subtask2ForEpicTask1 = new Subtask("sub2 for ep1",
            "Обновление 2 подзадачи для 1 эпической задачи до IN_PROGRESS", Status.IN_PROGRESS);
    Subtask update2Subtask1ForEpicTask1 = new Subtask("sub1 for ep1",
            "Обновление 1 подзадачи для 1 эпической задачи до DONE", Status.DONE);
    Subtask update2Subtask2ForEpicTask1 = new Subtask("sub2 for ep1",
            "Обновление 2 подзадачи для 1 эпической задачи до DONE", Status.DONE);
}
