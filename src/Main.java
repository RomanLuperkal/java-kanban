import Tasks.Status;
import java.lang.NullPointerException;

public class Main implements TestObjects{

    public static void main(String[] args) {
        Manager manager = new Manager();

        try {
            manager.createSimpleTask(simpleTask);
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
            System.out.println(manager.getSimpleTasks());
            System.out.println("Обновление статуса простой задачи до IN_PROGRESS:");
            manager.updateSimpleTask(simpleTask.getId(),
                    manager.getSimpleTask(simpleTask.getId()).changeStatus(Status.IN_PROGRESS));
            System.out.println(manager.getSimpleTasks());
            System.out.println("Обновление статуса простой задачи до DONE:");
            manager.updateSimpleTask(simpleTask.getId(),
                    manager.getSimpleTask(simpleTask.getId()).changeStatus(Status.DONE));
            System.out.println(manager.getSimpleTasks());
            System.out.println("Обновление 1й подзадачи до статуса IN_PROGRESS у 1й эпической задачи");
            manager.updateSubtask(epicTask1.getId(), subtask1ForEpicTask1.getId(),
                    manager.getSubtask(subtask1ForEpicTask1.getId()).changeStatus(Status.IN_PROGRESS));
            System.out.println(manager.getEpicTask(epicTask1.getId()));
            System.out.println("Обновление 2й подзадачи до статуса IN_PROGRESS у 1й эпической задачи");
            manager.updateSubtask(epicTask1.getId(), subtask2ForEpicTask1.getId(),
                    manager.getSubtask(subtask2ForEpicTask1.getId()).changeStatus(Status.IN_PROGRESS));
            System.out.println(manager.getEpicTask(epicTask1.getId()));
            System.out.println("Обновление 1й подзадачи до статуса DONE у 1й эпической задачи");
            manager.updateSubtask(epicTask1.getId(), subtask1ForEpicTask1.getId(),
                    manager.getSubtask(subtask1ForEpicTask1.getId()).changeStatus(Status.DONE));
            System.out.println(manager.getEpicTask(epicTask1.getId()));
            System.out.println("Обновление 2й подзадачи до статуса DONE у 1й эпической задачи");
            manager.updateSubtask(epicTask1.getId(), subtask2ForEpicTask1.getId(),
                    manager.getSubtask(subtask2ForEpicTask1.getId()).changeStatus(Status.DONE));
            System.out.println(manager.getEpicTask(epicTask1.getId()));
            System.out.println("Удаление подзадачи из 1й эпической задачи");
            manager.deleteSubtask(epicTask1.getId(), subtask1ForEpicTask1.getId());
            System.out.println(manager.getEpicTask(epicTask1.getId()));
            System.out.println("Удаление 2й эпической задачи");
            manager.deleteEpicTask(epicTask2.getId());
            System.out.println(manager.getEpicTasks());
            System.out.println("Удаление простой задачи");
            manager.deleteSimpleTask(simpleTask.getId());
            System.out.println(manager.getSimpleTasks());
        }catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }
}
