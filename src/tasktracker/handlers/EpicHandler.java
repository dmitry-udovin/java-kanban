package tasktracker.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends AbstractHandler implements HttpHandler {

    private TaskManager taskManager;
    private Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET": {
                String[] split = path.split("/");

                if (split.length == 4) {
                    try {
                        int taskId = Integer.parseInt(split[2]);
                        Epic epic = taskManager.getEpictaskWithID(taskId);

                        if (epic == null) {
                            sendNotFound(exchange, "Ошибка: задача с ID " + taskId + " не найдена");
                        } else {
                            String epicSubtasksJson = gson.toJson(epic.getTasksInEpic());
                            sendText(exchange, epicSubtasksJson);
                        }

                    } catch (NumberFormatException exp) {
                        sendResponse(400, "Ошибка: неверный формат ID. Ожидается число.", exchange);
                    } catch (Exception exp) {
                        sendResponse(500, "Ошибка сервера: " + exp.getMessage(), exchange);
                    }
                }

                if (split.length == 3) {

                    try {
                        int taskId = Integer.parseInt(split[2]);
                        Epic epic = taskManager.getEpictaskWithID(taskId);

                        if (epic == null) {
                            sendNotFound(exchange, "Ошибка: задача с ID " + taskId + " не найдена");
                        } else {
                            String epicJson = gson.toJson(epic);
                            sendText(exchange, epicJson);
                        }

                    } catch (NumberFormatException exp) {
                        sendResponse(400, "Ошибка: неверный формат ID. Ожидается число.", exchange);
                    } catch (Exception exp) {
                        sendResponse(500, "Ошибка сервера: " + exp.getMessage(), exchange);
                    }

                }

                if (split.length == 2) {
                    List<Epic> allEpics = taskManager.getEpicList();
                    String allEpicsJson = gson.toJson(allEpics);
                    sendText(exchange, allEpicsJson);
                }
                break;
            }

            case "POST": {
                String[] split = path.split("/");

                // заглянуть в тело запроса, десериализовать задачу в java-обьект
                InputStream requestBody = exchange.getRequestBody();
                byte[] bytes = requestBody.readAllBytes();

                String requestBodyString = new String(bytes, StandardCharsets.UTF_8);

                Epic epic = gson.fromJson(requestBodyString, Epic.class);

                if (epic != null) {

                    if (split.length == 2) {
                        taskManager.createNewEpic(epic);
                        sendResponse(201, "Успешно создан новый эпик под " + epic.getTaskId() + " номером", exchange);
                    }

                }
                break;
            }

            case "DELETE": {
                String[] split = path.split("/");

                if (split.length == 3) {
                    try {
                        int taskId = Integer.parseInt(split[2]);
                        if (taskManager.getEpictaskWithID(taskId) != null) {
                            taskManager.deleteEpic(taskId);
                            sendText(exchange, "Успешно удален эпик под номером: " + taskId);
                        } else {
                            sendNotFound(exchange, "Ошибка: не существует эпика по указанному номеру");
                        }
                    } catch (NumberFormatException exp) {
                        sendResponse(400, "Ошибка: неверный формат ID. Ожидается число.", exchange);
                    } catch (Exception exp) {
                        sendResponse(500, "Ошибка сервера: " + exp.getMessage(), exchange);
                    }

                }

            }


        }
    }

}
