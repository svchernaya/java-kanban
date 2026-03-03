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
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManagerSubtasksTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubtasksTest() throws IOException {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Test", "Epic Description", new ArrayList<>());
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Test 2", "Testing subtask 2", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", subtasksFromManager.get(0).getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Test", "Epic Description", new ArrayList<>());
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Test 2", "Testing subtask 2", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        URI urlForDelete = URI.create("http://localhost:8080/subtasks" + "/" + epic.getSubtaskIds().get(0));
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlForDelete).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode());
    }

    @Test
    public void testAddSubtaskWithTimeOverlap() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Test", "Epic Description", new ArrayList<>());
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        Subtask subtask1 = new Subtask("Test 2", "Testing subtask 2", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        String subtaskJson1 = gson.toJson(subtask1);
        HttpRequest requestSubtask1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson1)).build();
        HttpResponse<String> responseSubtask1 = client.send(requestSubtask1, HttpResponse.BodyHandlers.ofString());

        Subtask subtask2 = new Subtask("Test 3", "Testing subtask 3", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        String subtaskJson2 = gson.toJson(subtask2);
        HttpRequest requestSubtask2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
        HttpResponse<String> response = client.send(requestSubtask2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Test", "Epic Description", new ArrayList<>());
        manager.addEpic(epic);

        List<Subtask> subtasks = new ArrayList<>();

        Subtask subtask1 = new Subtask("Test 2", "Testing subtask 2", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Test 3", "Testing subtask 3", epic.getId(), Duration.ofMinutes(5).plusMinutes(3), LocalDateTime.now().plusDays(10));

        String subtaskJson1 = gson.toJson(subtask1);
        String subtaskJson2 = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest requestPost1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson1)).build();
        HttpResponse<String> responsePost1 = client.send(requestPost1, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestPost2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
        HttpResponse<String> responsePost2 = client.send(requestPost2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        subtasks.add(manager.getSubtask(epic.getSubtaskIds().get(0)));
        subtasks.add(manager.getSubtask(epic.getSubtaskIds().get(1)));

        List<Subtask> subtasksFromJson = gson.fromJson(responseGet.body(), new SubtaskListTypeToken());
        assertEquals(subtasks, subtasksFromJson, "Список всех подзадач не совпадает");
    }
}