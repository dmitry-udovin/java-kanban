package tasktracker.tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Subtask> tasksInEpic;


    public Epic(String epicTaskName, String epicTaskDescription, int epicID) {
        super(epicTaskName, epicTaskDescription, Status.NEW, epicID);

        tasksInEpic = new ArrayList<>();

    }

    public ArrayList<Subtask> getTasksInEpic() {
        return tasksInEpic;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        if (this.getClass() != object.getClass()) return false;
        Epic otherEpic = (Epic) object;
        return this.getTaskId() == otherEpic.getTaskId();
    }

}
