import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int id = 1;

    private int generateId() {
        return id++;
    }

    public void addTask(Task task) {
        if (task.getId() == 0) {
            task.setId(generateId());
        }
        tasks.put(task.getId(), task);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task TaskById(int id) {
        return tasks.get(id);
    }

    public void deleteTask() {
        tasks.clear();
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        //эпик
    }

    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask SubtaskById(int id) {
        return subtasks.get(id);
    }

    public void deleteSubtaskById(int id){
        subtasks.remove(id);
        //эпик
    }

    public void deleteSubtask(){
        subtasks.clear();
    }

    public void updateSubtask(Subtask subtask){
        subtasks.put(subtask.getId(),subtask);
    }


//приватные функции : генерация идентификатора, удаление подзадачу айди эпика, обновление статуса эпика
}