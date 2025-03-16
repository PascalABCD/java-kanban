package apitest;

import api.HttpTaskServer;
import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;

class EpicHandlerTest {
    private final HttpTaskServer server = new HttpTaskServer();
    private final TaskManager tm = server.getManager();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final HttpClient client = HttpClient.newHttpClient();

    EpicHandlerTest() throws IOException {
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
    void createEpicTest() {

    }

    @Test
    void getEpicTest() {

    }

    @Test
    void getEpicSubtasksTest() {

    }

    @Test
    void deleteEpicTest() {

    }

}