package taskTests;

import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private static Subtask subtask;

    @BeforeEach
    void setUp() {
        subtask = new Subtask(1, "Test Subtask", "Test Subtask description", NEW);
    }

    @Test
    public void getEpicTest() {
        int actualId = subtask.getEpicId();
        int expectedId = 1;

        assertEquals(expectedId, actualId);
    }
}