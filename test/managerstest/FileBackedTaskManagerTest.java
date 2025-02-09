package managerstest;

import manager.FileBackedTaskManager;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    FileBackedTaskManager fm;
    File testFile;
    Task task1;
    Task task2;
    Epic epic1;
    Epic epic2;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    void setUp() {
        try {
            testFile = File.createTempFile("test", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fm = new FileBackedTaskManager(testFile);

        task1 = new Task(1, "Task1", "Description1", Status.NEW);
        task2 = new Task(2, "Task2", "Description2", Status.IN_PROGRESS);

        epic1 = new Epic(3, "Epic1", "Description1", Status.NEW);
        epic2 = new Epic(4, "Epic2", "Description2", Status.IN_PROGRESS);

        subtask1 = new Subtask(3, 1, "Subtask1", "Description1", Status.NEW);
        subtask2 = new Subtask(3, 2, "Subtask2", "Description2", Status.IN_PROGRESS);
    }

    @AfterEach
    void tearDown() {
        testFile.delete();
    }

    @Test
    void emptyFileTest() {
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(0, manager.getAllTasks().size());
        assertEquals(0, manager.getAllEpics().size());
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    void saveAndLoadTasks() {
        Task task1 = new Task("Task", "Description", Status.NEW);
        Epic epic1 = new Epic("Epic", "Description");
        fm.createTask(task1);
        fm.createEpic(epic1);

        Subtask subTask1 = new Subtask(epic1.getId(), "Subtask", "Description", Status.IN_PROGRESS);
        fm.createSubtask(subTask1);
        fm.updateEpic(epic1);

        List<String> lines;
        try {
            lines = Files.readAllLines(testFile.toPath());
            assertEquals("id,type,name,status,description,epic", lines.get(0));
            assertEquals("1,TASK,Task,NEW,Description", lines.get(1));
            assertEquals("3,SUBTASK,Subtask,IN_PROGRESS,Description,2", lines.get(2));
            assertEquals("2,EPIC,Epic,IN_PROGRESS,Description", lines.get(3));
        } catch (IOException e) {
            System.out.println("Error reading file");
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadTasksTest() {
        File testFile = new File("test/managerstest/testData.csv");
        fm = FileBackedTaskManager.loadFromFile(testFile);

        String expectedTasks = "[Task{id=1, name='Task1', description='Description1', status=NEW}, " +
                "Task{id=3, name='Task2', description='Description2', status=NEW}]";

        String expectedEpics = "[Epic{id=2, name='Epic1', description='DescriptionEpic1', status=NEW, " +
                "subtasksList=[Subtask{id=4, name='Subtask1', description='DescriptionSubtask1', status=NEW, epicId=2}]}]";

        String expectedSubtasks = "[Subtask{id=4, name='Subtask1', description='DescriptionSubtask1', status=NEW, epicId=2}]";

        String actual = fm.getAllTasks().toString() + fm.getAllEpics().toString() + fm.getAllSubtasks().toString();
        String expected = expectedTasks + expectedEpics + expectedSubtasks;
        assertEquals(expected, actual);
    }

    @Test
    void removeTaskAndCheckFileContentTest() {
        fm.createTask(task1);
        fm.createTask(task2);

        fm.removeTask(task1);

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        List<Task> tasks = manager.getAllTasks();

        assertEquals(1, tasks.size());
        assertEquals(task2, tasks.getFirst());
    }
}