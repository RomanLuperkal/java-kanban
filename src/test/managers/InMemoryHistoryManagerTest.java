package test.managers;

import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SimpleTask;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryHistoryManagerTest {
    private static SimpleTask task1;
    private static SimpleTask task2;
    private static Epic epic1;
    private static Subtask subtask1;
    InMemoryHistoryManager historyManager;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @BeforeEach
    public void initializationHistoryManager() {
        historyManager = new InMemoryHistoryManager();
    }

    @BeforeAll
    public static void initializationTasks() {
        LocalDateTime dateTime = LocalDateTime.now();
        task1 = new SimpleTask("Test task1", "Test task1 desc", dateTime.plusHours(1).format(formatter)
                , 60);
        task2 = new SimpleTask("Test task2", "Test task2 desc", dateTime.plusHours(2).format(formatter)
                , 60);
        subtask1 = new Subtask("Test sub1", "Test sub1 desc", dateTime.plusMinutes(10).format(formatter)
                , 60);
        epic1 = new Epic("Test epic1", "Test epic1 desc");

        task1.setId(0);
        task2.setId(1);
        epic1.setId(2);
        subtask1.setId(3);
        epic1.createSubtask(3, subtask1);
    }


    @Test
    public void testEmptyHistory() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> historyManager.getHistory());
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");

        historyManager.add(task1);
        historyManager.remove(task1.getId());
        e = assertThrows(IllegalStateException.class, historyManager::getHistory);
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void testSimpleTaskInHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверное кол-во задач в истории");
        assertEquals(task1, history.get(0), "Порядок задач в истории не совпадает");
        assertEquals(task2, history.get(1), "Порядок задач в истории не совпадает");
    }

    @Test
    public void testDuplicationHistory() {
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Неверное кол-во задач в истории");
        assertEquals(task1, history.get(0), "задачи не совпадают");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверное кол-во задач в истории");
        assertEquals(task2, history.get(0), "Порядок задач в истории не совпадает");
        assertEquals(task1, history.get(1), "Порядок задач в истории не совпадает");
    }

    @Test
    public void testDeleteFromBeginHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(epic1, history.get(0), "Задачи в истории не совпадают");

        historyManager.add(task1);
        historyManager.add(subtask1);
        historyManager.remove(epic1.getId());
        history = historyManager.getHistory();
        assertEquals(task1, history.get(0), "Задачи в истории не совпадают");
    }

    @Test
    public void testDeleteFromMiddleHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(epic1, history.get(1), "Задачи в истории не совпадают");
    }

    @Test
    public void testDeleteFromEndHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.remove(epic1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(task1, history.get(0), "Задачи в истории не совпадают");

        historyManager.add(epic1);
        historyManager.add(subtask1);
        history = historyManager.getHistory();
        assertEquals(epic1, history.get(1), "Задачи в истории не совпадают");
    }
}