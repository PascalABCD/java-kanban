package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager tm;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager tm, Gson gson) {
        this.tm = tm;
        this.gson = gson;
    }

    protected void sendResponse(HttpExchange h, String responseString, int responseCode) throws IOException {
        byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(responseCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        sendResponse(h, "Задача не найдена ", 404);
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        sendResponse(h, "Есть пересечения по задачам ", 406);
    }

    protected void sendInternalServerError(HttpExchange h) throws IOException {
        sendResponse(h, "Ошибка при обработке запроса ", 500);
    }

    // universal method to get id from path
    protected Optional<Integer> getId(HttpExchange h) throws IOException {
        String[] path = h.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(path[2]));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            sendNotFound(h);
            return Optional.empty();
        }
    }

    public abstract void handle(HttpExchange h) throws IOException;
}
