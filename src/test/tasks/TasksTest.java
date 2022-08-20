package test.tasks;

import managers.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;
import tasks.SimpleTask;
import tasks.Status;
import tasks.Subtask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TasksTest {
    private Epic epic;
    private List<Subtask> subtasks;
    private InMemoryTaskManager taskManager;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @BeforeEach
    public void initializationsTasks() {
        epic = new Epic("Test epic", "desc ep");
        subtasks = List.of(new Subtask("Test sub1", "dest sub1")
                , new Subtask("Test sub2", "dest sub2")
                , new Subtask("Test sub3", "dest sub3"));
    }

    @BeforeEach
    public void initializationManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldReturnNewWhenEmptyEpic() {
        taskManager.createEpicTask(epic);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnNewWhenSubtasksAreNew() {
        taskManager.createEpicTask(epic);
        for (Subtask subtask : subtasks)
            taskManager.createSubtask(epic.getId(), subtask);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnDoneWhenSubtasksAreDone() {
        taskManager.createEpicTask(epic);
        for (Subtask subtask : subtasks) {
            taskManager.createSubtask(epic.getId(), subtask);
            subtask.changeStatus(Status.DONE);
            taskManager.updateSubtask(subtask);
        }
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void shouldReturnIn_ProgressWhenSubtasksAreDoneAndNew() {
        taskManager.createEpicTask(epic);
        for (Subtask subtask : subtasks) {
            taskManager.createSubtask(epic.getId(), subtask);
        }
        subtasks.get(0).changeStatus(Status.DONE);
        taskManager.updateSubtask(subtasks.get(0));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldReturnIn_ProgressWhenSubtasksAreIn_Progress() {
        taskManager.createEpicTask(epic);
        for (Subtask subtask : subtasks) {
            taskManager.createSubtask(epic.getId(), subtask);
            subtask.changeStatus(Status.IN_PROGRESS);
            taskManager.updateSubtask(subtask);
        }
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldReturnIn_ProgressWhenSubtasksAreAllStatus() {
        taskManager.createEpicTask(epic);
        for (Subtask subtask : subtasks)
            taskManager.createSubtask(epic.getId(), subtask);
        subtasks.get(0).changeStatus(Status.IN_PROGRESS);
        subtasks.get(1).changeStatus(Status.DONE);
        for (Subtask subtask : subtasks)
            taskManager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testSimpleTaskGetEndTime() {
        LocalDateTime taskDateTime = LocalDateTime.parse(LocalDateTime.now().plusHours(1).format(formatter), formatter);
        SimpleTask task1 = new SimpleTask("Test task1", "Test task1 desc", taskDateTime.format(formatter)
                , 60);
        assertEquals(task1.getEndTime(), (taskDateTime.plusMinutes(60)), "Время завершения задачи не совпадает");

        SimpleTask task2 = new SimpleTask("Test task1", "Test task1 desc");
        assertNull(task2.getEndTime());
    }

    @Test
    public void testEpicTaskGetEndTime() {
        LocalDateTime sub1DateTime = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(formatter)
                , formatter);
        LocalDateTime sub2DateTime = LocalDateTime.parse(LocalDateTime.now().plusHours(2).format(formatter)
                , formatter);
        Subtask sub1 = new Subtask("Test sub1", "Test sub1 desc", sub1DateTime.format(formatter)
                , 60);
        Subtask sub2 = new Subtask("Test sub2", "Test sub2 desc", sub2DateTime.format(formatter)
                , 60);
        taskManager.createEpicTask(epic);
        taskManager.createSubtask(epic.getId(), sub1);
        taskManager.createSubtask(epic.getId(), sub2);
        assertEquals(epic.getEndTime(), sub2DateTime.plusMinutes(60)
                , "Время завершения эпической задачи не совпадает");

        Epic epic2 = new Epic("Test epic2", "desc ep2");
        taskManager.createEpicTask(epic2);
        for (Subtask subtask : subtasks) {
            taskManager.createSubtask(epic2.getId(), subtask);
        }
        assertNull(epic2.getEndTime(), "Время завершения задачи не совпадает");
    }

    @Test
    public void testSubtaskGetEndTime() {
        LocalDateTime sub1DateTime = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(formatter)
                , formatter);
        Subtask sub = new Subtask("Test sub1", "Test sub1 desc", sub1DateTime.format(formatter)
                , 60);
        assertEquals(sub.getEndTime(), sub1DateTime.plusMinutes(60), "Время завершения подзадачи не совпадает");
        assertNull(subtasks.get(0).getEndTime(), "Время завершения задачи не совпадает");
    }
}
