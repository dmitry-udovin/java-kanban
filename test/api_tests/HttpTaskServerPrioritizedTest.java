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

public class HttpTaskServerPrioritizedTest {

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
    void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        Task t1 = new Task("T1", "D1", Task.Status.NEW,
                Optional.of(LocalDateTime.of(2050, 1, 1, 10, 0)), Duration.ofMinutes(5));
        Task t2 = new Task("T2", "D2", Task.Status.NEW,
                Optional.of(LocalDateTime.of(2050, 1, 1, 9, 0)), Duration.ofMinutes(5));
        manager.createNewTask(t1);
        manager.createNewTask(t2);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET().build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
        assertTrue(resp.body().indexOf("T2") < resp.body().indexOf("T1"),
                "Сначала должна идти более ранняя задача");
    }
}