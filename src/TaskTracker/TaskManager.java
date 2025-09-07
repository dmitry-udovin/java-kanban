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

    public TaskManager(String taskName, String taskDescription) {
        super(taskName, taskDescription);
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

    public String getTaskWithID(int taskID) {
        return taskHashMap.get(taskID);
    }

    public String getSubtaskWithID(int subtaskID) {
        return taskHashMap.get(subtaskID);
    }

    public String getEpictaskWithID(int epictaskID) {
        return taskHashMap.get(epictaskID);
    }

    // СОЗДАНИЕ НОВОЙ ЗАДАЧИ:



}