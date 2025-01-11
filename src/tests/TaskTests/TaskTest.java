package tests.TaskTests;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    private static Task task;
    private static Task task2;

    @BeforeEach
    void setUp() {
        task = new Task(1, "Test checkTasksNotEqual", "Test checkTasksNotEqual description", NEW);
        task2 = new Task(1, "Test checkTasksNotEqual2", "Test checkTasksNotEqual2 description", NEW);
    }

    @Test
    public void checkTasksEqualWhenEqualIdTest() {
        boolean result = task.equals(task2);
        assertTrue(result, "Задачи должны быть равны");
    }
}