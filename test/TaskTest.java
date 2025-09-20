import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TaskTest {
    TaskManager taskManager = Managers.getDefault();

    Task task1 = new Task("Задача 1", "Описание задачи 1");
    Task task2 = new Task("Задача 1", "Описание задачи 1");

    @Test
    void shouldReturnTrueWhenTasksHaveSameId() {
        assertEquals(task1, task2);
    }
}
