package test.managers;

import managers.HTTPTaskManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.KVServer;
import tasks.Epic;
import tasks.SimpleTask;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    @BeforeEach
    public void startServer() {
        try {
            kVServer = new KVServer();
            kVServer.start();
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера");
        }
    }

    @AfterEach
    public void stopServer() {
        kVServer.stop();
    }

    @Test
    public void testLoadEmptyListTasks() {
        try {
            TaskManager manager = Managers.getDefault("http://localhost:8078");
            manager.createSimpleTask(task1);
            manager.deleteSimpleTask(task1.getId());

            manager = HTTPTaskManager.loadManager("http://localhost:8078");
            IllegalStateException e = assertThrows(IllegalStateException.class, manager::getSimpleTasks);
            assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
            assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка создания менеджера");
        }
    }

    @Test
    public void testSaveTasksInFile() {
        try {
            TaskManager manager = Managers.getDefault("http://localhost:8078");
            manager.createSimpleTask(task1);
            manager.createEpicTask(epic1);
            manager.createSubtask(epic1.getId(), subtask1);
            manager.getSimpleTask(task1.getId());
            manager.getSubtask(subtask1.getId());
            manager.getEpicTask(epic1.getId());

            manager = HTTPTaskManager.loadManager("http://localhost:8078");
            List<SimpleTask> simpleTasks = manager.getSimpleTasks();
            List<Epic> Epics = manager.getEpicTasks();
            List<Subtask> subtasks = manager.getSubtasks();
            List<Task> history = manager.getHistory();
            assertEquals(task1, simpleTasks.get(0), "Задачи не совпадают");
            assertEquals(epic1, Epics.get(0), "Задачи не совпадают");
            assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают");
            assertEquals(task1, history.get(0), "Задачи не совпадают");
            assertEquals(subtask1, history.get(1), "Задачи не совпадают");
            assertEquals(epic1, history.get(2), "Задачи не совпадают");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка создания менеджера");
        }
    }

    @Test
    public void testSaveEmptyEpicInFile() {
        try {
            TaskManager manager = Managers.getDefault("http://localhost:8078");
            manager.createEpicTask(epic1);
            manager.getEpicTask(epic1.getId());

            manager = HTTPTaskManager.loadManager("http://localhost:8078");
            List<Epic> epic = manager.getEpicTasks();
            List<Task> history = manager.getHistory();
            assertEquals(epic1, epic.get(0), "Задачи не совпадают");
            assertEquals(epic1, history.get(0), "Задачи не совпадают");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка создания менеджера");
        }
    }

    @Test
    public void testSaveEmptyHistoryInFile() {
        try {
            TaskManager manager = Managers.getDefault("http://localhost:8078");
            manager.createSimpleTask(task1);
            manager.createEpicTask(epic1);

            manager = HTTPTaskManager.loadManager("http://localhost:8078");
            List<SimpleTask> simpleTasks = manager.getSimpleTasks();
            List<Epic> epicTasks = manager.getEpicTasks();
            assertEquals(task1, simpleTasks.get(0), "Задачи не совпадают");
            assertEquals(epic1, epicTasks.get(0), "Задачи не совпадают");
            IllegalStateException e = assertThrows(IllegalStateException.class, manager::getHistory);
            assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
            assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка создания менеджера");
        }
    }
}
