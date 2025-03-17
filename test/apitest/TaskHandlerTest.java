package apitest;

import api.HttpTaskServer;
import api.adapter.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Managers;
import manager.TaskManager;
import model.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class TaskHandlerTest {
    private final TaskManager tm = Managers.getDefault();
    private final HttpTaskServer server = new HttpTaskServer(8080, tm);
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final HttpClient client = HttpClient.newHttpClient();

    TaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        tm.removeAllTasks();
        tm.removeAllEpics();
        tm.removeAllSubtasks();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    @DisplayName("POST /tasks to create a task")
    void createTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", Status.NEW, Duration.ofDays(1),
                LocalDateTime.of(2021, 1, 1, 12, 0));

        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        int actualSize = tm.getAllTasks().size();
        int actualStatusCode = response.statusCode();
        System.out.println("++++++actual size ++++" + actualSize);
        Assertions.assertEquals(201, actualStatusCode);
        Assertions.assertEquals(1, actualSize);
    }

    @Test
    @DisplayName("GET /tasks to get all tasks")
    void getAllTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofDays(1),
                LocalDateTime.of(2021, 1, 1, 12, 0));
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS, Duration.ofDays(2),
                LocalDateTime.of(2021, 1, 2, 12, 0));

        tm.createTask(task1);
        tm.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        List<Task> tasks = gson.fromJson(response.body(), List.class);

        int actualStatusCode = response.statusCode();
        int actualSize = tasks.size();

        Assertions.assertEquals(200, actualStatusCode);
        Assertions.assertEquals(2, actualSize);
    }

    @Test
    @DisplayName("GET /tasks/{id} to get a task by id")
    void getTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", Status.NEW, Duration.ofDays(1),
                LocalDateTime.of(2021, 1, 1, 12, 0));
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS, Duration.ofDays(2),
                LocalDateTime.of(2021, 3, 2, 12, 0));

        tm.createTask(task);
        tm.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/2"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        Task actualTask = gson.fromJson(response.body(), Task.class);

        int actualStatusCode = response.statusCode();

        Assertions.assertEquals(200, actualStatusCode);
        Assertions.assertEquals(task2, actualTask);
    }

    @Test
    @DisplayName("DELETE /tasks/{id} to delete a task by id")
    void deleteTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", Status.NEW, Duration.ofDays(1),
                LocalDateTime.of(2021, 1, 1, 12, 0));
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS, Duration.ofDays(2),
                LocalDateTime.of(2021, 3, 2, 12, 0));

        tm.createTask(task);
        tm.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/2"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        int actualStatusCode = response.statusCode();
        int actualSize = tm.getAllTasks().size();

        Assertions.assertEquals(200, actualStatusCode);
        Assertions.assertEquals(1, actualSize);
    }

    @Test
    @DisplayName("DELETE /tasks to delete all tasks")
    void deleteAllTasksTest() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", Status.NEW, Duration.ofDays(1),
                LocalDateTime.of(2021, 1, 1, 12, 0));
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS, Duration.ofDays(2),
                LocalDateTime.of(2021, 3, 2, 12, 0));

        tm.createTask(task);
        tm.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        int actualStatusCode = response.statusCode();
        int actualSize = tm.getAllTasks().size();

        Assertions.assertEquals(200, actualStatusCode);
        Assertions.assertEquals(0, actualSize);
    }
}