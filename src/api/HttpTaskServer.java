package api;

import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import api.handler.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final int port;
    private final HttpServer server;

    public HttpTaskServer(int port, TaskManager tm) throws IOException {
        this.port = port;

        server = HttpServer.create(new InetSocketAddress(port), 0);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        server.createContext("/tasks", new TaskHandler(tm, gson));
        server.createContext("/epics", new EpicHandler(tm, gson));
        server.createContext("/subtasks", new SubtaskHandler(tm, gson));
        server.createContext("/history", new TaskHistoryHandler(tm, gson));
        server.createContext("/prioritized", new PrioritizedTaskHandler(tm, gson));
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer taskServer = new HttpTaskServer(8080, Managers.getDefault());
            taskServer.start();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Сервер запущен на порту " + port);
        server.start();
    }

    public void stop() {
        System.out.println("Сервер остановлен.");
        server.stop(0);
    }
}

