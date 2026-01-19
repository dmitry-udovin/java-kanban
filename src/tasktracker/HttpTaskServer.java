package tasktracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import tasktracker.adapters.DurationAdapter;
import tasktracker.adapters.LocalDateTimeAdapter;
import tasktracker.adapters.OptionalTypeAdapterFactory;
import tasktracker.handlers.EpicHandler;
import tasktracker.handlers.HistoryHandler;
import tasktracker.handlers.PriorityHandler;
import tasktracker.handlers.SubtaskHandler;
import tasktracker.handlers.TaskHandler;
import tasktracker.managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.taskManager = manager;

        server = HttpServer.create(new InetSocketAddress(8080), 0);

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapterFactory(new OptionalTypeAdapterFactory())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        gson = gsonBuilder.create();

        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        server.createContext("/epics", new EpicHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PriorityHandler(taskManager, gson));
    }

    public void start() {
        server.start();
        System.out.println("HTTP сервер запущен на порту 8080");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP сервер остановлен");
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapterFactory(new OptionalTypeAdapterFactory())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }
}