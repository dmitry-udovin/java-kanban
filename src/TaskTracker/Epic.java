package TaskTracker;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends TaskTracker.Task {

    static HashMap<Integer, Epic> idEpicHashMap = new HashMap<>();

    public void setTasksInEpic(ArrayList<Subtask> tasksInEpic) {
        this.tasksInEpic = tasksInEpic;
    }


    public ArrayList<Subtask> tasksInEpic = new ArrayList<>();

    public Epic(String epicTaskName, String epicTaskDescription, Status status) {
        super(epicTaskName, epicTaskDescription, status);

    }


}
