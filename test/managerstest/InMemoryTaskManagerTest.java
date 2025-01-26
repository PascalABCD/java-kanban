package managerstest;

import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryTaskManagerTest {
    private static InMemoryTaskManager tm;
    private static Task task1;
    private static Task task2;
    private static Epic epic1;

    @BeforeEach
    void setUp() {
        tm = new InMemoryTaskManager();
        task1 = new Task("Task 1", "Description 1", Status.NEW);
        task2 = new Task("Task 2", "Description 2", Status.NEW);
        epic1 = new Epic("Epic 1", "Description epic 1");
    }

    @Test
    void checkIdGeneratorTest() {
        Task taskEqualId = new Task(1, "Task 3", "Description 3", Status.NEW);
        tm.createTask(task1);
        tm.createTask(taskEqualId);

        final int taskId1 = task1.getId();
        final int taskIdEqual = taskEqualId.getId();

        Assertions.assertEquals(1, taskId1);
        Assertions.assertEquals(2, taskIdEqual);
    }

    @Test
    void getAllTasksTest() {
        tm.createTask(task1);
        tm.createTask(task2);

        String expectedTasks = "[Task{id=1, name='Task 1', description='Description 1', status=NEW}, Task{id=2, name='Task 2', description='Description 2', status=NEW}]";
        Assertions.assertEquals(expectedTasks, tm.getAllTasks().toString());
    }

    @Test
    void createTaskTest() {
        tm.createTask(task1);

        String expectedTask = "Task{id=1, name='Task 1', description='Description 1', status=NEW}";
        Assertions.assertEquals(expectedTask, task1.toString());
    }

    @Test
    void removeAllTasksTest() {
        tm.createTask(task1);
        tm.createTask(task2);

        tm.removeAllTasks();
        String expected = "[]";
        Assertions.assertEquals(expected, tm.getAllTasks().toString());
    }

    @Test
    void updateEpicStatusTest() {
        tm.createEpic(epic1);
        Subtask subtask = new Subtask(epic1.getId(), "Subtask 1", "Description 1", Status.NEW);

        epic1.addSubtask(subtask);
        tm.updateEpic(epic1);
        String epicStatus = epic1.getStatus().toString();

        Assertions.assertEquals("NEW", epicStatus);
    }
}