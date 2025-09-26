package TaskTracker;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");


        Task task1 = new Task("Задача 1","описание1", Task.Status.DONE,0);
        Task task2 = new Task("Задача 2","описание2", Task.Status.NEW,1);

        Epic epic1 = new Epic("Эпик 1","описание3", Task.Status.NEW, 2);
        Epic epic2 = new Epic("Эпик 2","описание4", Task.Status.DONE, 3);

        TaskManager taskManager = Managers.getDefault();

        Subtask firstSubtaskForEpic1 = new Subtask("подзадача 1","описание подзадачи",
                Task.Status.DONE,2);

        Subtask secondSubtaskForEpic1 = new Subtask("подзадача 2","описание подзадачи",
                Task.Status.IN_PROGRESS,2);

        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);

        taskManager.createNewEpic(epic1);

        taskManager.createNewSubtask(firstSubtaskForEpic1);
        taskManager.createNewSubtask(secondSubtaskForEpic1);

        taskManager.getSubtaskList();

        Subtask firstSubtaskForEpic2 = new Subtask("подзадача 1","описание подзадачи",
                Task.Status.DONE,3);

        Subtask secondSubtaskForEpic2 = new Subtask("подзадача 2", "описание подзадачи",
                Task.Status.IN_PROGRESS,3);

        taskManager.createNewEpic(epic2);

        taskManager.createNewSubtask(firstSubtaskForEpic2);
        taskManager.createNewSubtask(secondSubtaskForEpic2);

        taskManager.getSubtaskList();

        System.out.println();
        System.out.println("вывод информации - задача/эпик/подзадача");
        System.out.println("---------------------------------------");
        System.out.println(task1);
        System.out.println("---------------------------------------");
        System.out.println(task2);
        System.out.println("---------------------------------------");
        System.out.println(epic1);
        System.out.println("---------------------------------------");
        System.out.println(epic2);
        System.out.println("---------------------------------------");
        System.out.println(firstSubtaskForEpic1);
        System.out.println("---------------------------------------");
        System.out.println(secondSubtaskForEpic1);
        System.out.println("---------------------------------------");
        System.out.println(firstSubtaskForEpic2);
        System.out.println("---------------------------------------");
        System.out.println(secondSubtaskForEpic2);

        System.out.println();


        System.out.println("---------------------------------------");
        System.out.println("Вывожу список всех задач:");

        System.out.println(taskManager.getTaskList());


        System.out.println("---------------------------------------");
        System.out.println("Вывожу список всех подзадач:");

        System.out.println(taskManager.getSubtaskList());

        System.out.println("---------------------------------------");
        System.out.println("Вывожу список всех эпиков:");

        System.out.println(taskManager.getEpicList());

        System.out.println("---------------------------------------");
        System.out.println("после очистки списка: ");
        taskManager.removeAllTasks();
        System.out.println(taskManager.getTaskList());

        System.out.println("---------------------------------------");

        taskManager.getEpictaskWithID(2);

        taskManager.removeAllSubtasks();
        taskManager.removeAllEpicTasks();

        

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpicList()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksFromEpic(epic.getTaskId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtaskList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getManager().getHistory()) {
            System.out.println(task);
        }
    }

}
