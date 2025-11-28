package tasktracker.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.managers.InMemoryTaskManager;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends AbstractHandler implements HttpHandler {

    private TaskManager taskManager;
    private Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
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

                if (split.length == 3) {

                    try {
                        int taskId = Integer.parseInt(split[2]);
                        Subtask subtask = taskManager.getSubtaskWithID(taskId);

                        if (subtask == null) {
                            sendNotFound(exchange, "Ошибка: задача с ID " + taskId + " не найдена");
                        } else {
                            String subtaskJson = gson.toJson(subtask);
                            sendText(exchange, subtaskJson);
                        }

                    } catch (NumberFormatException exp) {
                        sendResponse(400, "Ошибка: неверный формат ID. Ожидается число.", exchange);
                    } catch (Exception exp) {
                        sendResponse(500, "Ошибка сервера: " + exp.getMessage(), exchange);
                    }

                }

                if (split.length == 2) {
                    List<Subtask> allTasks = taskManager.getSubtaskList();
                    String allTasksJson = gson.toJson(allTasks);
                    sendText(exchange, allTasksJson);
                }
                break;
            }

            case "POST": {
                String[] split = path.split("/");

                // заглянуть в тело запроса, десериализовать задачу в java-обьект
                InputStream requestBody = exchange.getRequestBody();
                byte[] bytes = requestBody.readAllBytes();

                String requestBodyString = new String(bytes, StandardCharsets.UTF_8);

                Subtask subtask = gson.fromJson(requestBodyString, Subtask.class);

                InMemoryTaskManager inMemoryManager = (InMemoryTaskManager) taskManager;

                if (subtask != null && !inMemoryManager.overlapsAny(subtask)) {

                    if (split.length == 2) {
                        int epicId = taskManager.createNewSubtask(subtask);
                        sendResponse(201, "Добавлена новая подзадача под " + epicId + " номером. Относится к эпику: #" + subtask.getEpicId(), exchange);
                    }

                    if (split.length == 3) {
                        try {
                            int taskId = Integer.parseInt(split[2]);
                            Subtask currentSubtask = taskManager.getSubtaskWithID(taskId);

                            currentSubtask.setTaskName(subtask.getTaskName());
                            currentSubtask.setTaskDescription(subtask.getTaskDescription());
                            currentSubtask.setStatus(subtask.getStatus());
                            currentSubtask.setStartTime(subtask.getStartTime());
                            currentSubtask.setDuration(subtask.getDuration());

                            sendResponse(201, "Вы успешно обновили подзадачу под " + taskId + " номером", exchange);

                        } catch (NumberFormatException exp) {
                            sendResponse(400, "Ошибка: неверный формат ID. Ожидается число.", exchange);
                        } catch (Exception exp) {
                            sendResponse(500, "Ошибка сервера: " + exp.getMessage(), exchange);
                        }
                    }
                } else {
                    if (inMemoryManager.overlapsAny(subtask)) {
                        sendHasInteractions(exchange, "Ошибка: время задачи пересекается с существующими.");
                    }
                }
                break;
            }

            case "DELETE": {
                String[] split = path.split("/");

                if (split.length == 3) {
                    try {
                        int taskId = Integer.parseInt(split[2]);
                        if (taskManager.getSubtaskWithID(taskId) != null) {
                            taskManager.deleteSubtask(taskId);
                            sendText(exchange, "Успешно удалена подзадача под номером: " + taskId);
                        } else {
                            sendNotFound(exchange, "Ошибка: нет подзадачи по указанному номеру");
                        }
                    } catch (NumberFormatException exp) {
                        sendResponse(400, "Ошибка: неверный формат ID. Ожидается число.", exchange);
                    } catch (Exception exp) {
                        sendResponse(500, "Ошибка сервера: " + exp.getMessage(), exchange);
                    }

                } else {
                    sendNotFound(exchange, "Ошибка: укажите номер подзадачи для удаления.");
                }

            }


        }
    }

}