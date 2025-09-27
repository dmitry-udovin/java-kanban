package TaskTracker;

public class Task {

    private int taskId = -1;
    private String taskName;
    private String taskDescription;
    private Status status;


    public Task(String taskName, String taskDescription, Status status, int taskId) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.taskId = taskId;
    }

    public Task(String taskName, String taskDescription, Status status) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
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


    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
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


}
