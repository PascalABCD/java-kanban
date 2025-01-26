package test.TaskTests;

import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static Epic epic;
    private static Subtask subtask;
    private static Subtask subtask2;

    @BeforeEach
    void setUp() {
        epic = new Epic(1, "Test Epic", "Test Epic description", NEW);
        subtask = new Subtask(1, "Test subtask", "Test subtask description", NEW);
        subtask2 = new Subtask(2, "Test subtask2 2", "Test subtask2 description 2", NEW);
    }

    @Test
    public void getSubtaskListTest() {
        epic.addSubtask(subtask);
        epic.addSubtask(subtask2);

        List<Subtask> subtasks = epic.getSubtasksList();
        List<Subtask> expected = List.of(subtask, subtask2);

        assertEquals(expected, subtasks);
    }

    @Test
    public void removeSubtaskByIdTest() {
        epic.addSubtask(subtask);
        epic.addSubtask(subtask2);

        epic.removeSubtaskById(subtask);

        List<Subtask> subtasks = epic.getSubtasksList();
        List<Subtask> expected = List.of(subtask2);

        assertEquals(expected, subtasks);
    }
}