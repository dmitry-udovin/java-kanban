package tasktracker;

import tasktracker.managers.Managers;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task(
                "Задача 1",
                "описание 1",
                Task.Status.NEW,
                Optional.of(LocalDateTime.of(2025, 1, 1, 9, 0)),
                Duration.ofMinutes(60)          // 09:00–10:00
        );
        int task1Id = taskManager.createNewTask(task1);

        Task task2 = new Task(
                "Задача 2",
                "описание 2",
                Task.Status.IN_PROGRESS,
                Optional.of(LocalDateTime.of(2025, 1, 1, 10, 30)),
                Duration.ofMinutes(30)          // 10:30–11:00 (не пересекается с task1)
        );
        int task2Id = taskManager.createNewTask(task2);

        // --- создаём эпики ---

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1", 0);
        int epic1Id = taskManager.createNewEpic(epic1);

        Epic epic2 = new Epic("Эпик 2", "описание эпика 2", 0);
        int epic2Id = taskManager.createNewEpic(epic2);

        // --- подзадачи для эпика 1 ---

        Subtask firstSubtaskForEpic1 = new Subtask(
                "подзадача 1 э1",
                "описание подзадачи 1",
                Task.Status.NEW,
                epic1Id,
                Optional.of(LocalDateTime.of(2025, 1, 1, 12, 0)),
                Duration.ofMinutes(30)          // 12:00–12:30
        );
        int sub1Epic1Id = taskManager.createNewSubtask(firstSubtaskForEpic1);

        Subtask secondSubtaskForEpic1 = new Subtask(
                "подзадача 2 э1",
                "описание подзадачи 2",
                Task.Status.DONE,
                epic1Id,
                Optional.of(LocalDateTime.of(2025, 1, 1, 13, 0)),
                Duration.ofMinutes(45)          // 13:00–13:45
        );
        int sub2Epic1Id = taskManager.createNewSubtask(secondSubtaskForEpic1);

        // --- подзадачи для эпика 2 ---

        Subtask firstSubtaskForEpic2 = new Subtask(
                "подзадача 1 э2",
                "описание подзадачи 1",
                Task.Status.NEW,
                epic2Id,
                Optional.of(LocalDateTime.of(2025, 1, 2, 10, 0)),
                Duration.ofMinutes(30)
        );
        int sub1Epic2Id = taskManager.createNewSubtask(firstSubtaskForEpic2);

        Subtask secondSubtaskForEpic2 = new Subtask(
                "подзадача 2 э2",
                "описание подзадачи 2",
                Task.Status.IN_PROGRESS,
                epic2Id,
                Optional.of(LocalDateTime.of(2025, 1, 2, 11, 0)),
                Duration.ofMinutes(30)
        );
        int sub2Epic2Id = taskManager.createNewSubtask(secondSubtaskForEpic2);

        // заполняем историю
        taskManager.getTaskWithID(task1Id);
        taskManager.getEpictaskWithID(epic1Id);
        taskManager.getSubtaskWithID(sub1Epic1Id);
        taskManager.getTaskWithID(task2Id);
        taskManager.getSubtaskWithID(sub2Epic2Id);

        // выводим всё текущее состояние
        printAllTasks(taskManager);

        System.out.println("\n--- удаляем все обычные задачи ---");
        taskManager.removeAllTasks();
        printAllTasks(taskManager);

        System.out.println("\n--- удаляем все подзадачи и эпики ---");
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpicTasks();
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\n======================================");
        System.out.println("ТЕКУЩЕЕ СОСТОЯНИЕ МЕНЕДЖЕРА");
        System.out.println("======================================");

        System.out.println("Задачи:");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }

        System.out.println("\nЭпики и их подзадачи:");
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic);
            for (Subtask sub : manager.getSubtasksFromEpic(epic.getTaskId())) {
                System.out.println("  └── " + sub);
            }
        }

        System.out.println("\nВсе подзадачи:");
        for (Subtask sub : manager.getSubtaskList()) {
            System.out.println(sub);
        }

        System.out.println("\nЗадачи по приоритету (startTime):");
        for (Task task : manager.getPrioritizedTasks()) {
            System.out.println(task.getTaskName() + " | start=" +
                    task.getStartTime().map(LocalDateTime::toString).orElse("нет") +
                    " | duration=" + task.getDuration().toMinutes() + " мин");
        }

        System.out.println("\nИстория просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("======================================\n");
    }
}