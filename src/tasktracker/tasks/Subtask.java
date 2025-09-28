package tasktracker.tasks;

public class Subtask extends Task {

    private final int epicId;


    public Subtask(String subTaskName, String subtaskDescription, Status status, int epicId) {
        super(subTaskName, subtaskDescription, status);
        this.epicId = epicId;

    }

    public int getEpicId() {
        return epicId;
    }

}