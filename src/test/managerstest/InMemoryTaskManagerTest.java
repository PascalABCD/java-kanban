package managerstest;

import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {
    private static InMemoryTaskManager tm;
    private static Task task1;
    private static Task task2;
    private static Epic epic1;

    @BeforeEach
    void setUp() {
        tm = new InMemoryTaskManager();
        task1 = new Task("Task 1", "Description 1", NEW);
        task2 = new Task("Task 2", "Description 2", NEW);
        epic1 = new Epic("Epic 1", "Description epic 1");
    }

    @Test
    void checkIdGeneratorTest() {
        Task taskEqualId = new Task(1, "Task 3", "Description 3", NEW);
        tm.createTask(task1);
        tm.createTask(taskEqualId);

        final int taskId1 = task1.getId();
        final int taskIdEqual = taskEqualId.getId();

        assertEquals(1, taskId1);
        assertEquals(2, taskIdEqual);
    }

    @Test
    void getAllTasksTest() {
        tm.createTask(task1);
        tm.createTask(task2);

        String expectedTasks = "[Task{id=1, name='Task 1', description='Description 1', status=NEW}, Task{id=2, name='Task 2', description='Description 2', status=NEW}]";
        assertEquals(expectedTasks, tm.getAllTasks().toString());
    }

    @Test
    void createTaskTest() {
        tm.createTask(task1);

        String expectedTask = "Task{id=1, name='Task 1', description='Description 1', status=NEW}";
        assertEquals(expectedTask, task1.toString());
    }

    @Test
    void removeAllTasksTest() {
        tm.createTask(task1);
        tm.createTask(task2);

        tm.removeAllTasks();
        String expected = "[]";
        assertEquals(expected, tm.getAllTasks().toString());
    }

    @Test
    void updateEpicStatusTest() {
        tm.createEpic(epic1);
        Subtask subtask = new Subtask(epic1.getId(), "Subtask 1", "Description 1", NEW);

        epic1.addSubtask(subtask);
        tm.updateEpic(epic1);
        String epicStatus = epic1.getStatus().toString();

        assertEquals("NEW", epicStatus);
    }
}