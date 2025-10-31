package tasktracker.tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> tasksInEpic;


    public Epic(String epicTaskName, String epicTaskDescription, int epicId) {
        super(epicTaskName, epicTaskDescription, Status.NEW, epicId);

        tasksInEpic = new ArrayList<>();

    }

    public ArrayList<Subtask> getTasksInEpic() {
        return tasksInEpic;
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.EPIC;
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
