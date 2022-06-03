import tasks.*;

public interface TestObjects {
    EpicTask epicTask1 = new EpicTask("EpicTask 1", "Description epic task");
    EpicTask epicTask2 = new EpicTask("EpicTask 2", "Description epic task");
    Subtask subtask1ForEpicTask1 = new Subtask("sub1 for ep1", "Description sub1 for ep 1");
    Subtask subtask2ForEpicTask1 = new Subtask("sub2 for ep1", "Description sub2 for ep 1");
    Subtask subtask1ForEpicTask2 = new Subtask("sub1 for ep2", "Description sub1 for ep 2");
    Task task = new Task("Task", "Description Task");

}
