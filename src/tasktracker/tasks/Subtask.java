package tasktracker.tasks;

public class Subtask extends Task {

    private int epicId;


    public Subtask(String subTaskName, String subtaskDescription, Status status, int epicId) {
        super(subTaskName, subtaskDescription, status);
        this.epicId = epicId;

    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}