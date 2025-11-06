import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManagerPrioritizedTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerPrioritizedTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteAllEpics();
        try {
            taskServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }


    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "First task", Duration.ofMinutes(5), LocalDateTime.now().plusHours(1));
        Task task2 = new Task("Task 2", "Second task", Duration.ofMinutes(10), LocalDateTime.now().plusHours(2));

        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> prioritizedFromResponse = gson.fromJson(response.body(), new TaskListTypeToken());
        List<Task> prioritizedFromManager = manager.getPrioritizedTasks();

        assertNotNull(prioritizedFromResponse);
        assertEquals(prioritizedFromManager.size(), prioritizedFromResponse.size());
        assertEquals("Task 1", prioritizedFromResponse.get(0).getTitle());
    }

}