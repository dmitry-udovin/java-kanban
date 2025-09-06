package TaskTracker;

import java.util.HashMap;

public class Epic extends TaskTracker.Task {

    static HashMap<Integer, String> epicHashMap = new HashMap<>();

    public Epic(String epicTaskName, String epicTaskDescription) {
        super(epicTaskName, epicTaskDescription);

        int epicID = Task.getTaskID();

        epicHashMap.put(epicID, epicTaskName);
    }

}
