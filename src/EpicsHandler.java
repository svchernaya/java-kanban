import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            URI requestURI = httpExchange.getRequestURI();
            String method = httpExchange.getRequestMethod();

            String[] splitStrings = requestURI.getPath().split("/");
            String lastElement = splitStrings[splitStrings.length - 1];
            String secondLastElement = splitStrings[splitStrings.length - 2];

            Gson gson = HttpTaskServer.getGson();

            if (lastElement.equals("epics")) {
                switch (method) {
                    case "GET":
                        List<Epic> epics = taskManager.getEpics();
                        String epicsJson = gson.toJson(epics);
                        sendText(httpExchange, epicsJson);
                        break;
                    case "POST":
                        try {
                            InputStream inputStream = httpExchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Epic epic = gson.fromJson(body, Epic.class);
                            taskManager.addEpic(epic);
                            sendCreated(httpExchange);
                        } catch (IllegalArgumentException e) {
                            if (e.getMessage().contains("пересекается")) {
                                sendHasInteractions(httpExchange);
                            } else {
                                sendInternalError(httpExchange);
                            }
                        }
                        break;
                }

            }
            if (secondLastElement.equals("epics")) {
                switch (method) {
                    case "GET":
                        Epic epicForGet = taskManager.getEpic(Integer.parseInt(lastElement));
                        if (epicForGet == null) {
                            sendNotFound(httpExchange);
                            return;
                        }
                        String epicJson = gson.toJson(epicForGet);
                        sendText(httpExchange, epicJson);
                        break;
                    case "DELETE":
                        int taskId = Integer.parseInt(lastElement);
                        if (taskManager.getEpic(taskId) == null) {
                            sendNotFound(httpExchange);
                            return;
                        }
                        taskManager.deleteEpicById(taskId);
                        sendText(httpExchange, "Задача удалена");
                        break;
                }

            }
            if (lastElement.equals("subtasks")) {
                if (method.equals("GET")) {
                    Epic epic = taskManager.getEpic(Integer.parseInt(secondLastElement));
                    if (epic == null) {
                        sendNotFound(httpExchange);
                        return;
                    }
                    List<Subtask> subtasks = taskManager.getEpicSubtasks(Integer.parseInt(secondLastElement));
                    String subtasksJson = gson.toJson(subtasks);
                    sendText(httpExchange, subtasksJson);
                }
            }
        } catch (Exception e) {
            sendInternalError(httpExchange);
        }
    }
}