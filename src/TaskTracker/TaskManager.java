package TaskTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static TaskTracker.Epic.epicHashMap;
import static TaskTracker.Subtask.subtaskHashMap;

public class TaskManager extends TaskTracker.Task {


    public TaskManager(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }


    // ПОЛУЧЕНИЕ СПИСКОВ ЗАДАЧ:

    public void getTaskList() {

        ArrayList<Set<Integer>> taskKeys = new ArrayList<>();
        taskKeys.add(taskHashMap.keySet());
        System.out.println("Вывод списка задач заданного типа: ");
        for (Set<Integer> key : taskKeys) {
            taskHashMap.get(key);
        }

    }

    public void getSubtaskList() {

        ArrayList<Set<Integer>> subtaskKeys = new ArrayList<>();
        subtaskKeys.add(subtaskHashMap.keySet());
        System.out.println("Вывод списка задач заданного типа: ");
        for (Set<Integer> key : subtaskKeys) {
            taskHashMap.get(key);
        }

    }

    public void getEpicList() {


        ArrayList<Set<Integer>> epicKeys = new ArrayList<>();
        epicKeys.add(epicHashMap.keySet());
        System.out.println("Вывод списка задач заданного типа: ");
        for (Set<Integer> key : epicKeys) {
            taskHashMap.get(key);
        }

    }

    // УДАЛЕНИЕ ВСЕХ ЗАДАЧ


}