import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
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

    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;
    Subtask subtask4;

    @BeforeEach
    void setUp() {
        taskManager.addEpic(epic1);

        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId(), duration1, startTime1);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId(), duration2, startTime2);
        subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId(), duration3, startTime3);
        subtask4 = new Subtask("Подзадача 4", "Описание подзадачи 4", epic1.getId(), duration4, startTime4);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);
    }

    @Test
    void shouldCalculateEpicTimeFieldsCorrectly() {
        Duration durationSubtasks = duration1.plus(duration2).plus(duration3).plus(duration4);
        LocalDateTime startTimeSubtasks = Stream.of(startTime1, startTime2, startTime3, startTime4)
                .min(LocalDateTime::compareTo)
                .get();

        LocalDateTime endTimeSubtasks = Stream.of(subtask1.getEndTime(), subtask2.getEndTime(), subtask3.getEndTime(), subtask4.getEndTime())
                .max(LocalDateTime::compareTo)
                .get();

        assertEquals(durationSubtasks, epic1.getDuration());
        assertEquals(startTimeSubtasks, epic1.getStartTime());
        assertEquals(endTimeSubtasks, epic1.getEndTime());

    }

    @Test
    void epicCannotBeItsOwnSubtask() {
        epic1.addSubtaskId(epic1.getId());
        assertFalse(epic1.getSubtaskIds().contains(epic1.getId()));
    }

    @Test
    void epicStatusShouldBeNewWhenAllSubtasksNew() {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        subtask3.setStatus(Status.NEW);
        subtask4.setStatus(Status.NEW);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        taskManager.updateSubtask(subtask4);

        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    void epicStatusShouldBeDoneWhenAllSubtasksDone() {
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);
        subtask4.setStatus(Status.DONE);

        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);
        taskManager.updateSubtask(subtask4);

        assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressWhenSubtasksNewAndDone() {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.NEW);
        subtask4.setStatus(Status.NEW);

        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);
        taskManager.updateSubtask(subtask4);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressWhenAnySubtaskInProgress() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.NEW);
        subtask3.setStatus(Status.DONE);
        subtask4.setStatus(Status.DONE);

        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);
        taskManager.updateSubtask(subtask4);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

}
