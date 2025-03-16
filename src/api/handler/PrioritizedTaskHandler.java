package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Set;

public class PrioritizedTaskHandler extends BaseHttpHandler {
    public PrioritizedTaskHandler(TaskManager tm, Gson gson) {
        super(tm, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String method = h.getRequestMethod();
        String[] path = h.getRequestURI().getPath().split("/");

        if (method.equals("GET") && path.length == 2) {
            getPrioritizedTasks(h);
        } else {
            sendInternalServerError(h);
        }

    }

    private void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        Set<Task> tasks = tm.getPrioritizedTasks();
        sendResponse(exchange, gson.toJson(tasks), HttpURLConnection.HTTP_OK);
    }
}
