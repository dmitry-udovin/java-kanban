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

public class HttpTaskServerHistoryTest {

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
    void shouldReturnHistory() throws IOException, InterruptedException {
        Task t1 = new Task("T1", "D1", Task.Status.NEW,
                Optional.of(LocalDateTime.now()), Duration.ofMinutes(5));
        int id = manager.createNewTask(t1);
        manager.getTaskWithID(id);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET().build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
        assertTrue(resp.body().contains("T1"));
    }
}