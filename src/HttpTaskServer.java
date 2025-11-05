import com.google.gson.TypeAdapter;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/epics", new EpicsHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
        httpServer.createContext("/subtasks", new SubtasksHandler());
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/tasks", new TasksHandler(manager));

        httpServer.start();
    }
}
