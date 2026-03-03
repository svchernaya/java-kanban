import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            if (!method.equals("GET")) {
                sendInternalError(httpExchange);
                return;
            }
            List<Task> historyTasks = taskManager.getHistory();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
            Gson gson = gsonBuilder.setPrettyPrinting().create();
            String historyJson = gson.toJson(historyTasks);
            sendText(httpExchange, historyJson);
        } catch (Exception e) {
            sendInternalError(httpExchange);
        }
    }
}