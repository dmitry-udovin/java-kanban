package TaskTracker;

import java.util.HashMap;

public class Epic extends TaskTracker.Task {

    static HashMap<Integer, Epic> epicHashMap = new HashMap<>();

    public Epic(String epicTaskName, String epicTaskDescription, int epictaskNumber) {
        super(epicTaskName, epicTaskDescription, epictaskNumber);

        int epicID = Task.getTaskID();

       // epicHashMap.put(epicID, epicTaskName);
    }

}
