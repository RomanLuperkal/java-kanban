package test.managers;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {


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
