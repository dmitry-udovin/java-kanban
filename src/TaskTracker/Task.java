package TaskTracker;

public class Task {
    private String taskName;
    private String taskDescription;
    private static int taskID; // метод в TaskManager для увеличения счётчика

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public static void setTaskID(int newTaskID) {
        Task.taskID = newTaskID;
    }



    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }



}
