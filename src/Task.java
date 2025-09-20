import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;
    private Type type = Type.Task;


    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description, int id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String title, String description, Status status, int id) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    void setStatus(Status status) {
        this.status = status;
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}