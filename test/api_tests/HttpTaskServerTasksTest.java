package api_tests;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import tasktracker.HttpTaskServer;
import tasktracker.managers.InMemoryTaskManager;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTasksTest {

    private TaskManager manager;
    private HttpTaskServer server;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    // ---------- POST /tasks ----------
    @Test
    void shouldAddTaskSuccessfully() throws IOException, InterruptedException {
        Task task = new Task(
                "TestTask",
                "Desc",
                Task.Status.NEW,
                Optional.of(LocalDateTime.of(2050, 3, 3, 9, 52, 2)),
                Duration.ofMinutes(30)
        );

        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "POST должен вернуть 201");
        assertEquals(1, manager.getTaskList().size(), "Должна добавиться одна задача");
        assertEquals("TestTask", manager.getTaskList().get(0).getTaskName());
    }

    // ---------- GET /tasks ----------
    @Test
    void shouldReturnAllTasks() throws IOException, InterruptedException {
        Task t1 = new Task("T1", "D1", Task.Status.NEW,
                Optional.of(LocalDateTime.now()), Duration.ofMinutes(10));
        manager.createNewTask(t1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "GET /tasks должен вернуть 200");
        assertTrue(response.body().contains("T1"), "Ответ должен содержать имя задачи");
    }

    // ---------- GET /tasks/{id} ----------
    @Test
    void shouldReturnTaskById() throws IOException, InterruptedException {
        Task t1 = new Task("TaskById", "Desc", Task.Status.NEW,
                Optional.of(LocalDateTime.now()), Duration.ofMinutes(10));
        int id = manager.createNewTask(t1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + id))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("TaskById"));
    }

    @Test
    void shouldReturn404IfTaskNotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Если задачи нет, возвращается 404");
        assertTrue(response.body().contains("не найдена"));
    }

    // ---------- POST /tasks/{id} ----------
    @Test
    void shouldUpdateExistingTask() throws IOException, InterruptedException {
        Task t1 = new Task("OldName", "OldDesc", Task.Status.NEW,
                Optional.of(LocalDateTime.now()), Duration.ofMinutes(10));
        int id = manager.createNewTask(t1);

        Task updated = new Task("NewName", "NewDesc", Task.Status.IN_PROGRESS,
                Optional.of(LocalDateTime.now().plusHours(1)), Duration.ofMinutes(15));

        String json = gson.toJson(updated);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + id))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("NewName", manager.getTaskWithID(id).getTaskName());
    }

    // ---------- DELETE /tasks/{id} ----------
    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task t1 = new Task("DeleteMe", "ToBeDeleted", Task.Status.NEW,
                Optional.of(LocalDateTime.now()), Duration.ofMinutes(10));
        int id = manager.createNewTask(t1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "DELETE должен вернуть 200");
        assertEquals(0, manager.getTaskList().size(), "Задача должна быть удалена");
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .DELETE()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("нет задачи"));
    }
}