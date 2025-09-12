package TaskTracker;

public class Subtask extends TaskTracker.Task {

    private int epicId;

    public Subtask(String subTaskName, String subtaskDescription, Status status, int epicId) {
        super(subTaskName, subtaskDescription, status, epicId);
        this.epicId = epicId;

    }

}