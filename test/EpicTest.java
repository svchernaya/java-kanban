import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

class EpicTest {
    Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", new ArrayList<>());
    Epic epic2 = new Epic("Эпик 1", "Описание эпика 1", new ArrayList<>());
    Subtask subtask1 = new Subtask("Подзадача 1","Описание подзадачи 1",epic1.getId());
    Subtask subtask2 = new Subtask("Подзадача 2","Описание подзадачи 2",epic2.getId());


    @Test
    void shouldReturnTrueWhenTasksHaveSameId() {
        epic1.addSubtaskId(subtask1.getId());
        epic2.addSubtaskId(subtask2.getId());

        assertEquals(epic1,epic2);
    }

    @Test
    void shouldNotAllowToAddEpicAsSubtaskToAnotherEpic(){
        int originalSubtaskIdsCount = epic1.getSubtaskIds().size();
        epic1.addSubtaskId(epic2.getId());

        assertEquals(epic1.getSubtaskIds().size(),originalSubtaskIdsCount);
    }

    @Test
    void epicCannotBeItsOwnSubtask() {
        epic1.addSubtaskId(epic1.getId());
        assertFalse(epic1.getSubtaskIds().contains(epic1.getId()));
    }

}
