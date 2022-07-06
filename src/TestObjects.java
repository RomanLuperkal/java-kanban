import tasks.*;

public interface TestObjects {
    Epic epicTask1 = new Epic("Epic 1", "Description epic task");
    Epic epicTask2 = new Epic("Epic 2", "Description epic task");
    Subtask subtask1ForEpicTask1 = new Subtask("sub1 for ep1", "Description sub1 for ep 1");
    Subtask subtask2ForEpicTask1 = new Subtask("sub2 for ep1", "Description sub2 for ep 1");
    Subtask subtask3ForEpicTask1 = new Subtask("sub3 for ep2", "Description sub3 for ep 2");
}
