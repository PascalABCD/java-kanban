package api.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager tm, Gson gson) {
        super(tm, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String method = h.getRequestMethod();
        String[] path = h.getRequestURI().getPath().split("/");

        switch (method) {
            case "GET" -> {
                if (path.length == 3) {
                    getEpicById(h);
                } else if (path.length == 4) {
                    getEpicSubtasks(h);
                } else {
                    getAllEpics(h);
                }
            }
            case "DELETE" -> {
                if (path.length == 3) {
                    deleteEpicById(h);
                } else {
                    sendNotFound(h);
                }
            }
            case "POST" -> {
                if (path.length == 3) {
                    updateEpic(h);
                } else {
                    createEpic(h);
                }
            }
        }
    }

    private void getAllEpics(HttpExchange h) throws IOException {
        List<Epic> epics = tm.getAllEpics();
        String response = gson.toJson(epics);
        sendResponse(h, response, 200);

    }

    private void getEpicById(HttpExchange h) throws IOException {
        Optional<Integer> id = getId(h);

        if (id.isPresent()) {
            Epic epic = tm.getEpicById(id.get());
            if (epic != null) {
                String response = gson.toJson(epic);
                sendResponse(h, response, 200);
            } else {
                sendNotFound(h);
            }
        } else {
            sendInternalServerError(h);
        }

    }

    private void getEpicSubtasks(HttpExchange h) throws IOException {
        String[] path = h.getRequestURI().getPath().split("/");
        Optional<String> parameter = Optional.of(path[3]);

        Optional<Integer> id = getId(h);

        if (parameter.get().equals("subtasks")) {
            if (id.isPresent()) {
                Epic epic = tm.getEpicById(id.get());
                if (epic != null) {
                    List<Subtask> subtasks = epic.getSubtasksList();
                    String response = gson.toJson(subtasks);
                    sendResponse(h, response, 200);
                } else {
                    sendNotFound(h);
                }
            } else {
                sendInternalServerError(h);
            }
        } else {
            sendNotFound(h);
        }
    }

    private void createEpic(HttpExchange h) throws IOException {
        String requestBody = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JsonElement element = JsonParser.parseString(requestBody);
        JsonObject jsonObject = element.getAsJsonObject();
        Epic epic = gson.fromJson(jsonObject, Epic.class);
        System.out.println(epic);

        try {
            tm.createEpic(epic);
            sendResponse(h, gson.toJson(epic), 201);
        } catch (Exception e) {
            sendInternalServerError(h);
        }
    }

    private void updateEpic(HttpExchange h) throws IOException {
        String requestBody = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JsonElement element = JsonParser.parseString(requestBody);
        JsonObject jsonObject = element.getAsJsonObject();
        Epic epic = gson.fromJson(jsonObject, Epic.class);

        try {
            tm.updateEpic(epic);
            sendResponse(h, gson.toJson(epic), 201);
        } catch (Exception e) {
            sendInternalServerError(h);
        }

    }

    private void deleteEpicById(HttpExchange h) throws IOException {
        Optional<Integer> id = getId(h);

        if (id.isPresent()) {
            Epic epic = tm.getEpicById(id.get());

            if (epic != null) {
                tm.removeEpic(id.get());
                sendResponse(h, "Эпик с id=" + id.get() + " удален", 200);
            } else {
                sendNotFound(h);
            }
        } else {
            sendInternalServerError(h);
        }
    }
}