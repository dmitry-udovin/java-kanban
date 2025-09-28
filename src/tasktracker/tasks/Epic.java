package tasktracker.tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> tasksInEpic;


    public Epic(String epicTaskName, String epicTaskDescription, int epicID) {
        super(epicTaskName, epicTaskDescription, Status.NEW, epicID);

        tasksInEpic = new ArrayList<>();

    }

    public ArrayList<Subtask> getTasksInEpic() {
        return tasksInEpic;
    }

}
