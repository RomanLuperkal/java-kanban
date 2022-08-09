package test.managers;

import exceptions.ManagerLoadException;
import managers.FileBackedTasksManager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {


    @Test
    public void testSaveEmptyListTasksInFile() {
        List<String> testContent;
        String pathTestFile = new File("src" + File.separator + "test" + File.separator + "test-resources"
                + File.separator + "EtalonSaveFiles" + File.separator + "EtalonEmptyList.csv").getAbsolutePath();
        String pathManagerSaveFile = new File("src" + File.separator + "test" + File.separator
                + "test-resources" + File.separator + "TestSaves" + File.separator + "EmptyList.csv").getAbsolutePath();
        try (Stream<String> lines = Files.lines(Path.of(pathTestFile))) {
            testContent = lines.collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.err.println("Ошибка! Не удалось прочитать файл.");
            return;
        }

        FileBackedTasksManager manager = new FileBackedTasksManager(new File(pathManagerSaveFile));
        manager.createSimpleTask(task1);
        manager.deleteSimpleTask(task1.getId());
        List<String> contentManager;
        try (Stream<String> lines = Files.lines(Path.of(pathManagerSaveFile))) {
            contentManager = lines.collect(Collectors.toCollection(ArrayList::new));

        } catch (IOException e) {
            System.err.println("Ошибка! Не удалось прочитать файл.");
            return;
        }
        assertEquals(testContent, contentManager);
    }

    @Test
    public void testRecoveryEmptyListTasksFromFile() {
        String pathManagerSaveFile = new File("src" + File.separator + "test" + File.separator
                + "test-resources" + File.separator + "TestSaves" + File.separator + "EmptyList.csv").getAbsolutePath();
        ManagerLoadException e = assertThrows(ManagerLoadException.class, () -> FileBackedTasksManager
                .loadFromFile(new File(pathManagerSaveFile)));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void testSaveTasksInFile() {
        List<String> testContent;
        String pathTestFile = new File("src" + File.separator + "test" + File.separator + "test-resources"
                + File.separator + "EtalonSaveFiles" + File.separator + "EtalonWithTasksList.csv").getAbsolutePath();
        String pathManagerSaveFile = new File("src" + File.separator + "test" + File.separator
                + "test-resources" + File.separator + "TestSaves" + File.separator
                + "WithTasksList.csv").getAbsolutePath();
        try (Stream<String> lines = Files.lines(Path.of(pathTestFile))) {
            testContent = lines.collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.err.println("Ошибка! Не удалось прочитать файл.");
            return;
        }

        FileBackedTasksManager manager = new FileBackedTasksManager(new File(pathManagerSaveFile));
        manager.createSimpleTask(task1);
        manager.createEpicTask(epic1);
        manager.createSubtask(epic1.getId(), subtask1);
        manager.getSimpleTask(task1.getId());
        manager.getSubtask(subtask1.getId());
        manager.getEpicTask(epic1.getId());
        List<String> contentManager;
        try (Stream<String> lines = Files.lines(Path.of(pathManagerSaveFile))) {
            contentManager = lines.collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.err.println("Ошибка! Не удалось прочитать файл.");
            return;
        }
        assertEquals(testContent, contentManager);
    }

    @Test
    public void testRecoveryTasksFromFile() {
        String pathManagerSaveFile = new File("src" + File.separator + "test" + File.separator
                + "test-resources" + File.separator + "TestSaves" + File.separator
                + "WithTasksList.csv").getAbsolutePath();
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(pathManagerSaveFile));
        assertNotNull(manager, "Менеджер задач не был загружен");
        task1.setId(0);
        epic1.setId(1);
        subtask1.setId(2);
        epic1.createSubtask(2, subtask1);
        List<Task> existTasksList = new ArrayList<>(List.of(task1));
        List<Task> existEpicsList = new ArrayList<>(List.of(epic1));
        List<Task> existSubtasksList = new ArrayList<>(List.of(subtask1));
        List<Task> existHistory = new ArrayList<>(List.of(task1, subtask1, epic1));
        assertEquals(existTasksList, manager.getSimpleTasks(), "Задачи не совпадают");
        assertEquals(existEpicsList, manager.getEpicTasks(), "Задачи не совпадают");
        assertEquals(existSubtasksList, manager.getSubtasks(), "Задачи не совпадают");
        assertEquals(existHistory, manager.getHistory(), "Задачи не совпадают");
    }

    @Test
    public void testSaveEmptyEpicInFile() {
        List<String> testContent;
        String pathTestFile = new File("src" + File.separator + "test" + File.separator + "test-resources"
                + File.separator + "EtalonSaveFiles" + File.separator + "EtalonEmptyEpic.csv").getAbsolutePath();
        String pathManagerSaveFile = new File("src" + File.separator + "test" + File.separator
                + "test-resources" + File.separator + "TestSaves" + File.separator + "EmptyEpic.csv").getAbsolutePath();
        try (Stream<String> lines = Files.lines(Path.of(pathTestFile))) {
            testContent = lines.collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.err.println("Ошибка! Не удалось прочитать файл.");
            return;
        }

        FileBackedTasksManager manager = new FileBackedTasksManager(new File(pathManagerSaveFile));
        manager.createEpicTask(epic1);
        manager.getEpicTask(epic1.getId());
        List<String> contentManager;
        try (Stream<String> lines = Files.lines(Path.of(pathManagerSaveFile))) {
            contentManager = lines.collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.err.println("Ошибка! Не удалось прочитать файл.");
            return;
        }
        assertEquals(testContent, contentManager, "Файлы сохранений не совпадают");
    }

    @Test
    public void testRecoveryEmptyEpicFromFile() {
        String pathManagerSaveFile = new File("src" + File.separator + "test" + File.separator
                + "test-resources" + File.separator + "TestSaves" + File.separator + "EmptyEpic.csv").getAbsolutePath();
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(pathManagerSaveFile));
        epic1.setId(0);
        List<Task> existEpicsList = new ArrayList<>(List.of(epic1));
        List<Task> existHistory = new ArrayList<>(List.of(epic1));
        assertNotNull(manager, "Менеджер задач не был загружен");
        assertEquals(existEpicsList, manager.getEpicTasks(), "Задачи не совпадают");
        assertEquals(existHistory, manager.getHistory(), "Задачи не совпадают");
    }

    @Test
    public void testSaveEmptyHistoryInFile() {
        List<String> testContent;
        String pathTestFile = new File("src" + File.separator + "test" + File.separator + "test-resources"
                + File.separator + "EtalonSaveFiles" + File.separator + "EtalonEmptyHistory.csv").getAbsolutePath();
        String pathManagerSaveFile = new File("src" + File.separator + "test" + File.separator
                + "test-resources" + File.separator + "TestSaves" + File.separator
                + "EmptyHistory.csv").getAbsolutePath();
        try (Stream<String> lines = Files.lines(Path.of(pathTestFile))) {
            testContent = lines.collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.err.println("Ошибка! Не удалось прочитать файл.");
            return;
        }

        FileBackedTasksManager manager = new FileBackedTasksManager(new File(pathManagerSaveFile));
        manager.createSimpleTask(task1);
        manager.createEpicTask(epic1);
        List<String> contentManager;
        try (Stream<String> lines = Files.lines(Path.of(pathManagerSaveFile))) {
            contentManager = lines.collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.err.println("Ошибка! Не удалось прочитать файл.");
            return;
        }
        assertEquals(testContent, contentManager, "Файлы сохранений не совпадают");
    }

    @Test
    public void testRecoveryEmptyHistoryFromFile() {
        String pathManagerSaveFile = new File("src" + File.separator + "test" + File.separator
                + "test-resources" + File.separator + "TestSaves" + File.separator
                + "EmptyHistory.csv").getAbsolutePath();
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(pathManagerSaveFile));
        task1.setId(0);
        epic1.setId(1);
        List<Task> existListEpics = new ArrayList<>(List.of(epic1));
        List<Task> existListTasks = new ArrayList<>(List.of(task1));
        assertNotNull(manager, "Менеджер задач не был загружен");
        assertEquals(existListEpics, manager.getEpicTasks(), "Задачи не совпадают");
        assertEquals(existListTasks, manager.getSimpleTasks(), "Задачи не совпадают");

        IllegalStateException e = assertThrows(IllegalStateException.class, manager::getHistory);
        assertNotNull(e.getMessage());
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void testGetPrioritizedTasks() {
        Subtask subtask3 = new Subtask("Test sub3", "Test sub3 desc");
        List<Task> expectedPrioritizedTasks = new ArrayList<>(List.of(task1, subtask1, subtask2, subtask3));
        manager.createSimpleTask(task1);
        manager.createEpicTask(epic1);
        manager.createSubtask(epic1.getId(), subtask1);
        manager.createSubtask(epic1.getId(), subtask2);
        manager.createSubtask(epic1.getId(), subtask3);
        assertEquals(expectedPrioritizedTasks, manager.getPrioritizedTasks(), "Приоритет задач не совпадает");
    }
}
