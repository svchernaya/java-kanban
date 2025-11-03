import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

class TaskTest {
    TaskManager taskManager = Managers.getDefault();

    Duration duration = Duration.ofMinutes(20);
    LocalDateTime startTime = LocalDateTime.of(2025, Month.MARCH, 5, 2, 30);

    Task task1 = new Task("Задача 1", "Описание задачи 1", duration, startTime);
    Task task2 = new Task("Задача 1", "Описание задачи 1", duration.plusDays(10), startTime.plusYears(-10));

    @Test
    void shouldCalculateEndTimeCorrectly() {
        LocalDateTime endTime = task1.getEndTime();
        LocalDateTime endTimeTest = startTime.plus(duration);
        assertEquals(endTime, endTimeTest);
    }

    @Test
    void tasksWithSameDataShouldBeEqual() {
        assertEquals(task1, task2);
    }

    @Test
    void taskShouldBeAddedToManager() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        assertEquals(task1, taskManager.getTask(task1.getId()));
        assertEquals(task2, taskManager.getTask(task2.getId()));
    }

    @Test
    void getTasksShouldReturnAllAddedTasks() {
        List<Task> tasks = new ArrayList<>();

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        tasks.add(task1);
        tasks.add(task2);

        assertEquals(tasks, taskManager.getTasks());
    }

    @Test
    void deleteTasksShouldRemoveAllTasks() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.deleteTasks();

        assertTrue(taskManager.getTasks().isEmpty());
    }
}
