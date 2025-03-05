package tasktests;

import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class EpicTest {
    private static Epic epic;
    private static Subtask subtask;
    private static Subtask subtask2;

    @BeforeEach
    void setUp() {
        epic = new Epic(1, "Test Epic", "Test Epic description", Status.NEW, Duration.ofHours(1), LocalDateTime.now());
        subtask = new Subtask(1, "Test subtask", "Test subtask description", Status.NEW, Duration.ofHours(1), LocalDateTime.now());
        subtask2 = new Subtask(2, "Test subtask2 2", "Test subtask2 description 2", Status.NEW, Duration.ofDays(1), LocalDateTime.now().plus(Duration.ofHours(1)));
    }

    @Test
    public void getSubtaskListTest() {
        epic.addSubtask(subtask);
        epic.addSubtask(subtask2);

        List<Subtask> subtasks = epic.getSubtasksList();
        List<Subtask> expected = List.of(subtask, subtask2);

        Assertions.assertEquals(expected, subtasks);
    }

    @Test
    public void removeSubtaskByIdTest() {
        epic.addSubtask(subtask);
        epic.addSubtask(subtask2);

        epic.removeSubtaskById(subtask);

        List<Subtask> subtasks = epic.getSubtasksList();
        List<Subtask> expected = List.of(subtask2);

        Assertions.assertEquals(expected, subtasks);
    }
}