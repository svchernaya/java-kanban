import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    List<Task> getTasks();

    Task getTask(int id);

    void deleteTasks();

    void deleteTaskById(int id);

    void updateTask(Task task);

    void addSubtask(Subtask subtask);

    List<Subtask> getSubtasks();

    Subtask getSubtask(int id);

    void deleteSubtaskById(int id);

    void deleteSubtasks();

    void updateSubtask(Subtask subtask);

    void addEpic(Epic epic);

    Epic getEpic(int id);

    void deleteEpicById(int id);

    void deleteAllEpics();

    List<Epic> getEpics();

    void updateEpic(Epic epic);

    List<Task> getHistory();

    List<Subtask> getEpicSubtasks(int epicId);
}