package managerstest;

import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager hm;
    private InMemoryTaskManager tm;
    private Task task1;
    private Epic epic1;
    private Subtask subtask1;

    @BeforeEach
    void setUp() {
        hm = new InMemoryHistoryManager();
        tm = new InMemoryTaskManager();

        task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        epic1 = new Epic(3, "Epic 1", "Description epic 1", Status.NEW);
        subtask1 = new Subtask(epic1.getId(), 5, "SubTask 1", "Description subtask 1", Status.NEW);

    }

    @Test
    void addTaskTest() {

        hm.add(task1);
        hm.add(epic1);
        hm.add(subtask1);

        String expected = "[Task{id=1, name='Task 1', description='Description 1', status=NEW}, " +
                "Epic{id=3, name='Epic 1', description='Description epic 1', status=NEW, subtasksList=[]}, " +
                "Subtask{id=5, name='SubTask 1', description='Description subtask 1', status=NEW, epicId=3}]";
        String actual = hm.getHistory().toString();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getHistory3TasksTest() {
        tm.createTask(task1);
        tm.createEpic(epic1);

        Subtask subtask2 = new Subtask(epic1.getId(), "Subtask", "Description", Status.NEW);
        tm.createSubtask(subtask2);

        tm.getTaskById(1);
        tm.getEpicById(2);
        tm.getSubtaskById(3);

        String expected = "[Task{id=1, name='Task 1', description='Description 1', status=NEW}, " +
                "Epic{id=2, name='Epic 1', description='Description epic 1', status=NEW, subtasksList=[Subtask{id=3, name='Subtask', description='Description', status=NEW, epicId=2}]}, " +
                "Subtask{id=3, name='Subtask', description='Description', status=NEW, epicId=2}]";

        String actual = tm.getHistory().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getEmptyHistoryTest() {
        String expected = "[]";
        String actual = hm.getHistory().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void checkHistorySizeTest() {
        for (int i = 1; i < 5; i++) {
            Task task = new Task("Task " + i, "Description " + i, Status.NEW);
            tm.createTask(task);
            tm.getTaskById(i);
        }

        for (int i = 5; i < 10; i++) {
            Epic epic = new Epic("Epic " + i, "Description epic " + i);
            tm.createEpic(epic);
            tm.getEpicById(i);
        }

        for (int i = 10; i < 12; i++) {
            Subtask subtask = new Subtask(5, "Subtask " + i, "Description " + i, Status.NEW);
            tm.createSubtask(subtask);
            tm.getSubtaskById(i);
        }

        String expected = "[Task{id=1, name='Task 1', description='Description 1', status=NEW}, " +
                "Task{id=2, name='Task 2', description='Description 2', status=NEW}, " +
                "Task{id=3, name='Task 3', description='Description 3', status=NEW}, " +
                "Task{id=4, name='Task 4', description='Description 4', status=NEW}, " +
                "Epic{id=5, name='Epic 5', description='Description epic 5', status=NEW, subtasksList=[Subtask{id=10, name='Subtask 10', description='Description 10', status=NEW, epicId=5}, Subtask{id=11, name='Subtask 11', description='Description 11', status=NEW, epicId=5}]}, " +
                "Epic{id=6, name='Epic 6', description='Description epic 6', status=NEW, subtasksList=[]}, " +
                "Epic{id=7, name='Epic 7', description='Description epic 7', status=NEW, subtasksList=[]}, " +
                "Epic{id=8, name='Epic 8', description='Description epic 8', status=NEW, subtasksList=[]}, " +
                "Epic{id=9, name='Epic 9', description='Description epic 9', status=NEW, subtasksList=[]}, " +
                "Subtask{id=10, name='Subtask 10', description='Description 10', status=NEW, epicId=5}, " +
                "Subtask{id=11, name='Subtask 11', description='Description 11', status=NEW, epicId=5}]";

        String actual = tm.getHistory().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void checkHistoryNotContainRepeatedTasksTest() {
        hm.add(task1);
        hm.add(epic1);
        hm.add(subtask1);

        hm.add(task1);
        hm.add(epic1);
        hm.add(subtask1);

        String expected = "[Task{id=1, name='Task 1', description='Description 1', status=NEW}, " +
                "Epic{id=3, name='Epic 1', description='Description epic 1', status=NEW, subtasksList=[]}, " +
                "Subtask{id=5, name='SubTask 1', description='Description subtask 1', status=NEW, epicId=3}]";

        Assertions.assertEquals(expected, hm.getHistory().toString());
    }

    @Test
    void checkHistoryNotContainRemovedTaskTest() {
        Subtask subtask2 = new Subtask(epic1.getId(), "Subtask 2", "Description 2", Status.NEW);
        hm.add(subtask1);
        hm.add(subtask2);
        hm.remove(subtask2.getId());

        String expected = "[Subtask{id=5, name='SubTask 1', description='Description subtask 1', status=NEW, epicId=3}]";
        Assertions.assertEquals(expected, hm.getHistory().toString());
    }

    @Test
    void checkHistoryNotContainDeletedEpicWithSubtaskTest() {
        Epic epic1 = new Epic("Epic 1", "Description epic 1");
        tm.createEpic(epic1);
        Subtask subtask2 = new Subtask(epic1.getId(), "Subtask 2", "Description 2", Status.NEW);
        tm.createSubtask(subtask2);

        tm.getEpicById(epic1.getId());
        tm.getSubtaskById(subtask2.getId());

        tm.removeAllEpics();

        String expected = "[]";

        Assertions.assertEquals(expected, tm.getHistory().toString());
    }

    @Test
    void checkHistoryNotContainAllRemovedTasksTest() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        tm.createTask(task1);

        tm.getTaskById(1);
        tm.removeAllTasks();

        String expected = "[]";

        Assertions.assertEquals(expected, hm.getHistory().toString());
    }
}