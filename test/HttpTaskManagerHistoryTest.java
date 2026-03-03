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

public class HttpTaskManagerHistoryTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerHistoryTest() throws IOException {
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
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);

        manager.getTask(task.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> historyFromResponse = gson.fromJson(response.body(), new TaskListTypeToken());
        List<Task> historyFromManager = manager.getHistory();

        assertNotNull(historyFromResponse);
        assertEquals(historyFromManager.size(), historyFromResponse.size());
        assertEquals(historyFromManager.get(0).getTitle(), historyFromResponse.get(0).getTitle());
    }
}