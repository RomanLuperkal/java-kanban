import com.google.gson.*;
import managers.HTTPTaskManager;
import managers.Managers;
import managers.TaskManager;
import managers.TestObjects;
import servers.KVServer;
import servers.adapters.*;
import servers.HttpTaskServer;
import tasks.*;

import java.io.IOException;

public class Main implements TestObjects {

    public static void main(String[] args) {
        try {
            KVServer kvServer = new KVServer();
            kvServer.start();
            HttpTaskServer server = new HttpTaskServer(Managers.getDefault("http://localhost:8078"));
            server.start();
            GsonBuilder gsonBuilder = new GsonBuilder();
            TaskAdapter deserializerTasks = new TaskAdapter("type");
            deserializerTasks.registerBarnType(TaskType.TASK.toString(), SimpleTask.class);
            deserializerTasks.registerBarnType(TaskType.EPIC.toString(), Epic.class);
            deserializerTasks.registerBarnType(TaskType.SUBTASK.toString(), Subtask.class);

            TaskManager manager = Managers.getDefault("http://localhost:8078");
            manager.createSimpleTask(TestObjects.task);
            manager.createEpicTask(TestObjects.epicTask1);
            manager.createSubtask(TestObjects.epicTask1.getId(), TestObjects.subtask1ForEpicTask1);
            manager.createEpicTask(TestObjects.epicTask2);
            manager.createSubtask(TestObjects.epicTask2.getId(), TestObjects.subtask3ForEpicTask1);
            manager.createSubtask(TestObjects.epicTask1.getId(), TestObjects.subtask2ForEpicTask1);
            subtask1ForEpicTask1.changeStatus(Status.IN_PROGRESS);
            manager.updateSubtask(subtask1ForEpicTask1);
            manager.getSimpleTask(TestObjects.task.getId());
            manager.getEpicTask(TestObjects.epicTask1.getId());
            manager.getSubtask(TestObjects.subtask3ForEpicTask1.getId());
            manager.getSubtask(TestObjects.subtask2ForEpicTask1.getId());

            TaskManager manager2 = HTTPTaskManager.loadManager("http://localhost:8078");
            System.out.println(manager.getHistory());
            System.out.println(manager2.getHistory());
            System.out.println(manager.getPrioritizedTasks());
            System.out.println(manager2.getPrioritizedTasks());
            System.out.println();
            kvServer.stop();
            server.stop();
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка подключения к серверу");
        }
    }
}
