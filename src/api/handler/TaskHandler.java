package api.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerSaveException;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager tm, Gson gson) {
        super(tm, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String method = h.getRequestMethod();
        String[] path = h.getRequestURI().getPath().split("/");

        switch (method) {
            case "GET" -> {
                if (path.length == 3) {
                    getTaskById(h);
                } else {
                    getAllTasks(h);
                }
            }
            case "POST" -> {
                if (path.length == 3) {
                    updateTask(h);
                } else {
                    createTask(h);
                }
            }
            case "DELETE" -> {
                if (path.length == 3) {
                    deleteTaskById(h);
                } else {
                    deleteAllTasks(h);
                }
            }
        }
    }

    private void getAllTasks(HttpExchange h) throws IOException {
        List<Task> tasks = tm.getAllTasks();
        String response = gson.toJson(tasks);
        sendResponse(h, response, 200);
    }

    private void getTaskById(HttpExchange h) throws IOException {
        Optional<Integer> id = getId(h);

        if (id.isPresent()) {
            Task task = tm.getTaskById(id.get());
            if (task != null) {
                String response = gson.toJson(task);
                sendResponse(h, response, 200);
            } else {
                sendNotFound(h);
            }
        } else {
            sendInternalServerError(h);
        }
    }

    private void createTask(HttpExchange h) throws IOException {
        String requestBody = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JsonElement element = JsonParser.parseString(requestBody);
        JsonObject jsonObject = element.getAsJsonObject();
        Task task = gson.fromJson(jsonObject, Task.class);
        System.out.println(task);

        try {
            tm.createTask(task);
            sendResponse(h, gson.toJson(task), 201);
        } catch (ManagerSaveException e) {
            sendHasInteractions(h);
        }
    }

    private void updateTask(HttpExchange h) throws IOException {
        String requestBody = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JsonElement element = JsonParser.parseString(requestBody);
        JsonObject jsonObject = element.getAsJsonObject();
        Task task = gson.fromJson(jsonObject, Task.class);

        try {
            tm.updateTask(task);
            sendResponse(h, gson.toJson(task), 201);
        } catch (ManagerSaveException e) {
            sendNotFound(h);
        }
    }

    private void deleteTaskById(HttpExchange h) throws IOException {
        Optional<Integer> id = getId(h);

        if (id.isPresent()) {
            tm.removeTask(id.get());
            sendResponse(h, "Задача удалена", 200);
        } else {
            sendNotFound(h);
        }
    }

    private void deleteAllTasks(HttpExchange h) throws IOException {
        try {
            tm.removeAllTasks();
            sendResponse(h, "Все задачи удалены", 200);
        } catch (IOException e) {
            sendInternalServerError(h);
        }
    }
}
