import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    Subtask subtask1 = new Subtask("Подзача 1","Описание подзадачи 1",2);
    Subtask subtask2 = new Subtask("Подзача 1","Описание подзадачи 1",2);

    @Test
    void shouldReturnTrueWhenTasksHaveSameId() {
        assertEquals(subtask1,subtask2);
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic(){
        subtask1.setEpicId(subtask1.getId());
        assertNotEquals(subtask1.getEpicId(),subtask1.getId());
    }
}
