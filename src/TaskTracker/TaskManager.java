package TaskTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class TaskManager extends TaskTracker.Task {

    ArrayList<Set<Integer>> taskKeys = new ArrayList<>();
    ArrayList<Set<Integer>> subtaskKeys = new ArrayList<>();
    ArrayList<Set<Integer>> epicKeys = new ArrayList<>();

    public TaskManager(String taskName, String taskDescription, Status status) {
        super(taskName, taskDescription, status);
    }

    private static int taskID = 0;
    static HashMap<Integer, Task> taskHashMap = new HashMap<>();
    static HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    static HashMap<Integer, ArrayList<Subtask>> epicHashMap = new HashMap<>();

    // ПОЛУЧЕНИЕ СПИСКОВ ЗАДАЧ:

    public ArrayList<Task> getTaskList() {

        ArrayList<Task> taskList = new ArrayList<>();
        for (Task task : taskHashMap.values()) {
            taskList.add(task);
        }
        return taskList; // вернул список задач в виде обьекта

    }

    public ArrayList<Subtask> getSubtaskList() {

        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : subtaskHashMap.values()) {
            subtaskList.add(subtask);
        }

        return subtaskList; // вернул список задач в виде обьекта

    }

    public ArrayList<ArrayList<Subtask>> getEpicList() {

        ArrayList<ArrayList<Subtask>> epicList = new ArrayList<>();
        for (ArrayList<Subtask> epic : epicHashMap.values()) {
            epicList.add(epic);
        }

        return epicList; // вернул список задач в виде обьекта

    }

    // УДАЛЕНИЕ ВСЕХ ЗАДАЧ:

    public void removeAllTasks() {
        taskKeys.add(taskHashMap.keySet());
        for (Set<Integer> key : taskKeys) {
            taskHashMap.remove(key);
        }
    }

    public void removeAllSubtasks() {
        subtaskKeys.add(subtaskHashMap.keySet());
        for (Set<Integer> key : subtaskKeys) {
            subtaskHashMap.remove(key);
        }
    }

    public void removeAllEpicTasks() {
        epicKeys.add(epicHashMap.keySet());
        for (Set<Integer> key : epicKeys) {
            epicHashMap.remove(key);
        }
    }

    // ПОЛУЧЕНИЕ ЗАДАЧИ ПО ИДЕНТИФИКАТОРУ:

    public Task getTaskWithID(int taskID) {
        return taskHashMap.get(taskID);
    }

    public Subtask getSubtaskWithID(int subtaskID) {
        return subtaskHashMap.get(subtaskID);
    }

    public ArrayList<Subtask> getEpictaskWithID(int epictaskID) {
        return epicHashMap.get(epictaskID);
    }

    // СОЗДАНИЕ НОВОЙ ЗАДАЧИ:

    public void createNewTask(Task task) {

        task.setTaskId(taskID);

        taskHashMap.put(taskID, task);
        System.out.println("Новая задача под номером " + taskID + " успешно добавлена!");
        taskID++;
    }

    public void createNewSubtask(Subtask subtask, Epic epic) {

        subtask.setTaskId(taskID);

        subtaskHashMap.put(taskID, subtask);
        epic.tasksInEpic.add(subtask);
        System.out.println("Успешно добавлена новая подзадача под номером " + taskID);
        taskID++;
    }

    public void createNewEpic(Epic epic, ArrayList<Subtask> tasksInEpic) {

        epic.setTaskId(taskID);
        epic.setTasksInEpic(tasksInEpic);

        epicHashMap.put(taskID, tasksInEpic);
        System.out.println("Успешно добавлен новый эпик под номером " + taskID);
        taskID++;
    }

    // ОБНОВЛЕНИЕ ЗАДАЧИ:

    public void updateTask(Task task, Task newTask) {
        if (taskHashMap.containsKey(task.getTaskId())) {
            taskHashMap.put(task.getTaskId(), newTask);
        } else {
            System.out.println("Не существует задачи по указанному номеру");
        }
    }

    public void updateSubtask(Subtask subtask, Subtask newSubtask, Epic epic) {

        if (subtaskHashMap.containsKey(subtask.getTaskId())) {
            subtaskHashMap.put(subtask.getTaskId(), newSubtask);
            epic.tasksInEpic.remove(subtask.getTaskId()); // удалил старую задачу из списка (поле класса epic)
            epic.tasksInEpic.add(newSubtask); // добавил новую задачу

            for (Subtask sub : epic.tasksInEpic) {
                while (sub.getStatus() == Status.DONE) {
                    epic.setStatus(Status.DONE);
                }
                if (sub.getStatus() != Status.DONE) {
                    while (sub.getStatus() == Status.NEW) {
                        epic.setStatus(Status.NEW);
                    }
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }

            }

        } else {
            System.out.println("Не существует задачи по указанному номеру");
        }
    }

    public void updateEpictask(ArrayList<Subtask> newTasksInEpic, Epic oldEpic) {
        if (epicHashMap.containsKey(oldEpic.getTaskId())) {
            epicHashMap.put(oldEpic.getTaskId(), newTasksInEpic);
        }
    }

    // УДАЛЕНИЕ ЗАДАЧИ ПО ИДЕНТИФИКАТОРУ:

    public void deleteTask(int taskID) {
        taskHashMap.remove(taskID);
    }

    public void deleteSubtask(int subtaskID, Epic epic) {

        subtaskHashMap.remove(subtaskID);

        epic.tasksInEpic.remove(subtaskID);

        for (Subtask sub : epic.tasksInEpic) {
            while (sub.getStatus() == Status.DONE) {
                epic.setStatus(Status.DONE);
            }
            if (sub.getStatus() != Status.DONE) {
                while (sub.getStatus() == Status.NEW) {
                    epic.setStatus(Status.NEW);
                }
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }

        }

    }

    public void deleteEpic(int epicID, Epic epic) {
        epicHashMap.remove(epicID);
        epic.tasksInEpic.clear();


    }

    // ПОЛУЧЕНИЕ СПИСКА ПОДЗАДАЧ ОПРЕДЕЛЕННОГО ЭПИКА

    public ArrayList<Subtask> getSubtasksFromEpic(int epicID) {
        if (epicHashMap.containsKey(epicID)) {

            return epicHashMap.get(epicID);
        } else {
            System.out.println("Не существует эпика по указанному ID.");
            return new ArrayList<>();
        }
    }

}