package TaskTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static TaskTracker.Epic.epicHashMap;
import static TaskTracker.Subtask.subtaskHashMap;

public class TaskManager extends TaskTracker.Task {

    ArrayList<Set<Integer>> taskKeys = new ArrayList<>();
    ArrayList<Set<Integer>> subtaskKeys = new ArrayList<>();
    ArrayList<Set<Integer>> epicKeys = new ArrayList<>();

    public TaskManager(String taskName, String taskDescription, int taskNumber) {
        super(taskName, taskDescription, taskNumber);
    }


    // ПОЛУЧЕНИЕ СПИСКОВ ЗАДАЧ:

    public void getTaskList() {

        taskKeys.add(taskHashMap.keySet());
        System.out.println("Вывод списка задач заданного типа: ");
        for (Set<Integer> key : taskKeys) {
            taskHashMap.get(key);
        }

    }

    public void getSubtaskList() {

        subtaskKeys.add(subtaskHashMap.keySet());
        System.out.println("Вывод списка задач заданного типа: ");
        for (Set<Integer> key : subtaskKeys) {
            taskHashMap.get(key);
        }

    }

    public void getEpicList() {


        epicKeys.add(epicHashMap.keySet());
        System.out.println("Вывод списка задач заданного типа: ");
        for (Set<Integer> key : epicKeys) {
            taskHashMap.get(key);
        }

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

    public Epic getEpictaskWithID(int epictaskID) {
        return epicHashMap.get(epictaskID);
    }

    // СОЗДАНИЕ НОВОЙ ЗАДАЧИ:

public void createNewTask(Task task) {
        int taskID = getTaskID();
        taskHashMap.put(taskID,task);
    System.out.println("Новая задача под номером " + taskID + " успешно добавлена!");
}

public void createNewSubtask(Subtask subtask) {
        int taskID = getTaskID();
        subtaskHashMap.put(taskID,subtask);
    System.out.println("Успешно добавлена новая подзадача под номером " + taskID);
}
public void createNewEpic(Epic epic) {
        int taskID = getTaskID();
        epicHashMap.put(taskID,epic);
    System.out.println("Успешно добавлен новый эпик под номером " + taskID);
}

    // ОБНОВЛЕНИЕ ЗАДАЧИ:

    public void updateTask(Task task) {
        if (taskHashMap.containsKey(task.taskNumber)) {
            taskHashMap.put(task.taskNumber,task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtaskHashMap.containsKey(subtask.taskNumber)) {
            subtaskHashMap.put(subtask.taskNumber,subtask);
        }
    }

    public void updateEpictask(Epic epic) {
        if (epicHashMap.containsKey(epic.taskNumber)) {
            epicHashMap.put(epic.taskNumber,epic);
        }
    }

    // УДАЛЕНИЕ ЗАДАЧИ ПО ИДЕНТИФИКАТОРУ:

    public void deleteTask(int taskID) {
        taskHashMap.remove(taskID);
    }

    public void deleteSubtask(int subtaskID) {
        subtaskHashMap.remove(subtaskID);
    }

    public void deleteEpic(int epicID) {
        epicHashMap.remove(epicID);
    }

    // ПОЛУЧЕНИЕ СПИСКА ПОДЗАДАЧ ОПРЕДЕЛЕННОГО ЭПИКА

    public void getSubtasksFromEpic() {

    }

}