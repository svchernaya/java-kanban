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

public class HttpTaskManagerEpicsTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicsTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Test", "Epic Description", new ArrayList<>());
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic Test", epicsFromManager.get(0).getTitle(), "Некорректное имя эпика");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Test", "Epic Description", new ArrayList<>());
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> tasksFromManager = manager.getEpics();
        int epicId = tasksFromManager.get(0).getId();

        URI urlForDelete = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlForDelete).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode());
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic Test 1", "Epic Description 1", new ArrayList<>());
        Epic epic2 = new Epic("Epic Test 2", "Epic Description 2", new ArrayList<>());

        String epicJson1 = gson.toJson(epic1);
        String epicJson2 = gson.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest requestPost1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson1)).build();
        HttpResponse<String> responsePost1 = client.send(requestPost1, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestPost2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson2)).build();
        HttpResponse<String> responsePost2 = client.send(requestPost2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        List<Epic> epicsFromManager = manager.getEpics();
        List<Epic> epicsFromJson = gson.fromJson(responseGet.body(), new EpicListTypeToken());

        assertEquals(epicsFromManager, epicsFromJson, "Список всех эпиков не совпадает");
    }


    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Test", "Epic Description", new ArrayList<>());
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Test 1", "Testing subtask 1", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Test 2", "Testing subtask 2", epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(manager.getSubtask(epic.getSubtaskIds().get(0)));
        subtasks.add(manager.getSubtask(epic.getSubtaskIds().get(1)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromJson = gson.fromJson(response.body(), new SubtaskListTypeToken());
        assertEquals(subtasks, subtasksFromJson, "Список подзадач эпика не совпадает");
    }

    @Test
    public void testGetEpicSubtasksWithInvalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/999/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}