import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
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

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
            Gson gson = gsonBuilder.setPrettyPrinting().create();

            if (lastElement.equals("tasks") ){
                switch(method){
                    case "GET":
                        List<Task> tasks = taskManager.getTasks();
                        String tasksJson = gson.toJson(tasks);
                        sendText(httpExchange,tasksJson);
                        break;
                    case "POST":
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Task task = gson.fromJson(body, Task.class);
                        taskManager.addTask(task);
                        sendCreated(httpExchange);
                }

            }
            if (secondLastElement.equals("tasks")){
                switch (method){
                    case "GET":
                        Task taskForGet = taskManager.getTask(Integer.parseInt(lastElement));
                        String taskJson = gson.toJson(taskForGet);
                        sendText(httpExchange,taskJson);
                    case "POST":
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Task taskForPost = gson.fromJson(body, Task.class);
                        int taskId = Integer.parseInt(lastElement);
                        taskForPost.setId(taskId);
                        taskManager.updateTask(taskForPost);
                        sendCreated(httpExchange);
                }

            }
        }catch (Exception e) {
            // обработать исключение
        }
    }
}