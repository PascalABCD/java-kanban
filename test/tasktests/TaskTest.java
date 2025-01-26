package tasktests;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskTest {
    private static Task task;
    private static Task task2;

    @BeforeEach
    void setUp() {
        task = new Task(1, "Test checkTasksNotEqual", "Test checkTasksNotEqual description", Status.NEW);
        task2 = new Task(1, "Test checkTasksNotEqual2", "Test checkTasksNotEqual2 description", Status.IN_PROGRESS);
    }

    @Test
    public void checkTasksEqualWhenEqualIdTest() {
        boolean result = task.equals(task2);
        Assertions.assertTrue(result);
    }
}