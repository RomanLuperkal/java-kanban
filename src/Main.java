import tasks.Status;
import java.lang.NullPointerException;

public class Main implements TestObjects{

    public static void main(String[] args) {
        Manager manager = new Manager();

        try {
            manager.createTask(task);
            manager.createEpicTask(epicTask1);
            manager.createSubtask(epicTask1.getId(), subtask1ForEpicTask1);
            manager.createSubtask(epicTask1.getId(), subtask2ForEpicTask1);
            manager.createEpicTask(epicTask2);
            manager.createSubtask(epicTask2.getId(), subtask1ForEpicTask2);

            System.out.println("Список всех подзадач:");
            System.out.println(manager.getSubtasks());
            System.out.println("Список всех эпических задач:");
            System.out.println(manager.getEpicTasks());
            System.out.println("Список всех простых задач:");
            System.out.println(manager.getTasks());
            System.out.println("Обновление статуса простой задачи до IN_PROGRESS:");
            task.changeStatus(Status.IN_PROGRESS);
            manager.updateTask(task);
            System.out.println(manager.getTasks());
            System.out.println("Обновление статуса простой задачи до DONE:");
            task.changeStatus(Status.DONE);
            manager.updateTask(task);
            System.out.println(manager.getTasks());
            System.out.println("Обновление 1й подзадачи до статуса IN_PROGRESS у 1й эпической задачи");
            subtask1ForEpicTask1.changeStatus(Status.IN_PROGRESS);
            manager.updateSubtask(subtask1ForEpicTask1);
            System.out.println(manager.getEpicTask(epicTask1.getId()));
            System.out.println("Обновление 2й подзадачи до статуса IN_PROGRESS у 1й эпической задачи");
            subtask2ForEpicTask1.changeStatus(Status.IN_PROGRESS);
            manager.updateSubtask(subtask2ForEpicTask1);
            System.out.println(manager.getEpicTask(epicTask1.getId()));
            System.out.println("Обновление 1й подзадачи до статуса DONE у 1й эпической задачи");
            subtask1ForEpicTask1.changeStatus(Status.DONE);
            manager.updateSubtask(subtask1ForEpicTask1);
            System.out.println(manager.getEpicTask(epicTask1.getId()));
            System.out.println("Обновление 2й подзадачи до статуса DONE у 1й эпической задачи");
            subtask2ForEpicTask1.changeStatus(Status.DONE);
            manager.updateSubtask(subtask2ForEpicTask1);
            System.out.println(manager.getEpicTask(epicTask1.getId()));
            System.out.println("Удаление подзадачи из 1й эпической задачи");
            manager.deleteSubtask(subtask1ForEpicTask1.getId());
            System.out.println(manager.getEpicTask(epicTask1.getId()));
            System.out.println("Удаление 2й эпической задачи");
            manager.deleteEpicTask(epicTask2.getId());
            System.out.println(manager.getEpicTasks());
            System.out.println("Удаление простой задачи");
            manager.deleteTask(task.getId());
            System.out.println(manager.getTasks());
        }catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }
}
