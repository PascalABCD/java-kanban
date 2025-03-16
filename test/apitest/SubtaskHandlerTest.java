package apitest;

import api.HttpTaskServer;
import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskHandlerTest {
    private final HttpTaskServer server = new HttpTaskServer();
    private final TaskManager tm = server.getManager();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final HttpClient client = HttpClient.newHttpClient();

    SubtaskHandlerTest() throws IOException {
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
    @DisplayName("POST /subtasks to create a subtask")
    void createSubtaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description 1", Status.NEW);
        tm.createEpic(epic);
        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, Duration.ofDays(1),
                LocalDateTime.of(2021, 1, 1, 12, 0));

        String json = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        assertEquals(201, response.statusCode());
        assertEquals(1, tm.getAllSubtasks().size());
    }

    @Test
    @DisplayName("GET /subtasks to get all subtasks")
    void getAllSubtasksTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description 1", Status.NEW);
        tm.createEpic(epic);
        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, Duration.ofDays(1),
                LocalDateTime.of(2021, 1, 1, 12, 0));
        tm.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask 1"));
    }

    @Test
    @DisplayName("GET /subtasks/{id} to get a subtask by id")
    void getSubtaskByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description 1", Status.NEW);
        tm.createEpic(epic);
        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, Duration.ofDays(1),
                LocalDateTime.of(2021, 1, 1, 12, 0));
        tm.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask 1"));
    }

    @Test
    @DisplayName("DELETE /subtasks/{id} to delete a subtask by id")
    void deleteSubtaskByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description 1", Status.NEW);
        tm.createEpic(epic);
        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, Duration.ofDays(1),
                LocalDateTime.of(2021, 1, 1, 12, 0));
        tm.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        assertEquals(200, response.statusCode());
        assertEquals(0, tm.getAllSubtasks().size());
    }

    @Test
    @DisplayName("DELETE /subtasks to delete a subtask by id")
    void deleteAllSubtasksTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description 1", Status.NEW);
        tm.createEpic(epic);
        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, Duration.ofDays(1),
                LocalDateTime.of(2021, 1, 1, 12, 0));
        tm.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        assertEquals(200, response.statusCode());
        assertEquals(0, tm.getAllSubtasks().size());
    }
}