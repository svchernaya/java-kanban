import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String filePath;

    public FileBackedTaskManager(String path) {
        this.filePath = path;
    }

    public void save() {
        try (BufferedWriter bufferHistory = new BufferedWriter(new FileWriter(filePath))) {
            bufferHistory.write("id,type,name,status,description,epic\n");

            for (Task task : super.getTasks()) {
                bufferHistory.write(toString(task));
            }
            for (Epic epic : super.getEpics()) {
                bufferHistory.write(toString(epic));
            }
            for (Subtask subtask : super.getSubtasks()) {
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
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

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

    public Task fromString(String value) {
        String[] split = value.split(",");

        int id = Integer.parseInt(split[0]);
        Type type = Type.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        switch (type) {
            case Subtask:
                return new Subtask(name, description, status, id, (int) Integer.parseInt(split[5]));
            case Task:
                return new Task(name, description, status, id);
            case Epic:
                return new Epic(name, description, status, id, new ArrayList<>());
            default:
                throw new IllegalArgumentException("Не верный тип задачи");
        }
    }

    static FileBackedTaskManager loadFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.getPath());
            {
                String header = br.readLine();
                while (br.ready()) {
                    String line = br.readLine();
                    Task task = fileBackedTaskManager.fromString(line);
                    switch (task.getType()) {
                        case Subtask:
                            fileBackedTaskManager.addSubtask((Subtask) task);
                            break;
                        case Task:
                            fileBackedTaskManager.addTask(task);
                            break;
                        case Epic:
                            fileBackedTaskManager.addEpic((Epic) task);
                            break;
                        default:
                            System.out.println("В файле присутствует задача несуществующего типа");
                    }
                }
                return fileBackedTaskManager;

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}