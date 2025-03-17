package managerstest;

import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class TaskManagerTest<T extends TaskManager>{
    protected abstract T createManager();

    protected TaskManager taskManager = createManager();
    private static Task task1;
    private static Task task2;
    private static Epic epic1;

    @BeforeEach
    void setUp() {
        task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofHours(5), LocalDateTime.of(2021, 1, 1, 1, 1));
        task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofHours(5), LocalDateTime.of(2021, 2, 1, 1, 1));
        epic1 = new Epic(1,"Epic 1", "Description epic 1", Status.NEW, Duration.ofHours(5), LocalDateTime.of(2021, 3, 1, 1, 1));
    }

    @Test
    void checkIdGeneratorTest() {
        Task taskEqualId = new Task(1, "Task 3", "Description 3", Status.NEW, Duration.ofHours(5), LocalDateTime.of(2021, 1, 1, 1, 1));
        taskManager.createTask(task1);
        taskManager.createTask(taskEqualId);

        final int taskId1 = task1.getId();
        final int taskIdEqual = taskEqualId.getId();

        Assertions.assertEquals(1, taskId1);
        Assertions.assertEquals(2, taskIdEqual);
    }

    @Test
    void getAllTasksTest() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        String expectedTasks = "[Task{id=1, name='Task 1', description='Description 1', status=NEW, duration=PT5H, startTime=2021-01-01T01:01}, Task{id=2, name='Task 2', description='Description 2', status=NEW, duration=PT5H, startTime=2021-02-01T01:01}]";
        Assertions.assertEquals(expectedTasks, taskManager.getAllTasks().toString());
    }

    @Test
    void createTaskTest() {
        taskManager.createTask(task1);

        String expectedTask = "Task{id=1, name='Task 1', description='Description 1', status=NEW, duration=PT5H, startTime=2021-01-01T01:01}";
        Assertions.assertEquals(expectedTask, task1.toString());
    }

    @Test
    void removeAllTasksTest() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.removeAllTasks();
        String expected = "[]";
        Assertions.assertEquals(expected, taskManager.getAllTasks().toString());
    }

    @Test
    void updateEpicStatusTest() {
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask(epic1.getId(), "Subtask 1", "Description 1", Status.NEW, Duration.ofHours(5), LocalDateTime.of(2021, 1, 8, 1, 1));

        epic1.addSubtask(subtask);
        taskManager.updateEpic(epic1);
        String epicStatus = epic1.getStatus().toString();

        Assertions.assertEquals("NEW", epicStatus);
    }

    @Test
    void checkPriorityTest() {
        taskManager.createTask(task2);
        taskManager.createTask(task1);

        String expectedTasks = "[Task{id=2, name='Task 1', description='Description 1', status=NEW, duration=PT5H, startTime=2021-01-01T01:01}, " +
                "Task{id=1, name='Task 2', description='Description 2', status=NEW, duration=PT5H, startTime=2021-02-01T01:01}]";
        String actualTasks = taskManager.getPrioritizedTasks().toString();
        Assertions.assertEquals(expectedTasks, actualTasks);
    }
}