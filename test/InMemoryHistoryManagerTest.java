import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    Epic epic1 = new Epic("Эпик 1", "Описание", new ArrayList<>());
    Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1.getId());
    Task task1 = new Task("Задача 1", "Описание задачи 1");

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
    public void ShouldBeDeletedOldVersionOfTask(){
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
    public void ShouldBeDeletedTask(){
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
}