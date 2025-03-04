import java.util.ArrayList;

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
        subtaskIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }
}
