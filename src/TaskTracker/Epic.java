package TaskTracker;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends TaskTracker.Task {

    static HashMap<Integer, ArrayList<Subtask>> epicHashMap = new HashMap<>();
    static HashMap<Integer, Epic> idEpicHashMap = new HashMap<>();

    public Epic(String epicTaskName, String epicTaskDescription, int epictaskNumber, Status status) {
        super(epicTaskName, epicTaskDescription, epictaskNumber, status);

        int epicID = Task.getTaskID();
    }


}
