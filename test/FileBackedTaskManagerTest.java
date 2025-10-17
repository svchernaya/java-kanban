import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    Duration durationForSubtask = Duration.ofMinutes(50);
    LocalDateTime startTimeForSubtask = LocalDateTime.of(2024, Month.FEBRUARY, 10, 4, 50);
    Duration durationForTask = Duration.ofMinutes(20);
    LocalDateTime startTimeForTask = LocalDateTime.of(2025, Month.MARCH, 5, 2, 30);

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            File file = File.createTempFile("task_manager_test", ".csv");
            return new FileBackedTaskManager(file.getPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка создания временного файла");
        }
    }

    @Test
    public void testFileOperations() {
        try {
            File file = File.createTempFile("task_manager_test", ".csv");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.getPath());

            Epic epic1 = new Epic("Эпик 1", "Описание", new ArrayList<>());
            Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1.getId(), durationForSubtask, startTimeForSubtask);
            Task task1 = new Task("Задача 1", "Описание задачи 1", durationForTask, startTimeForTask);

            fileBackedTaskManager.addEpic(epic1);
            fileBackedTaskManager.addTask(task1);
            fileBackedTaskManager.addSubtask(subtask1);

            String fileRead = Files.readString(file.toPath());

            assertTrue(file.exists());
            assertTrue(fileRead.contains("Эпик 1"), "Должен быть эпик");
            assertTrue(fileRead.contains("Задача 1"), "Должна быть задача");
            assertTrue(fileRead.contains("Подзадача 1"), "Должна быть подзадача");

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка инициализации");
        }
    }

    @Test
    public void shouldSaveAndLoadEmptyFile() {
        try {
            File file = File.createTempFile("empty_test", ".csv");

            FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

            assertTrue(manager2.getTasks().isEmpty(), "Задач не должно быть");
            assertTrue(manager2.getEpics().isEmpty(), "Эпиков не должно быть");
            assertTrue(manager2.getSubtasks().isEmpty(), "Подзадач не должно быть");

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка теста пустого файла");
        }
    }
}