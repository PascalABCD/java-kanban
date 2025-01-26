package tasktests;

import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    private static Subtask subtask;

    @BeforeEach
    void setUp() {
        subtask = new Subtask(1, "Test Subtask", "Test Subtask description", Status.NEW);
    }

    @Test
    public void getEpicTest() {
        int actualId = subtask.getEpicId();
        int expectedId = 1;

        Assertions.assertEquals(expectedId, actualId);
    }
}