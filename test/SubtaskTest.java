import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

class SubtaskTest {
    TaskManager taskManager;
    Epic epic;

    Duration duration1 = Duration.ofMinutes(20);
    LocalDateTime startTime1 = LocalDateTime.of(2025, Month.MARCH, 5, 2, 30);
    Duration duration2 = Duration.ofMinutes(30);
    LocalDateTime startTime2 = LocalDateTime.of(2023, Month.MARCH, 5, 3, 30);

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        epic = new Epic("Эпик", "Описание эпика", new ArrayList<>());
        taskManager.addEpic(epic);
    }

    @Test
    void shouldReturnTrueWhenTasksHaveSameId() {
        Subtask subtask1 = new Subtask("Подзача 1", "Описание подзадачи 1", epic.getId(), duration1, startTime1);
        taskManager.addSubtask(subtask1);

        Subtask retrievedSubtask = taskManager.getSubtask(subtask1.getId());
        assertEquals(subtask1, retrievedSubtask);
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        Subtask subtask1 = new Subtask("Подзача 1", "Описание подзадачи 1", epic.getId(), duration1, startTime1);
        taskManager.addSubtask(subtask1);

        subtask1.setEpicId(subtask1.getId());
        assertNotEquals(subtask1.getEpicId(), subtask1.getId());
    }

    @Test
    void shouldCalculateEndTimeCorrectly() {
        Subtask subtask1 = new Subtask("Подзача 1", "Описание подзадачи 1", epic.getId(), duration1, startTime1);
        LocalDateTime endTime = subtask1.getEndTime();
        LocalDateTime endTimeTest = startTime1.plus(duration1);
        assertEquals(endTime, endTimeTest);
    }

    @Test
    void subtaskShouldBeAddedToManager() {
        Subtask subtask1 = new Subtask("Подзача 1", "Описание подзадачи 1", epic.getId(), duration1, startTime1);
        Subtask subtask2 = new Subtask("Подзача 2", "Описание подзадачи 2", epic.getId(), duration2, startTime2);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(subtask1, taskManager.getSubtask(subtask1.getId()));
        assertEquals(subtask2, taskManager.getSubtask(subtask2.getId()));
    }

    @Test
    void getSubtasksShouldReturnAllAddedTasks() {
        Subtask subtask1 = new Subtask("Подзача 1", "Описание подзадачи 1", epic.getId(), duration1, startTime1);
        Subtask subtask2 = new Subtask("Подзача 2", "Описание подзадачи 2", epic.getId(), duration2, startTime2);

        List<Subtask> expectedSubtasks = new ArrayList<>();
        expectedSubtasks.add(subtask1);
        expectedSubtasks.add(subtask2);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        List<Subtask> actualSubtasks = taskManager.getSubtasks();
        assertEquals(expectedSubtasks, actualSubtasks);
    }

    @Test
    void deleteSubtasksShouldRemoveAllTasks() {
        Subtask subtask1 = new Subtask("Подзача 1", "Описание подзадачи 1", epic.getId(), duration1, startTime1);
        Subtask subtask2 = new Subtask("Подзача 2", "Описание подзадачи 2", epic.getId(), duration2, startTime2);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.deleteSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void identicalSubtasksShouldWorkWithoutProblems() {
        Subtask subtask1 = new Subtask("Подзача 1", "Описание подзадачи 1", epic.getId(), duration1, startTime1);
        Subtask subtask2 = new Subtask("Подзача 1", "Описание подзадачи 1", epic.getId(), duration1, startTime1);

        assertEquals(subtask1.getId(), subtask2.getId());
    }
}