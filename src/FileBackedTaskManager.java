import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileBackedTaskManager extends InMemoryTaskManager{
    FileWriter fileHistory;

    public FileBackedTaskManager(FileWriter fileHistory) {
        this.fileHistory = fileHistory;
    }

    public void save(){
        try (BufferedWriter bufferHistory = new BufferedWriter(fileHistory)){
            bufferHistory.write("id,type,name,status,description,epic");

            for (Task task : super.getTasks()){
                bufferHistory.write(toString(task));
            }
            for (Epic epic : super.getEpics()){
                bufferHistory.write(toString(epic));
            }
            for (Subtask subtask : super.getSubtasks()){
                bufferHistory.write(toString(subtask));
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка автосохранения");
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void deleteTasks(){
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id){
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task){
        super.updateTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask){
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id){
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteSubtasks(){
        super.deleteSubtasks();
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask){
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic){
        super.addEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id){
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics(){
        super.deleteAllEpics();
        save();
    }

    @Override
    public void updateEpic(Epic epic){
        super.updateEpic(epic);
        save();
    }

    @Override
    public String toString(Task task) {
        String line = task.getId() + ","
                + task.getType() + ","
                + task.getTitle() + ","
                + task.getStatus() + ","
                + task.getDescription();

        if (task.getType() == Type.Subtask) {
            Subtask subtask = (Subtask) task;
            return line + "," + subtask.getEpicId() + "\n";
        } else {
            return line + "\n";
        }
    }
}
