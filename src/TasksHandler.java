import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
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

            if (lastElement.equals("tasks")) {
                switch (method) {
                    case "GET":
                        List<Task> tasks = taskManager.getTasks();
                        String tasksJson = gson.toJson(tasks);
                        sendText(httpExchange, tasksJson);
                        break;
                    case "POST":
                        try {
                            InputStream inputStream = httpExchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Task task = gson.fromJson(body, Task.class);
                            taskManager.addTask(task);
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
            if (secondLastElement.equals("tasks")) {
                switch (method) {
                    case "GET":
                        Task taskForGet = taskManager.getTask(Integer.parseInt(lastElement));
                        if (taskForGet == null) {
                            sendNotFound(httpExchange);
                            return;
                        }
                        String taskJson = gson.toJson(taskForGet);
                        sendText(httpExchange, taskJson);
                        break;
                    case "POST":
                        try {
                            InputStream inputStream = httpExchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Task taskForPost = gson.fromJson(body, Task.class);
                            int taskId = Integer.parseInt(lastElement);
                            taskForPost.setId(taskId);
                            taskManager.updateTask(taskForPost);
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
                        if (taskManager.getTask(taskId) == null) {
                            sendNotFound(httpExchange);
                            return;
                        }
                        taskManager.deleteTaskById(taskId);
                        sendText(httpExchange, "Задача удалена");
                        break;
                }

            }
        } catch (Exception e) {
            sendInternalError(httpExchange);
        }
    }
}