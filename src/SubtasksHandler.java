import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
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

            if (lastElement.equals("subtasks")) {
                switch (method) {
                    case "GET":
                        List<Subtask> subtasks = taskManager.getSubtasks();
                        String sutasksJson = gson.toJson(subtasks);
                        sendText(httpExchange, sutasksJson);
                        break;
                    case "POST":
                        try {
                            InputStream inputStream = httpExchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            taskManager.addSubtask(subtask);
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
            if (secondLastElement.equals("subtasks")) {
                switch (method) {
                    case "GET":
                        Subtask subtaskForGet = taskManager.getSubtask(Integer.parseInt(lastElement));
                        if (subtaskForGet == null) {
                            sendNotFound(httpExchange);
                            return;
                        }
                        String sutaskJson = gson.toJson(subtaskForGet);
                        sendText(httpExchange, sutaskJson);
                        break;
                    case "POST":
                        try {
                            InputStream inputStream = httpExchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Subtask sutaskForPost = gson.fromJson(body, Subtask.class);
                            int subtaslId = Integer.parseInt(lastElement);
                            sutaskForPost.setId(subtaslId);
                            taskManager.updateSubtask(sutaskForPost);
                            sendCreated(httpExchange);
                        } catch (IllegalArgumentException e) {
                            if (e.getMessage().contains("пересекается")) {
                                sendHasInteractions(httpExchange);
                            } else {
                                sendInternalError(httpExchange);
                            }
                        }
                        break;
                    case "DELETE":
                        int taskId = Integer.parseInt(lastElement);
                        if (taskManager.getSubtask(taskId) == null) {
                            sendNotFound(httpExchange);
                            return;
                        }
                        taskManager.deleteSubtaskById(taskId);
                        sendText(httpExchange, "Задача удалена");
                        break;
                }

            }
        } catch (Exception e) {
            sendInternalError(httpExchange);
        }
    }
}