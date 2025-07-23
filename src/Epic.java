import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task{
    private final ArrayList<Integer> subtaskIds;


    public Epic(String title, String description, ArrayList<Integer> subtaskIds) {
        super(title, description);
        this.subtaskIds = subtaskIds;
    }

    public Epic(String title, String description, Status status, ArrayList<Integer> subtaskIds) {
        super(title, description, status);
        this.subtaskIds = subtaskIds;
    }

    public Epic(String title, String description, int id, ArrayList<Integer> subtaskIds) {
        super(title, description, id);
        this.subtaskIds = subtaskIds;
    }

    public Epic(String title, String description, Status status, int id, ArrayList<Integer> subtaskIds) {
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

    public ArrayList<Integer> getSubtaskIds() {
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
