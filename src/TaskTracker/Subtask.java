package TaskTracker;

import java.util.HashMap;

public class Subtask extends TaskTracker.Task {

    static HashMap<Integer, String> subtaskHashMap = new HashMap<>();

    public Subtask(String subTaskName, String subtaskDescription) {
        super(subTaskName, subtaskDescription);
        int subtaskID = Task.getTaskID();

        subtaskHashMap.put(subtaskID, subTaskName);
    }

}