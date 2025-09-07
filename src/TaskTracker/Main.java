package TaskTracker;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Task task1 = new Task("Задача 1", "тестируем проект", 1,
                Task.Status.IN_PROGRESS);
        Task task2 = new Task("Задача 2", "продолжаем тестировать", 2,
                Task.Status.NEW);

        Epic epic1 = new Epic("Эпик 1", "testDescription1", 3,
                Task.Status.NEW);

        Epic epic2 = new Epic("Эпик 2", "testDescription2", 4,
                Task.Status.IN_PROGRESS);

        TaskManager taskManager = new TaskManager("задача", "описание",
                0, Task.Status.NEW);

        Subtask firstSubtaskForEpic1 = new Subtask("подзадача1", "описание", 5,
                Task.Status.IN_PROGRESS);
        Subtask secondSubtaskForEpic1 = new Subtask("подзадача2", "описание",
                6, Task.Status.DONE);

        ArrayList<Subtask> tasksInEpic1 = new ArrayList<>();
        tasksInEpic1.add(firstSubtaskForEpic1);
        tasksInEpic1.add(secondSubtaskForEpic1);


        taskManager.createNewEpic(tasksInEpic1);


        Subtask firstSubtaskForEpic2 = new Subtask("подзадача1", "описание",
                6, Task.Status.DONE);
        Subtask secondSubtaskForEpic2 = new Subtask("подзадача2", "описание",
                7, Task.Status.DONE);

        ArrayList<Subtask> tasksInEpic2 = new ArrayList<>();
        tasksInEpic2.add(firstSubtaskForEpic2);
        tasksInEpic2.add(secondSubtaskForEpic2);

        taskManager.createNewEpic(tasksInEpic2);

        taskManager.createNewSubtask(firstSubtaskForEpic2);
        taskManager.createNewSubtask(secondSubtaskForEpic2);

        taskManager.getSubtaskList();

        taskManager.deleteSubtask(7);

        taskManager.deleteEpic(3);

        taskManager.removeAllTasks();

        firstSubtaskForEpic1.setStatus(Task.Status.NEW);
        secondSubtaskForEpic2.setStatus(Task.Status.NEW);

        taskManager.deleteSubtask(6);

    }

}
