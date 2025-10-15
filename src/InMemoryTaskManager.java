import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager;
    private int id = 1;
    private Set<Task> sortedTasks = new TreeSet<>((Task t1, Task t2) -> t1.getStartTime().compareTo(t2.getStartTime()));


    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }


    @Override
    public void addTask(Task task) {
        if (hasTimeOverlap(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующими!");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        historyManager.add(task);
        if (task.getStartTime() != null) {
            sortedTasks.add(task);
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTask(int id) {
        Task viewedTask = tasks.get(id);
        if (viewedTask != null) {
            historyManager.add(viewedTask);
        }
        return viewedTask;
    }

    @Override
    public void deleteTasks() {
        tasks.keySet().stream()
                .forEach(taskId -> {
                    sortedTasks.remove(tasks.get(taskId));
                    historyManager.remove(taskId);
                });
        tasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        sortedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (hasTimeOverlap(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующими!");
        }
        Task oldTask = tasks.get(task.getId());
        sortedTasks.remove(oldTask);
        tasks.put(task.getId(), task);
        sortedTasks.add(task);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (hasTimeOverlap(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с существующими!");
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        historyManager.add(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
        }
        if (subtask.getStartTime() != null) {
            sortedTasks.add(subtask);
        }
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask viewedSubtask = subtasks.get(id);
        if (viewedSubtask != null) {
            historyManager.add(viewedSubtask);
        }
        return viewedSubtask;
    }

    @Override
    public void deleteSubtaskById(int id) {
        sortedTasks.remove(subtasks.get(id));
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().remove(Integer.valueOf(id));
                updateEpicStatus(epic.getId());
                updateEpicTime(epic.getId());
            }
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtasks() {
        subtasks.keySet().stream()
                .forEach(subtaskId -> {
                    sortedTasks.remove(subtasks.get(subtaskId));
                    historyManager.remove(subtaskId);
                });
        subtasks.clear();
        epics.values().stream().forEach(epic -> {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
        });
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (hasTimeOverlap(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с существующими!");
        }
        Subtask oldSubtask = subtasks.get(subtask.getId());
        sortedTasks.remove(oldSubtask);
        subtasks.put(subtask.getId(), subtask);
        sortedTasks.add(subtask);
        updateEpicStatus(subtask.getEpicId());
        updateEpicTime(subtask.getEpicId());
    }

    @Override
    public void addEpic(Epic epic) {
        if (hasTimeOverlap(epic)) {
            throw new IllegalArgumentException("Эпик пересекается по времени с существующими!");
        }
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        historyManager.add(epic);
        updateEpicTime(epic.getId());
    }

    @Override
    public Epic getEpic(int id) {
        Epic viewedEpic = epics.get(id);
        if (viewedEpic != null) {
            historyManager.add(viewedEpic);
        }
        return viewedEpic;
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtaskIds().stream()
                    .forEach(subtaskId -> {
                        subtasks.remove(subtaskId);
                        historyManager.remove(subtaskId);
                    });
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().stream()
                .forEach(epicId -> historyManager.remove(epicId));
        subtasks.keySet().stream()
                .forEach(subtaskId -> historyManager.remove(subtaskId));
        subtasks.clear();
        epics.clear();
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void updateEpic(Epic epic) {
        if (hasTimeOverlap(epic)) {
            throw new IllegalArgumentException("Эпик пересекается по времени с существующими!");
        }
        epics.put(epic.getId(), epic);
        updateEpicTime(epic.getId());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> subtasksList = new ArrayList<>();
        if (epic != null) {
            epic.getSubtaskIds().stream()
                    .forEach(subtaskId -> subtasksList.add(subtasks.get(subtaskId)));
        }
        return subtasksList;
    }

    public List<Task> getPrioritizedTasks() {
        List<Task> allSortedTasks = new ArrayList<>(sortedTasks);
        return allSortedTasks;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<Subtask> subtasksOfEpic = new ArrayList<>();
        epic.getSubtaskIds().stream()
                .forEach(subtaskId -> subtasksOfEpic.add(subtasks.get(subtaskId)));

        if (subtasksOfEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (Subtask subtask : subtasksOfEpic) {
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void updateEpicTime(int id) {
        Epic epic = epics.get(id);
        epic.setStartTime(startTimeEpic(id));
        epic.setDuration(durationEpic(id));
        epic.setEndTime(endTimeEpic(id));
    }

    private Duration durationEpic(int id) {
        Duration sumSubtasks = Duration.ofMinutes(0);
        ;
        List<Integer> subtaskIds = epics.get(id).getSubtaskIds();
        for (int idSubtask : subtaskIds) {
            Duration subtaskDuration = subtasks.get(idSubtask).getDuration();
            if (subtaskDuration != null) {
                sumSubtasks = sumSubtasks.plus(subtaskDuration);
            }
        }
        return sumSubtasks;
    }

    private LocalDateTime startTimeEpic(int id) {
        List<Integer> subtaskIds = epics.get(id).getSubtaskIds();
        Optional<LocalDateTime> minStartTime = subtaskIds.stream()
                .map(idSubtask -> subtasks.get(idSubtask).getStartTime())
                .min(LocalDateTime::compareTo);
        if (!minStartTime.isEmpty()) {
            return minStartTime.get();
        } else {
            return null;
        }
    }

    private LocalDateTime endTimeEpic(int id) {
        List<Integer> subtaskIds = epics.get(id).getSubtaskIds();
        Optional<LocalDateTime> maxEndTime = subtaskIds.stream()
                .map(idSubtask -> subtasks.get(idSubtask).getEndTime())
                .max(LocalDateTime::compareTo);
        if (!maxEndTime.isEmpty()) {
            return maxEndTime.get();
        } else {
            return null;
        }
    }

    private int generateId() {
        return id++;
    }

    private boolean isTimeOverlap(Task task1, Task task2) {
        if (!(task1.getEndTime().isBefore(task2.getStartTime()) || task2.getEndTime().isBefore(task1.getStartTime()))) {
            return true;
        } else {
            return false;
        }
    }

    private boolean hasTimeOverlap(Task task) {
        if ((getPrioritizedTasks().stream()
                .filter(task1 -> isTimeOverlap(task, task1))
                .collect(Collectors.toList())).isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}