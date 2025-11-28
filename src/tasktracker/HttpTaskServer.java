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
import tasktracker.managers.Managers;
import tasktracker.managers.TaskManager;
import tasktracker.utilities.ManagerType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);

        TaskManager taskManager = Managers.get(ManagerType.IN_MEMORY);

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapterFactory(new OptionalTypeAdapterFactory())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        Gson gson = gsonBuilder.create();

        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        server.createContext("/epics", new EpicHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PriorityHandler(taskManager, gson));

        server.start();

        System.out.println("Сервер запущен!");
    }
}