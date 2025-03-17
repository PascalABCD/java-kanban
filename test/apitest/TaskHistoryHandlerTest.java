package apitest;

import api.HttpTaskServer;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskHistoryHandlerTest {
    private final TaskManager tm = Managers.getDefault();
    private final HttpTaskServer server = new HttpTaskServer(8080, tm);
    private final HttpClient client = HttpClient.newHttpClient();

    TaskHistoryHandlerTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        tm.removeAllTasks();
        tm.removeAllEpics();
        tm.removeAllSubtasks();
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void getTaskHistoryTest() throws IOException, InterruptedException {
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofHours(5), LocalDateTime.of(2021, 1, 1, 1, 1));
        Epic epic = new Epic(2, "Epic 1", "Description epic 1", Status.NEW, Duration.ofHours(5), LocalDateTime.of(2021, 2, 2, 2, 2));

        tm.createTask(task);
        tm.createEpic(epic);

        Subtask subtask = new Subtask(epic.getId(), "Subtask", "Description", Status.NEW, Duration.ofHours(5), LocalDateTime.of(2021, 3, 3, 18, 17));
        tm.createSubtask(subtask);

        tm.getTaskById(1);
        tm.getEpicById(2);
        tm.getSubtaskById(3);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        assertEquals(200, statusCode);

    }

}