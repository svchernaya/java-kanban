import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", new ArrayList<>());
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        taskManager.addSubtask(subtask1);

        System.out.println("Статус эпика 1: " + taskManager.getEpic(epic1.getId()).getStatus());

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        System.out.println("Статус эпика 1 после завершения подзадачи: " + taskManager.getEpic(epic1.getId()).getStatus());

        taskManager.deleteTaskById(task1.getId());
        System.out.println("Задачи после удаления задачи 1: " + taskManager.getAllTasks());

        System.out.println("Все подзадачи: " + taskManager.getAllSubtask());
        System.out.println("Все эпики: " + taskManager.getAllEpics());
    }
}
