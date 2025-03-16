package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager tm, Gson gson) {
        super(tm, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String method = h.getRequestMethod();
        String[] path = h.getRequestURI().getPath().split("/");

        switch (method) {
            case "GET" -> {
                if (path.length == 3) {
                    getSubtaskById(h);
                } else {
                    getAllSubtasks(h);
                }
            }
            case "POST" -> {
                if (path.length == 3) {
                    updateSubtask(h);
                } else {
                    createSubtask(h);
                }
            }
            case "DELETE" -> {
                if (path.length == 3) {
                    deleteSubtaskById(h);
                } else {
                    sendNotFound(h);
                }
            }
        }
    }

    private void getSubtaskById(HttpExchange h) throws IOException {
        Optional<Integer> id = getId(h);

        if (id.isPresent()) {
            Subtask subtask = tm.getSubtaskById(id.get());
            if (subtask != null) {
                String response = gson.toJson(subtask);
                sendResponse(h, response, 200);
            } else {
                sendNotFound(h);
            }
        } else {
            sendInternalServerError(h);
        }
    }

    private void getAllSubtasks(HttpExchange h) throws IOException {
        List<Subtask> subtasks = tm.getAllSubtasks();
        String response = gson.toJson(subtasks);
        sendResponse(h, response, 200);
    }

    private void createSubtask(HttpExchange h) throws IOException {
        InputStream is = h.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        Epic epic = tm.getEpicById(subtask.getEpicId());

        try {
            if (epic != null) {
                tm.createSubtask(subtask);
                sendResponse(h, "Подзадача создана в эпике с id " + epic.getId(), 201);
            } else {
                sendNotFound(h);
            }
        } catch (IOException e) {
            sendInternalServerError(h);
        }
    }

    private void updateSubtask(HttpExchange h) throws IOException {
        Optional<Integer> id = getId(h);

        if (id.isPresent()) {
            InputStream is = h.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            subtask.setId(id.get());
            try {
                tm.updateSubtask(subtask);
                sendResponse(h, "Подзадача создана ", 201);
            } catch (IOException e) {
                sendInternalServerError(h);
            }
        } else {
            sendNotFound(h);
        }
    }

    private void deleteSubtaskById(HttpExchange h) throws IOException {
        Optional<Integer> id = getId(h);

        if (id.isPresent()) {
            tm.removeSubtask(id.get());
            sendResponse(h, "Подзадача удалена ", 200);
        } else {
            sendInternalServerError(h);
        }
    }
}
