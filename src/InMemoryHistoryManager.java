import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private static final int MAX_HISTORY_SIZE = 10;
    private final List<Task> viewedTasksHistory = new ArrayList<>();


    @Override
    public void add(Task task){
        viewedTasksHistory.remove(task);

        viewedTasksHistory.add(task);

        if (viewedTasksHistory.size() > MAX_HISTORY_SIZE) {
            viewedTasksHistory.remove(0);
        }
    }

    @Override
    public List<Task> getHistory(){
        return viewedTasksHistory;
    }
}