package tasktracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class Epic extends Task {

    private ArrayList<Subtask> tasksInEpic;

    private Optional<LocalDateTime> endTime;

    public Epic(String epicTaskName, String epicTaskDescription, int epicId) {
        super(epicTaskName, epicTaskDescription, Status.NEW, epicId, Optional.empty(), Duration.ZERO);

        tasksInEpic = new ArrayList<>();

    }

    @Override
    public void setStartTime(Optional<LocalDateTime> start) {
        super.setStartTime(start != null ? start : Optional.empty());
    }

    @Override
    public void setDuration(Duration duration) {
        super.setDuration(duration != null ? duration : Duration.ZERO);
    }

    public void setEndTime(Optional<LocalDateTime> end) {
        this.endTime = end != null ? end : Optional.empty();
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return endTime;
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
