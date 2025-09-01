import java.util.List;
import java.util.HashMap;

public class Epic extends Task {
    private final List<Integer> subtaskIds;


    public Epic(String title, String description, List<Integer> subtaskIds) {
        super(title, description);
        this.subtaskIds = subtaskIds;
    }

    public Epic(String title, String description, Status status, List<Integer> subtaskIds) {
        super(title, description, status);
        this.subtaskIds = subtaskIds;
    }

    public Epic(String title, String description, int id, List<Integer> subtaskIds) {
        super(title, description, id);
        this.subtaskIds = subtaskIds;
    }

    public Epic(String title, String description, Status status, int id, List<Integer> subtaskIds) {
        super(title, description, status, id);
        this.subtaskIds = subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        TaskManager taskManager = Managers.getDefault();
        HashMap<Integer, Epic> epics = new HashMap<>();
        for (Epic epic : taskManager.getEpics()) {
            if (epic.getId() == subtaskId) {
                return;
            }
        }
        if (subtaskId != this.getId()) {
            subtaskIds.add(subtaskId);
        }
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
