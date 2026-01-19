package tasktracker.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends AbstractHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
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

                    if (split.length == 4) {
                        int epicId = Integer.parseInt(split[2]);
                        Epic epic = taskManager.getEpictaskWithID(epicId);
                        if (epic == null) {
                            sendNotFound(exchange, "Ошибка: эпик с ID " + epicId + " не найден");
                        } else {
                            sendText(exchange, gson.toJson(epic.getTasksInEpic()));
                        }
                        break;
                    }

                    if (split.length == 3) {
                        int epicId = Integer.parseInt(split[2]);
                        Epic epic = taskManager.getEpictaskWithID(epicId);
                        if (epic == null) {
                            sendNotFound(exchange, "Ошибка: эпик с ID " + epicId + " не найден");
                        } else {
                            sendText(exchange, gson.toJson(epic));
                        }
                        break;
                    }

                    if (split.length == 2) {
                        List<Epic> all = taskManager.getEpicList();
                        sendText(exchange, gson.toJson(all));
                    }
                    break;
                }

                case "POST": {
                    String[] split = path.split("/");
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);

                    if (epic == null) {
                        sendResponse(400, "Ошибка: тело запроса пустое или некорректное", exchange);
                        break;
                    }

                    if (split.length == 2) {
                        int id = taskManager.createNewEpic(epic);
                        sendResponse(201, "Успешно создан новый эпик под " + id + " номером", exchange);
                        break;
                    }

                    sendResponse(400, "Обновление эпиков не поддерживается", exchange);
                    break;
                }

                case "DELETE": {
                    String[] split = path.split("/");
                    if (split.length == 3) {
                        int epicId = Integer.parseInt(split[2]);
                        if (taskManager.getEpictaskWithID(epicId) != null) {
                            taskManager.deleteEpic(epicId);
                            sendText(exchange, "Успешно удален эпик под номером: " + epicId);
                        } else {
                            sendNotFound(exchange, "Ошибка: не существует эпика по указанному номеру");
                        }
                    } else {
                        sendNotFound(exchange, "Ошибка: укажите номер эпика для удаления.");
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