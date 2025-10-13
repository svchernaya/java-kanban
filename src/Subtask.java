import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;
    private Type type = Type.Subtask;

    public Subtask(String title, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(title, description, duration, startTime);
        if (epicId != this.getId()) {
            this.epicId = epicId;
        }
    }

    public Subtask(String title, String description, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        super(title, description, status, duration, startTime);
        if (epicId != this.getId()) {
            this.epicId = epicId;
        }
    }

    public Subtask(String title, String description, int id, int epicId, Duration duration, LocalDateTime startTime) {
        super(title, description, id, duration, startTime);
        if (epicId != this.getId()) {
            this.epicId = epicId;
        }
    }

    public Subtask(String title, String description, Status status, int id, int epicId, Duration duration, LocalDateTime startTime) {
        super(title, description, status, id, duration, startTime);
        if (epicId != this.getId()) {
            this.epicId = epicId;
        }
    }

    public int getEpicId() {
        return epicId;
    }

    void setEpicId(int epicId) {
        if (epicId != this.getId()) {
            this.epicId = epicId;
        }
    }

    @Override
    public String toCsvString() {
        String line = getId() + ","
                + getType() + ","
                + getTitle() + ","
                + getStatus() + ","
                + getDescription() + ","
                + epicId + ","
                + getDuration() + ","
                + getStartTime() + "\n";

        return line;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", duration=" + getDuration().toMinutes() + '\'' +
                ", startTime=" + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + '\'' +
                ", endTime=" + getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                '}';
    }
}