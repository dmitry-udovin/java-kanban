package TaskTracker;

import java.util.HashMap;

public class Subtask extends TaskTracker.Task {

    static HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();

    public Subtask(String subTaskName, String subtaskDescription, int subtaskNumber, Status status) {
        super(subTaskName, subtaskDescription, subtaskNumber, status);
        int subtaskID = Task.getTaskID();

    }

}