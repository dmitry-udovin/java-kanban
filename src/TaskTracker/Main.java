package TaskTracker;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Task task1 = new Task("Задача 1", "тестовое описание", Task.Status.IN_PROGRESS);
        Task task2 = new Task("Задача 2", "тестовое описание", Task.Status.NEW);
        Epic epic1 = new Epic("Эпик 1", "тестовое описание", Task.Status.NEW);
        Epic epic2 = new Epic("Эпик 2", "тестовое описание",
                Task.Status.IN_PROGRESS);

        TaskManager taskManager = new TaskManager("задача", "описание",
                Task.Status.NEW);

        Subtask firstSubtaskForEpic1 = new Subtask("подзадача1", "описание",
                Task.Status.IN_PROGRESS);
        Subtask secondSubtaskForEpic1 = new Subtask("подзадача2", "описание",
                Task.Status.DONE);


        ArrayList<Subtask> tasksInEpic1 = new ArrayList<>();
        tasksInEpic1.add(firstSubtaskForEpic1);
        tasksInEpic1.add(secondSubtaskForEpic1);

        taskManager.createNewEpic(epic1, tasksInEpic1);


        Subtask firstSubtaskForEpic2 = new Subtask("подзадача1", "описание",
                Task.Status.DONE);
        Subtask secondSubtaskForEpic2 = new Subtask("подзадача2", "описание",
                Task.Status.DONE);

        ArrayList<Subtask> tasksInEpic2 = new ArrayList<>();

        tasksInEpic2.add(firstSubtaskForEpic2);
        tasksInEpic2.add(secondSubtaskForEpic2);

        taskManager.createNewEpic(epic2, tasksInEpic2);

        taskManager.getSubtaskList();

        taskManager.createNewSubtask(firstSubtaskForEpic1, epic1);
        taskManager.createNewSubtask(secondSubtaskForEpic1, epic1);

        taskManager.getSubtaskList();


    }

}
