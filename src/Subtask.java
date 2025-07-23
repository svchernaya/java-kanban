public class Subtask extends Task {
    private int epicId;


    public Subtask(String title, String description, int epicId) {
        super(title, description);
        if (epicId != this.getId()) {
            this.epicId = epicId;
        }
    }

    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        if (epicId != this.getId()) {
            this.epicId = epicId;
        }
    }

    public Subtask(String title, String description, int id, int epicId) {
        super(title, description, id);
        if (epicId != this.getId()) {
            this.epicId = epicId;
        }
    }

    public Subtask(String title, String description, Status status, int id, int epicId) {
        super(title, description, status, id);
        if (epicId != this.getId()) {
            this.epicId = epicId;
        }
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId != this.getId()) {
            this.epicId = epicId;
        }
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}