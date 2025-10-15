import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.stream.Stream;

class EpicTest {
    TaskManager taskManager = Managers.getDefault();

    Duration duration1 = Duration.ofMinutes(20);
    Duration duration2 = Duration.ofMinutes(50);
    Duration duration3 = Duration.ofMinutes(60);
    Duration duration4 = Duration.ofMinutes(120);

    LocalDateTime startTime1 = LocalDateTime.of(2025, Month.MARCH, 5, 2, 30);
    LocalDateTime startTime2 = LocalDateTime.of(2024, Month.MARCH, 21, 10, 26);
    LocalDateTime startTime3 = LocalDateTime.of(2023, Month.MARCH, 12, 22, 11);
    LocalDateTime startTime4 = LocalDateTime.of(2025, Month.FEBRUARY, 7, 5, 59);

    Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", new ArrayList<>());
    Epic epic2 = new Epic("Эпик 1", "Описание эпика 1", new ArrayList<>());

    @Test
    void shouldCalculateEpicTimeFieldsCorrectly() {
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId(), duration1, startTime1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId(), duration2, startTime2);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId(), duration3, startTime3);
        Subtask subtask4 = new Subtask("Подзадача 4", "Описание подзадачи 4", epic1.getId(), duration4, startTime4);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);

        Duration durationSubtasks = duration1.plus(duration2).plus(duration3).plus(duration4);
        LocalDateTime startTimeSubtasks = Stream.of(startTime1, startTime2, startTime3, startTime4)
                .min(LocalDateTime::compareTo)
                .get();

        LocalDateTime endTimeSubtasks = Stream.of(subtask1.getEndTime(), subtask2.getEndTime(), subtask3.getEndTime(), subtask4.getEndTime())
                .max(LocalDateTime::compareTo)
                .get();

        assertEquals(durationSubtasks,epic1.getDuration());
        assertEquals(startTimeSubtasks,epic1.getStartTime());
        assertEquals(endTimeSubtasks,epic1.getEndTime());

    }

    @Test
    void shouldNotAllowToAddEpicAsSubtaskToAnotherEpic() {
        int originalSubtaskIdsCount = epic1.getSubtaskIds().size();
        epic1.addSubtaskId(epic2.getId());

        assertEquals(epic1.getSubtaskIds().size(), originalSubtaskIdsCount);
    }

    @Test
    void epicCannotBeItsOwnSubtask() {
        epic1.addSubtaskId(epic1.getId());
        assertFalse(epic1.getSubtaskIds().contains(epic1.getId()));
    }

}
