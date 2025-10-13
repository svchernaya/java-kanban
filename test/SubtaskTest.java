import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

class SubtaskTest {
    TaskManager taskManager = Managers.getDefault();

    Duration duration = Duration.ofMinutes(20);
    LocalDateTime startTime = LocalDateTime.of(2025, Month.MARCH,5,2,30);

    Subtask subtask1 = new Subtask("Подзача 1", "Описание подзадачи 1", 2,duration,startTime);
    Subtask subtask2 = new Subtask("Подзача 1", "Описание подзадачи 1", 2,duration,startTime);

    @Test
    void shouldReturnTrueWhenTasksHaveSameId() {
        assertEquals(subtask1, subtask2);
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        subtask1.setEpicId(subtask1.getId());
        assertNotEquals(subtask1.getEpicId(), subtask1.getId());
    }

    @Test
    void shouldCalculateEndTimeCorrectly() {
        LocalDateTime endTime = subtask1.getEndTime();
        LocalDateTime endTimeTest = startTime.plus(duration);
        assertEquals(endTime, endTimeTest);
    }

    @Test
    void subtaskShouldBeAddedToManager() {
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(subtask1, taskManager.getSubtask(subtask1.getId()));
        assertEquals(subtask2, taskManager.getSubtask(subtask2.getId()));
    }

    @Test
    void getSubtasksShouldReturnAllAddedTasks() {
        List<Task> subtasks = new ArrayList<>();

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        subtasks.add(subtask1);
        subtasks.add(subtask2);

        assertEquals(subtasks, taskManager.getSubtasks());
    }

    @Test
    void deleteSubtasksShouldRemoveAllTasks(){
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);

        taskManager.deleteSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty());
    }
}
