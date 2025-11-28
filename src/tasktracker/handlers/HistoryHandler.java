package tasktracker.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends AbstractHandler implements HttpHandler {

    private TaskManager taskManager;
    private Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (method.equals("GET")) {
            String[] split = path.split("/");

            if (split.length == 2) {

                try {

                    if (split[1].equals("history")) {
                        List<Task> tasks = taskManager.getHistory();
                        String allTasksJson = gson.toJson(tasks);
                        sendText(exchange, allTasksJson);

                    }


                } catch (Exception exp) {
                    sendResponse(500, "Ошибка сервера: " + exp.getMessage(), exchange);
                }

            }

        }

    }

}
