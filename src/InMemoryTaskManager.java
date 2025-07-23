import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();       // Хранит все обычные задачи
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>(); // Хранит все подзадачи
    private final HashMap<Integer, Epic> epics = new HashMap<>();       // Хранит все эпики
    private int id = 1;                                                // Счётчик для генерации ID

    // добавление новой задачи
    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }
    //получение списка всех задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    //получение задачи по id
    public Task taskById(int id) {
        return tasks.get(id);
    }
    //удаление всех задач
    public void deleteTasks() {
        tasks.clear();
    }
    //удаление задачи по ID
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }
    //обновление задачи
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    //добавление подзадачи
    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
        }
    }
    //получение списка всех подзадач
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }
    //получение подзадачи по ID
    public Subtask subtaskById(int id) {
        return subtasks.get(id);
    }
    //удаление подзадачи по ID
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().remove(Integer.valueOf(id));
                updateEpicStatus(epic.getId());
            }
        }
    }
    //удаление всех подзадач
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }
    //обновление подзадачи
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic.getId());
        }
    }

    //добавление эпика
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }
    //получение эпика по ID
    public Epic getEpic(int id) {
        return epics.get(id);
    }
    //удаление эпика по ID
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }
    //удаление всех эпиков
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.clear();
    }
    //получение списка всех эпиков
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
    //обновление эпика
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }
    //Обновление статуса эпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            ArrayList<Subtask> subtasksList = new ArrayList<>();
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasksList.add(subtasks.get(subtaskId));
            }
            if (subtasksList.isEmpty()) {
                epic.setStatus(Status.NEW);
            } else if (subtasksList.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE)) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    private int generateId() {
        return id++;
    }
}