package TaskTracker;

import java.util.HashMap;

public class Subtask extends TaskTracker.Task {

    static HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();

    public Subtask(String subTaskName, String subtaskDescription, int subtaskNumber) {
        super(subTaskName, subtaskDescription, subtaskNumber);
        int subtaskID = Task.getTaskID();

   //     subtaskHashMap.put(subtaskID, subTaskName);
    }

}