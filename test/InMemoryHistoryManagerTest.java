import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();

    Duration durationForSubtask = Duration.ofMinutes(50);
    LocalDateTime startTime = LocalDateTime.of(2024, Month.FEBRUARY, 10, 4, 50);
    Duration durationForTask = Duration.ofMinutes(20);
    LocalDateTime startTimeForTask = LocalDateTime.of(2025, Month.MARCH, 5, 2, 30);

    Epic epic1 = new Epic("Эпик 1", "Описание", new ArrayList<>());
    Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1.getId(), durationForSubtask, startTime);
    Task task1 = new Task("Задача 1", "Описание задачи 1", durationForTask, startTimeForTask);

    List<Task> viewedTasksHistory = new ArrayList<>();

    @Test
    public void shouldBeSavedTaskToHistory() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addTask(task1);

        viewedTasksHistory.add(epic1);
        viewedTasksHistory.add(subtask1);
        viewedTasksHistory.add(task1);

        assertEquals(viewedTasksHistory, taskManager.getHistory());
    }

    @Test
    public void ShouldBeDeletedOldVersionOfTask() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addTask(task1);

        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask1.getId());

        viewedTasksHistory.clear();
        viewedTasksHistory.add(task1);
        viewedTasksHistory.add(epic1);
        viewedTasksHistory.add(subtask1);

        assertEquals(viewedTasksHistory, taskManager.getHistory());
    }

    @Test
    public void ShouldBeDeletedTask() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addTask(task1);

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteSubtaskById(subtask1.getId());

        viewedTasksHistory.clear();
        viewedTasksHistory.add(epic1);

        assertEquals(viewedTasksHistory, taskManager.getHistory());
    }

    @Test
    public void shouldKeepUpdatedVersionInHistory() {
        taskManager.addTask(task1);
        taskManager.getTask(task1.getId());

        task1.setTitle("Новая");

        taskManager.updateTask(task1);
        taskManager.getTask(task1.getId());

        assertEquals("Новая", taskManager.getHistory().get(0).getTitle());
    }

    @Test
    public void shouldClearHistoryAfterDeletingAllTasks() {
        taskManager.addTask(task1);
        taskManager.getTask(task1.getId());

        taskManager.deleteTasks();

        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void testHistoryRemovesDuplicates() {
        taskManager.addTask(task1);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task1.getId());

        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    void shouldReturnEmptyHistoryWhenNoTasksViewed() {
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void shouldRemoveTaskFromBeginningOfHistory() {
        Task task1 = new Task("Задача 1", "Описание 1", durationForTask, startTimeForTask);
        Task task2 = new Task("Задача 2", "Описание 2", durationForTask, startTimeForTask.plusHours(1));
        Task task3 = new Task("Задача 3", "Описание 3", durationForTask, startTimeForTask.plusHours(2));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());

        taskManager.deleteTaskById(task1.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void shouldRemoveTaskFromMiddleOfHistory() {
        Task task1 = new Task("Задача 1", "Описание 1", durationForTask, startTimeForTask);
        Task task2 = new Task("Задача 2", "Описание 2", durationForTask, startTimeForTask.plusHours(1));
        Task task3 = new Task("Задача 3", "Описание 3", durationForTask, startTimeForTask.plusHours(2));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());

        taskManager.deleteTaskById(task2.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void shouldRemoveTaskFromEndOfHistory() {
        Task task1 = new Task("Задача 1", "Описание 1", durationForTask, startTimeForTask);
        Task task2 = new Task("Задача 2", "Описание 2", durationForTask, startTimeForTask.plusHours(1));
        Task task3 = new Task("Задача 3", "Описание 3", durationForTask, startTimeForTask.plusHours(2));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());

        taskManager.deleteTaskById(task3.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }
}