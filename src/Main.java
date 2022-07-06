import managers.Managers;
import managers.TaskManager;
import tasks.Task;

import java.lang.IllegalStateException;
import java.util.Random;

public class Main implements TestObjects {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        Random random = new Random();
        try {
            inMemoryTaskManager.createEpicTask(epicTask1);
            inMemoryTaskManager.createSubtask(epicTask1.getId(), subtask1ForEpicTask1);
            inMemoryTaskManager.createSubtask(epicTask1.getId(), subtask2ForEpicTask1);
            inMemoryTaskManager.createSubtask(epicTask1.getId(), subtask3ForEpicTask1);
            inMemoryTaskManager.createEpicTask(epicTask2);
            int choice;
            for (int x = 0; x < 10000; x++) {
                choice = random.nextInt(5) + 1;
                callTask(choice, inMemoryTaskManager);
            }
            int i = 0;
            for (Task task : inMemoryTaskManager.getHistory()) {
                System.out.println(i + ": " + task);
                i++;
            }
            System.out.println(inMemoryTaskManager.getHistory());
            inMemoryTaskManager.deleteSubtask(subtask3ForEpicTask1.getId());
            i = 0;
            System.out.println("Удаляем subtask3ForEpicTask1");
            for (Task task : inMemoryTaskManager.getHistory()) {
                System.out.println(i + ": " + task);
                i++;
            }
            inMemoryTaskManager.deleteEpicTask(epicTask1.getId());
            System.out.println("Удаляем epicTask1");
            i = 0;
            for (Task task : inMemoryTaskManager.getHistory()) {
                System.out.println(i + ": " + task);
                i++;
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void callTask(int choice, TaskManager inMemoryTaskManager) {
        switch (choice) {
            case 1:
                System.out.println(inMemoryTaskManager.getEpicTask(epicTask1.getId()));
                break;
            case 2:
                System.out.println(inMemoryTaskManager.getEpicTask(epicTask2.getId()));
                break;
            case 3:
                System.out.println(inMemoryTaskManager.getSubtask(subtask1ForEpicTask1.getId()));
                break;
            case 4:
                System.out.println(inMemoryTaskManager.getSubtask(subtask2ForEpicTask1.getId()));
                break;
            case 5:
                System.out.println(inMemoryTaskManager.getSubtask(subtask3ForEpicTask1.getId()));
                break;
        }

    }
}
