package TaskTracker;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager extends TaskTracker.Task {

    public TaskManager() {

    }

    private static int taskID = 0;
    HashMap<Integer, Task> taskHashMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    HashMap<Integer, Epic> epicHashMap = new HashMap<>();

    // ПОЛУЧЕНИЕ СПИСКОВ ЗАДАЧ:

    public ArrayList<Task> getTaskList() {

        ArrayList<Task> taskList = new ArrayList<>();
        for (Task task : taskHashMap.values()) {
            taskList.add(task);
        }
        return taskList;

    }

    public ArrayList<Subtask> getSubtaskList() {

        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : subtaskHashMap.values()) {
            subtaskList.add(subtask);
        }

        return subtaskList;

    }

    public ArrayList<Epic> getEpicList() {

        ArrayList<Epic> epicList = new ArrayList<>();
        for (Epic epic : epicHashMap.values()) {
            epicList.add(epic);
        }

        return epicList;

    }

    // УДАЛЕНИЕ ВСЕХ ЗАДАЧ:

    public void removeAllTasks() {
        taskHashMap.clear();
    }

    public void removeAllSubtasks() {
        subtaskHashMap.clear();
    }

    public void removeAllEpicTasks() {
        epicHashMap.clear();
    }

    // ПОЛУЧЕНИЕ ЗАДАЧИ ПО ИДЕНТИФИКАТОРУ:

    public Task getTaskWithID(int taskID) {
        return taskHashMap.get(taskID);
    }

    public Subtask getSubtaskWithID(int subtaskID) {
        return subtaskHashMap.get(subtaskID);
    }

    public Epic getEpictaskWithID(int epictaskID) {
        return epicHashMap.get(epictaskID);
    }

    // СОЗДАНИЕ НОВОЙ ЗАДАЧИ:

    public void createNewTask(Task task) {

        taskHashMap.put(taskID, task);
        System.out.println("Новая задача под номером " + taskID + " успешно добавлена!");
        taskID++;
    }

    public void createNewSubtask(Subtask subtask) {


        if (epicHashMap.containsKey(subtask.getTaskId())) {
            subtaskHashMap.put(taskID, subtask);
            Epic epic = epicHashMap.get(subtask.getTaskId());
            epic.tasksInEpic.add(subtask);

            System.out.println("Добавлена новая подзадача в эпик под номером " + epic.getTaskId());

            updateEpicStatus(epic);
        } else {
            int epicNumber = subtask.getTaskId();
            System.out.println("Не существует эпика с номером " + epicNumber + " для добавления подзадачи.");
        }


    }

    public void createNewEpic(Epic epic) {

        epicHashMap.put(taskID, epic);
        epic.setTaskId(taskID);
        System.out.println("Успешно добавлен новый эпик под номером " + taskID);
        taskID++;
    }

    // ОБНОВЛЕНИЕ ЗАДАЧИ:

    public void updateTask(Task task) {
        taskHashMap.put(task.getTaskId(), task);
    }

    public void updateSubtask(Subtask subtask) {

        subtaskHashMap.put(subtask.getTaskId(), subtask);
        Epic epic = epicHashMap.get(subtask.getTaskId());
        epic.tasksInEpic.remove(subtask.getTaskId());
        epic.tasksInEpic.add(subtask);
    }

    public void updateEpic(Epic epic) {

        epicHashMap.put(epic.getTaskId(), epic);

    }

    // УДАЛЕНИЕ ЗАДАЧИ ПО ИДЕНТИФИКАТОРУ:

    public void deleteTask(int taskID) {
        if (taskHashMap.containsKey(taskID)) {
            taskHashMap.remove(taskID);
        } else {
            System.out.println("Нет задачи по указанному ID.");
        }
    }

    public void deleteSubtask(int subtaskID) {


        if (subtaskHashMap.containsKey(subtaskID)) {

            Epic epic = epicHashMap.get(subtaskID);

            subtaskHashMap.remove(subtaskID);
            epic.tasksInEpic.remove(subtaskID);

            updateEpicStatus(epic);

        } else {
            System.out.println("Подзадача с указанным ID не соответствует ни одному из эпиков.");
        }

    }

    public void deleteEpic(int epicID) {
        if (epicHashMap.containsKey(epicID)) {
            Epic epic = epicHashMap.get(epicID);
            epicHashMap.remove(epicID);
            epic.tasksInEpic.clear();

        } else {
            System.out.println("Нет эпика по указанному ID.");
        }
    }

    // ПОЛУЧЕНИЕ СПИСКА ПОДЗАДАЧ ОПРЕДЕЛЕННОГО ЭПИКА

    public void getSubtasksFromEpic(int epicID) {
        if (epicHashMap.containsKey(epicID)) {
            Epic epic = epicHashMap.get(epicID);
            System.out.println(epic.tasksInEpic);
        } else {
            System.out.println("Не существует эпика по указанному ID.");
        }
    }

    // ОБНОВЛЕНИЕ СТАТУСА ЭПИКА

    public void updateEpicStatus(Epic epic) {
        for (Subtask taskInEpic : epic.tasksInEpic) {

            if (taskInEpic.getStatus() == Status.DONE && epic.getStatus() == Status.DONE) {
                epic.setStatus(Status.DONE);
                continue;
            }

            if (taskInEpic.getStatus() == Status.NEW && epic.getStatus() == Status.NEW) {
                epic.setStatus(Status.NEW);
                continue;
            }

            epic.setStatus(Status.IN_PROGRESS);
            break;

        }
    }


}