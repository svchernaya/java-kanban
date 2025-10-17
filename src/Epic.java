import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds;
    private Type type = Type.Epic;
    private LocalDateTime endTime;

    public Epic(String title, String description, List<Integer> subtaskIds) {
        super(title, description, null, null);
        this.subtaskIds = subtaskIds;
    }

    public Epic(String title, String description, Status status, List<Integer> subtaskIds) {
        super(title, description, status, null, null);
        this.subtaskIds = subtaskIds;
    }

    public Epic(String title, String description, int id, List<Integer> subtaskIds) {
        super(title, description, id, null, null);
        this.subtaskIds = subtaskIds;
    }

    public Epic(String title, String description, Status status, int id, List<Integer> subtaskIds) {
        super(title, description, status, id, null, null);
        this.subtaskIds = subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != this.getId()) {
            subtaskIds.add(subtaskId);
        }
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds + '\'' +
                ", duration=" + getDuration().toMinutes() + '\'' +
                ", startTime=" + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                ", endTime=" + getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                '}';
    }
}
