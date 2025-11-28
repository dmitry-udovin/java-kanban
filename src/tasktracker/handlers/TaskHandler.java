package tasktracker.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.managers.InMemoryTaskManager;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends AbstractHandler implements HttpHandler {

    private TaskManager taskManager;
    private Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
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
                        Task task = taskManager.getTaskWithID(taskId);

                        if (task == null) {
                            sendNotFound(exchange, "Ошибка: задача с ID " + taskId + " не найдена");
                        } else {
                            String taskJson = gson.toJson(task);
                            sendText(exchange, taskJson);
                        }

                    } catch (NumberFormatException exp) {
                        sendResponse(400, "Ошибка: неверный формат ID. Ожидается число.", exchange);
                    } catch (Exception exp) {
                        sendResponse(500, "Ошибка сервера: " + exp.getMessage(), exchange);
                    }

                }

                if (split.length == 2) {
                    List<Task> allTasks = taskManager.getTaskList();
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

                Task task = gson.fromJson(requestBodyString, Task.class);

                InMemoryTaskManager inMemoryManager = (InMemoryTaskManager) taskManager;

                if (task != null && !inMemoryManager.overlapsAny(task)) {

                    if (split.length == 2) {
                        taskManager.createNewTask(task);
                        sendResponse(201, "Добавлена новая задача под " + task.getTaskId() + " номером", exchange);
                    }

                    if (split.length == 3) {
                        try {
                            int taskId = Integer.parseInt(split[2]);
                            Task currentTask = taskManager.getTaskWithID(taskId);

                            currentTask.setTaskName(task.getTaskName());
                            currentTask.setTaskDescription(task.getTaskDescription());
                            currentTask.setStatus(task.getStatus());
                            currentTask.setStartTime(task.getStartTime());
                            currentTask.setDuration(task.getDuration());

                            sendResponse(201, "Вы успешно обновили задачу под " + taskId + " номером", exchange);

                        } catch (NumberFormatException exp) {
                            sendResponse(400, "Ошибка: неверный формат ID. Ожидается число.", exchange);
                        } catch (Exception exp) {
                            sendResponse(500, "Ошибка сервера: " + exp.getMessage(), exchange);
                        }
                    }
                } else {
                    if (inMemoryManager.overlapsAny(task)) {
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
                        if (taskManager.getTaskWithID(taskId) != null) {
                            taskManager.deleteTask(taskId);
                            sendText(exchange, "Успешно удалена задача под номером: " + taskId);
                        } else {
                            sendNotFound(exchange, "Ошибка: нет задачи по указанному номеру");
                        }
                    } catch (NumberFormatException exp) {
                        sendResponse(400, "Ошибка: неверный формат ID. Ожидается число.", exchange);
                    } catch (Exception exp) {
                        sendResponse(500, "Ошибка сервера: " + exp.getMessage(), exchange);
                    }

                } else {
                    sendNotFound(exchange, "Ошибка: укажите номер задачи для удаления.");
                }

            }


        }

    }

}
