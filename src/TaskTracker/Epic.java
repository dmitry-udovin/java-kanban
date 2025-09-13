package TaskTracker;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> tasksInEpic;

    public ArrayList<Subtask> getTasksInEpic() {
        return tasksInEpic;
    }

    public Epic(String epicTaskName, String epicTaskDescription, Status status, int epicID) {
        super(epicTaskName, epicTaskDescription, status, epicID);

        tasksInEpic = new ArrayList<>();

    }

}
