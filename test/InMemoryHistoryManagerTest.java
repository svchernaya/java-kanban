import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();

    Task task1 = new Task("Задача 1", "Описание задачи 1");
    Epic epic1 = new Epic("Эпик 1", "Описание", new ArrayList<>());
    Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1.getId());

    List<Task> viewedTasksHistory = new ArrayList<>();

    @Test
    public void shouldKeepPreviousTaskVersionInHistory() {
        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);

        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask1.getId());

        viewedTasksHistory.add(task1);
        viewedTasksHistory.add(epic1);
        viewedTasksHistory.add(subtask1);

        assertEquals(viewedTasksHistory, taskManager.getHistory());
    }

    @Test
    void historyShouldNotExceedMaxSize() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Задача " + i, "Описание " + i, i);
            taskManager.addTask(task);
            taskManager.getTask(task.getId());
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(10, history.size(), "История не должна превышать 10 элементов.");
        assertEquals(6, history.get(0).getId(), "Старые задачи должны удаляться.");
    }
}