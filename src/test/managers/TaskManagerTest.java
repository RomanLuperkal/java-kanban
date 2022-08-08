package test.managers;

import exceptions.TaskDateDurationException;
import managers.FileBackedTasksManager;
import managers.InMemoryTaskManager;
import managers.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected SimpleTask task1;
    protected SimpleTask task2;
    protected Epic epic1;
    protected Epic epic2;
    protected Subtask subtask1;
    protected Subtask subtask2;
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @BeforeEach
    public void initializationTasks() {
        task1 = new SimpleTask("Test task1", "Test task1 desc", "01.01.2023 12:00"
                , 60);
        task2 = new SimpleTask("Test task2", "Test task2 desc", "01.01.2023 13:00"
                , 60);
        subtask1 = new Subtask("Test sub1", "Test sub1 desc", "01.01.2023 14:00", 60);
        subtask2 = new Subtask("Test sub2", "Test sub2 desc", "01.01.2023 15:00", 60);
        epic1 = new Epic("Test epic1", "Test epic1 desc");
        epic2 = new Epic("Test epic2", "Test epic2 desc");
    }

    @BeforeEach
    public void initializationTaskManager() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<T> type = (Class<T>) superClass.getActualTypeArguments()[0];

        if (type == InMemoryTaskManager.class) {
            manager = (T) new InMemoryTaskManager();
        }
        if (type == FileBackedTasksManager.class) {
            File file = new File("test_save.csv");
            manager = (T) new FileBackedTasksManager(file);
        }

    }

    @Test
    public void testAddNewSimpleTask() {
        manager.createSimpleTask(task1);
        final Task createdTask = manager.getSimpleTask(task1.getId());
        assertEquals(task1, createdTask, "Задачи не совпадают");
        final List<SimpleTask> tasks = manager.getSimpleTasks();
        assertEquals(1, tasks.size(), "Неверное кол-во задач");
        assertEquals(task1, tasks.get(0), "Задачи в списке не совпадают");
    }

    @Test
    public void shouldIllegalStateExceptionWhenAddExistingSimpleTask() {
        manager.createSimpleTask(task1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> manager.createSimpleTask(task1));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void shouldIllegalStateExceptionWhenListSimpleTasksIsEmptyAndWeGetAllSimpleTasks() {
        IllegalStateException e = assertThrows(IllegalStateException.class, manager::getSimpleTasks);
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void shouldIllegalStateExceptionWhenWeGetSimpleTaskOnBadIncorrectId() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> manager.getSimpleTask(33));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
        manager.createSimpleTask(task1);
        e = assertThrows(IllegalStateException.class, () -> manager.getSimpleTask(33));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void testAddNewEpic() {
        manager.createEpicTask(epic1);
        Epic createdEpic = manager.getEpicTask(epic1.getId());
        assertEquals(epic1, createdEpic);
        List<Epic> epics = manager.getEpicTasks();
        assertEquals(1, epics.size(), "Неверное кол-во задач");
        assertEquals(epic1, epics.get(0), "Задачи в списке не совпадают");
    }

    @Test
    public void shouldIllegalStateExceptionWhenAddExistingEpic() {
        manager.createEpicTask(epic1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> manager.createEpicTask(epic1));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void shouldIllegalStateExceptionWhenListEpicTasksIsEmptyAndWeGetAllEpicTasks() {
        IllegalStateException e = assertThrows(IllegalStateException.class, manager::getEpicTasks);
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void shouldIllegalStateExceptionWhenWeGetEpicOnBadIncorrectId() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> manager.getEpicTask(34));
        //assertEquals("Список эпических задач пуст.", e.getMessage());
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
        manager.createEpicTask(epic1);
        e = assertThrows(IllegalStateException.class, () -> manager.getEpicTask(33));
        //assertEquals("Такой эпической задачи нет", e.getMessage());
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void testAddNewSubtask() {
        manager.createEpicTask(epic1);
        manager.createSubtask(epic1.getId(), subtask1);
        Subtask createdSub = manager.getSubtask(subtask1.getId());
        assertEquals(subtask1, createdSub);
        List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(1, subtasks.size(), "Неверное кол-во задач");
        assertEquals(subtask1, subtasks.get(0), "Задачи в списке не совпадают");
        subtasks = manager.getListSubtasksByEpicTaskId(epic1.getId());
        assertEquals(1, subtasks.size(), "Неверное кол-во задач");
        assertEquals(subtask1, subtasks.get(0), "Задачи в списке не совпадают");
    }

    @Test
    public void shouldIllegalStateExceptionWhenAddExistingSubtask() {
        manager.createEpicTask(epic1);
        manager.createSubtask(epic1.getId(), subtask1);
        IllegalStateException e = assertThrows(IllegalStateException.class
                , () -> manager.createSubtask(epic1.getId(), subtask1));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void shouldIllegalStateExceptionWhenCreateSubAndNoEpic() {
        IllegalStateException e = assertThrows(IllegalStateException.class
                , () -> manager.createSubtask(7, subtask1));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void shouldIllegalStateExceptionWhenListSubtasksIsEmptyAndWeGetAllSubtasks() {
        IllegalStateException e = assertThrows(IllegalStateException.class, manager::getSubtasks);
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void shouldIllegalStateExceptionWhenWeGetSubtaskOnBadIncorrectId() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> manager.getSubtask(34));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
        manager.createEpicTask(epic1);
        manager.createSubtask(epic1.getId(), subtask1);
        e = assertThrows(IllegalStateException.class, () -> manager.getSubtask(34));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void shouldIllegalStateExceptionWhenGetListSubtasksByIncorrectEpicTaskId() {
        IllegalStateException e = assertThrows(IllegalStateException.class
                , () -> manager.getListSubtasksByEpicTaskId(45));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
        manager.createEpicTask(epic1);
        manager.createSubtask(epic1.getId(), subtask1);
        e = assertThrows(IllegalStateException.class
                , () -> manager.getListSubtasksByEpicTaskId(45));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void testDeleteSimpleTasks() {
        manager.createSimpleTask(task1);
        manager.deleteSimpleTask(task1.getId());
        IllegalStateException e = assertThrows(IllegalStateException.class
                , () -> manager.getSimpleTask(task1.getId()));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
        manager.createSimpleTask(task1);
        manager.createSimpleTask(task2);
        manager.deleteSimpleTasks();
        e = assertThrows(IllegalStateException.class, manager::getSimpleTasks);
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void testDeleteEpicTasks() {
        manager.createEpicTask(epic1);
        manager.deleteEpicTask(epic1.getId());
        IllegalStateException e = assertThrows(IllegalStateException.class
                , () -> manager.getEpicTask(epic1.getId()));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
        manager.createEpicTask(epic1);
        manager.createEpicTask(epic2);
        manager.deleteEpicTasks();
        e = assertThrows(IllegalStateException.class
                , manager::getEpicTasks);
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void testDeleteSubtasks() {
        manager.createEpicTask(epic1);
        manager.createSubtask(epic1.getId(), subtask1);
        manager.deleteSubtask(subtask1.getId());
        IllegalStateException e = assertThrows(IllegalStateException.class
                , () -> manager.getSubtask(subtask1.getId()));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
        manager.createSubtask(epic1.getId(), subtask1);
        manager.createSubtask(epic1.getId(), subtask2);
        manager.deleteSubtasks();
        e = assertThrows(IllegalStateException.class
                , manager::getSubtasks);
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }

    @Test
    public void testUpdateSimpleTask() {
        manager.createSimpleTask(task1);
        task1.changeStatus(Status.IN_PROGRESS);
        manager.updateSimpleTask(task1);
        SimpleTask updateTask = manager.getSimpleTask(task1.getId());
        assertEquals(Status.IN_PROGRESS, updateTask.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    public void testUpdateEpic() {
        manager.createEpicTask(epic1);
        epic1.changeStatus(Status.IN_PROGRESS);
        manager.updateEpicTask(epic1);
        Epic updateTask = manager.getEpicTask(epic1.getId());
        assertEquals(Status.IN_PROGRESS, updateTask.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    public void testUpdateSubtasks() {
        manager.createEpicTask(epic1);
        manager.createSubtask(epic1.getId(), subtask1);
        manager.createSubtask(epic1.getId(), subtask2);
        subtask1.changeStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        Epic updateEpic = manager.getEpicTask(epic1.getId());
        Subtask updateSub1 = manager.getSubtask(subtask1.getId());
        assertEquals(Status.IN_PROGRESS, updateSub1.getStatus(), "Статусы задач не совпадают");
        assertEquals(Status.IN_PROGRESS, updateEpic.getStatus(), "Статусы задач не совпадают");

        subtask2.changeStatus(Status.DONE);
        manager.updateSubtask(subtask2);
        Subtask updateSub2 = manager.getSubtask(subtask2.getId());
        assertEquals(Status.DONE, updateSub2.getStatus(), "Статусы задач не совпадают");
        assertEquals(Status.IN_PROGRESS, updateEpic.getStatus(), "Статусы задач не совпадают");

        subtask1.changeStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        assertEquals(Status.DONE, updateEpic.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    public void shouldTasksDateDurationExceptionWhenDateTasksEqual() {
        LocalDateTime dateTime = LocalDateTime.now();
        SimpleTask testTask1 = new SimpleTask("testTask1", "testTask1 desc"
                , dateTime.plusHours(1).format(formatter), 60);
        SimpleTask testTask2 = new SimpleTask("testTask2", "testTask2 desc"
                , dateTime.plusHours(1).format(formatter), 60);
        manager.createSimpleTask(testTask1);
        TaskDateDurationException e = assertThrows(TaskDateDurationException.class
                , () -> manager.createSimpleTask(testTask2));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");

        Subtask testSub1 = new Subtask("testSub1", "testSub1 desc"
                , dateTime.plusHours(2).format(formatter), 60);
        Subtask testSub2 = new Subtask("testSub2", "testSub2 desc"
                , dateTime.plusHours(2).format(formatter), 60);
        manager.createEpicTask(epic1);
        manager.createSubtask(epic1.getId(), testSub1);
        e = assertThrows(TaskDateDurationException.class, () -> manager.createSubtask(epic1.getId(), testSub2));
        assertNotNull(e.getMessage(), "Отсутствует сообщение об ошибке");
        assertFalse(e.getMessage().isBlank(), "Сообщение об ошибке пустое");
    }
}
