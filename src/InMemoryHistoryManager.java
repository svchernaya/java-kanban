import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager{
    private Node head;
    private Node tail;
    private int size = 0;
    private Map<Integer,Node> historyMap = new HashMap<>();

    class Node {

        public Task task;
        public Node next;
        public Node prev;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void add(Task task){
        linkLast(task);
    }

    @Override
    public List<Task> getHistory(){
        return getTasks();
    }

    public void linkLast(Task task){
        if (historyMap.containsKey(task.getId()))
            remove(task.getId());
        final Node oldTail = tail;
        final Node newTail = new Node(oldTail,task,null);
        tail = newTail;
        if (oldTail == null)
            head = newTail;
        else
            oldTail.next = newTail;
        size++;
        historyMap.put(task.getId(),newTail);
    }

    public List<Task> getTasks(){
        List<Task> historyList = new ArrayList<>();
        Node current = head;
        while (!(current == null)){
            historyList.add(current.task);
            current = current.next;
        }
        return historyList;
    }


    @Override
    public void remove(int id) {
        if (!historyMap.containsKey(id)) {
            return;
        }
        Node nodeToRemove = historyMap.get(id);
        removeNode(nodeToRemove);
        historyMap.remove(id);
        size--;
    }

    private void removeNode(Node nodeToRemove) {
        if (nodeToRemove.prev != null) {
            nodeToRemove.prev.next = nodeToRemove.next;
        } else {
            head = nodeToRemove.next;
        }

        if (nodeToRemove.next != null) {
            nodeToRemove.next.prev = nodeToRemove.prev;
        } else {
            tail = nodeToRemove.prev;
        }

        nodeToRemove.prev = null;
        nodeToRemove.next = null;
        nodeToRemove.task = null;
    }

}