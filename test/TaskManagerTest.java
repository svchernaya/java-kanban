import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected abstract T createTaskManager();

    protected Duration durationForSubtask1 = Duration.ofMinutes(50);
    protected LocalDateTime startTime1 = LocalDateTime.of(2024, Month.FEBRUARY, 10, 4, 50);
    protected Duration durationForSubtask2 = Duration.ofMinutes(120);
    protected LocalDateTime startTime2 = LocalDateTime.of(2021, Month.SEPTEMBER, 21, 20, 33);
    protected Duration durationForTask = Duration.ofMinutes(20);
    protected LocalDateTime startTimeForTask = LocalDateTime.of(2025, Month.MARCH, 5, 2, 30);

    @Test
    void testAddDifferentTaskTypes() {
        TaskManager taskManager = createTaskManager();
        Task task = new Task("Задача 1", "Описание задачи 1", durationForTask, startTimeForTask);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", new ArrayList<>());

        taskManager.addTask(task);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId(), durationForSubtask1, startTime1);
        taskManager.addSubtask(subtask1);

        assertNotNull(taskManager.getTask(task.getId()));
        assertNotNull(taskManager.getEpic(epic.getId()));
        assertNotNull(taskManager.getSubtask(subtask1.getId()));
    }

    @Test
    void shouldPreserveTaskFieldsWhenAddedToManager() {
        TaskManager taskManager = createTaskManager();
        Task task = new Task("Задача 1", "Описание задачи 1", durationForTask, startTimeForTask);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", new ArrayList<>());

        taskManager.addTask(task);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId(), durationForSubtask1, startTime1);
        taskManager.addSubtask(subtask1);

        assertEquals(task, taskManager.getTask(task.getId()));
        assertEquals(epic, taskManager.getEpic(epic.getId()));
        assertEquals(subtask1, taskManager.getSubtask(subtask1.getId()));
    }

    @Test
    void deletingEpicShouldDeleteSubtasks() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId(), durationForSubtask1, startTime1);
        taskManager.addSubtask(subtask1);

        taskManager.deleteEpicById(epic.getId());
        assertNull(taskManager.getSubtask(subtask1.getId()));
    }

    @Test
    void shouldUpdateTaskStatusCorrectlyThroughUpdateMethod() {
        TaskManager taskManager = createTaskManager();
        Task task = new Task("Задача", "Описание", durationForTask, startTimeForTask);
        taskManager.addTask(task);

        assertEquals(Status.NEW, taskManager.getTask(task.getId()).getStatus());

        Task updatedTask = new Task("Задача", "Описание", Status.DONE, task.getId(), durationForTask, startTimeForTask);
        taskManager.updateTask(updatedTask);

        assertEquals(Status.DONE, taskManager.getTask(task.getId()).getStatus());
    }

    @Test
    void epicShouldNotContainStaleSubtaskIdsAfterRemoval() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic.getId(), durationForSubtask1, startTime1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", epic.getId(), durationForSubtask2, startTime2);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.deleteSubtaskById(subtask1.getId());

        assertFalse(epic.getSubtaskIds().contains(subtask1.getId()));
        assertTrue(epic.getSubtaskIds().contains(subtask2.getId()));
    }

    @Test
    void subtaskShouldHaveValidEpic() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Эпик", "Описание эпика", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId(), durationForSubtask1, startTime1);
        taskManager.addSubtask(subtask);

        assertEquals(epic.getId(), subtask.getEpicId(), "EpicId подзадачи должен совпадать с id эпика");
    }

    @Test
    void shouldNotAllowTasksWithTimeOverlap() {
        TaskManager taskManager = createTaskManager();

        Task task1 = new Task("Задача 1", "Описание", Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0));
        Task task2 = new Task("Задача 2", "Описание", Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 10, 30));

        taskManager.addTask(task1);
        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addTask(task2);
        }, "Должна быть ошибка при пересечении времени задач");
    }

    @Test
    void shouldAllowTasksWithoutTimeOverlap() {
        TaskManager taskManager = createTaskManager();

        Task task1 = new Task("Задача 1", "Описание", Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0));
        Task task2 = new Task("Задача 2", "Описание", Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JANUARY, 1, 11, 30));

        taskManager.addTask(task1);

        assertDoesNotThrow(() -> {
            taskManager.addTask(task2);
        }, "Не должно быть ошибки при непересекающемся времени");
        assertEquals(2, taskManager.getTasks().size());
    }
}