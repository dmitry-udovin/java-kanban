package TaskTracker;

import java.util.ArrayList;

public class Epic extends TaskTracker.Task {

    public ArrayList<Subtask> tasksInEpic;

    public Epic(String epicTaskName, String epicTaskDescription, Status status, int epicID) {
        super(epicTaskName, epicTaskDescription, status, epicID);

        tasksInEpic = new ArrayList<>();

    }

    public void setTasksInEpic(ArrayList<Subtask> tasksInEpic) {
        this.tasksInEpic = tasksInEpic;
    }

}
