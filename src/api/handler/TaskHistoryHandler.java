package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class TaskHistoryHandler extends BaseHttpHandler {
    public TaskHistoryHandler(TaskManager tm, Gson gson) {
        super(tm, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String method = h.getRequestMethod();
        String[] path = h.getRequestURI().getPath().split("/");

        if (method.equals("GET") && path.length == 2) {
            getHistory(h);
        } else {
            sendInternalServerError(h);
        }

    }

    private void getHistory(HttpExchange h) throws IOException {
        List<Task> tasks = tm.getHistory();
        sendResponse(h, gson.toJson(tasks), 200);
    }
}
