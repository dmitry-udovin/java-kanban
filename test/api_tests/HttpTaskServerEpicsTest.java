package api_tests;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import tasktracker.HttpTaskServer;
import tasktracker.managers.InMemoryTaskManager;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerEpicsTest {

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

    @Test
    void shouldCreateAndGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Test epic", 0);
        String json = gson.toJson(epic);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, resp.statusCode());

        HttpRequest get = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET().build();
        HttpResponse<String> getResp = client.send(get, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResp.statusCode());
        assertTrue(getResp.body().contains("Epic1"));
    }

    @Test
    void shouldReturnEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "With subs", 0);
        int epicId = manager.createNewEpic(epic);

        Subtask sub = new Subtask("Sub1", "desc", Task.Status.NEW, epicId,
                Optional.of(LocalDateTime.now()), Duration.ofMinutes(20));
        manager.createNewSubtask(sub);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId + "/subtasks"))
                .GET().build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
        assertTrue(resp.body().contains("Sub1"));
    }
}