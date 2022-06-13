import tasks.Task;

import java.lang.NullPointerException;

public class Main implements TestObjects {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        try {
            inMemoryTaskManager.createTask(task);
            inMemoryTaskManager.createEpicTask(epicTask1);
            inMemoryTaskManager.createSubtask(epicTask1.getId(), subtask1ForEpicTask1);
            inMemoryTaskManager.createSubtask(epicTask1.getId(), subtask2ForEpicTask1);
            inMemoryTaskManager.createEpicTask(epicTask2);
            inMemoryTaskManager.createSubtask(epicTask2.getId(), subtask1ForEpicTask2);

            System.out.println(inMemoryTaskManager.getTask(task.getId()));
            System.out.println(inMemoryTaskManager.getEpicTask(epicTask1.getId()));
            System.out.println(inMemoryTaskManager.getEpicTask(epicTask2.getId()));
            System.out.println(inMemoryTaskManager.getSubtask(subtask1ForEpicTask1.getId()));
            System.out.println(inMemoryTaskManager.getSubtask(subtask2ForEpicTask1.getId()));
            System.out.println(inMemoryTaskManager.getSubtask(subtask1ForEpicTask2.getId()));
            System.out.println(inMemoryTaskManager.getTask(task.getId()));
            System.out.println(inMemoryTaskManager.getTask(task.getId()));
            System.out.println(inMemoryTaskManager.getTask(task.getId()));
            System.out.println(inMemoryTaskManager.getTask(task.getId()));
            System.out.println(inMemoryTaskManager.getTask(task.getId()));
            System.out.println(inMemoryTaskManager.getEpicTask(epicTask1.getId()));

            System.out.println("История просмотра:");
            int i = 1;
            for (Task task : inMemoryTaskManager.getHistory()) {
                System.out.println(i++ + ": " + task);
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }
}
