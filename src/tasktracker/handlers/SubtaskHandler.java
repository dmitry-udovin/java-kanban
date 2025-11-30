package tasktracker.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.exceptions.TaskTimeException;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends AbstractHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET": {
                    String[] split = path.split("/");

                    if (split.length == 3) {
                        int subId = Integer.parseInt(split[2]);
                        Subtask subtask = taskManager.getSubtaskWithID(subId);

                        if (subtask == null) {
                            sendNotFound(exchange, "Ошибка: подзадача с ID " + subId + " не найдена");
                        } else {
                            sendText(exchange, gson.toJson(subtask));
                        }
                    } else if (split.length == 2) {
                        List<Subtask> all = taskManager.getSubtaskList();
                        sendText(exchange, gson.toJson(all));
                    }
                    break;
                }

                case "POST": {
                    String[] split = path.split("/");
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);

                    if (subtask == null) {
                        sendResponse(400, "Ошибка: тело запроса пустое или некорректное", exchange);
                        break;
                    }

                    if (split.length == 2) {
                        try {
                            int id = taskManager.createNewSubtask(subtask);
                            sendResponse(201, "Добавлена новая подзадача под номером " + id
                                    + ". Относится к эпику #" + subtask.getEpicId(), exchange);
                        } catch (TaskTimeException e) {
                            sendHasInteractions(exchange, "Ошибка: время подзадачи пересекается с существующими.");
                        }
                        break;
                    }

                    if (split.length == 3) {
                        try {
                            int subId = Integer.parseInt(split[2]);
                            Subtask current = taskManager.getSubtaskWithID(subId);

                            if (current == null) {
                                sendNotFound(exchange, "Ошибка: подзадача с ID " + subId + " не найдена");
                                break;
                            }

                            subtask.setTaskId(subId);
                            taskManager.updateSubtask(subtask);
                            sendResponse(201, "Вы успешно обновили подзадачу #" + subId, exchange);
                        } catch (NumberFormatException e) {
                            sendResponse(400, "Ошибка: неверный формат ID. Ожидается число.", exchange);
                        } catch (TaskTimeException e) {
                            sendHasInteractions(exchange, "Ошибка: время подзадачи пересекается с существующими.");
                        }
                    }
                    break;
                }

                case "DELETE": {
                    String[] split = path.split("/");

                    if (split.length == 3) {
                        int subId = Integer.parseInt(split[2]);
                        if (taskManager.getSubtaskWithID(subId) != null) {
                            taskManager.deleteSubtask(subId);
                            sendText(exchange, "Успешно удалена подзадача под номером: " + subId);
                        } else {
                            sendNotFound(exchange, "Ошибка: нет подзадачи по указанному номеру");
                        }
                    } else {
                        sendNotFound(exchange, "Ошибка: укажите номер подзадачи для удаления.");
                    }
                    break;
                }
            }
        } catch (NumberFormatException e) {
            sendResponse(400, "Ошибка: неверный формат ID. Ожидается число.", exchange);
        } catch (Exception e) {
            sendResponse(500, "Ошибка сервера: " + e.getMessage(), exchange);
        }
    }
}