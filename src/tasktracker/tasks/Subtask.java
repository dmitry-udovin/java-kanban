package tasktracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class Subtask extends Task {

    private int epicId;


    public Subtask(String subTaskName, String subtaskDescription, Status status, int epicId, Optional<LocalDateTime> startTime, Duration duration) {
        super(subTaskName, subtaskDescription, status, startTime, duration);
        this.epicId = epicId;

    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}