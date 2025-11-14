package tasktracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class Task {

    private int taskId = -1;
    private String taskName;
    private String taskDescription;
    private Status status;
    private Duration duration;
    private Optional<LocalDateTime> startTime;


    public Task(String taskName, String taskDescription, Status status, int taskId, Optional<LocalDateTime> startTime, Duration duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.taskId = taskId;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String taskName, String taskDescription, Status status, Optional<LocalDateTime> startTime, Duration duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public void setTaskName(String name) {
        this.taskName = name;
    }

    public void setTaskDescription(String description) {
        this.taskDescription = description;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public String getTaskDescription() {
        return this.taskDescription;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public Optional<LocalDateTime> getEndTime() {
        return startTime.map(startTime -> startTime.plus(duration));
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public TaskTypes getType() {
        return TaskTypes.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public Optional<LocalDateTime> getStartTime() {
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration != null ? duration : Duration.ZERO;
    }

    public void setStartTime(Optional<LocalDateTime> startTime) {
        this.startTime = startTime != null ? startTime : Optional.empty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return (taskId == otherTask.taskId);

    }

    @Override
    public int hashCode() {
        Integer hash = 17;
        if (taskId >= 0) {
            hash = hash + taskId;
        } else {
            return -1;
        }

        return hash;
    }

    @Override
    public String toString() {
        return "taskID " + taskId + ", taskName=" + "{" + taskName + "}, taskDescription=" + "{" + taskDescription +
                "}" + ", status={" + status + "}.  ";
    }


    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }

    public enum TaskTypes {
        TASK,
        EPIC,
        SUBTASK
    }

}
