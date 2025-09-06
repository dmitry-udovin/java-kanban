package TaskTracker;

public class TaskManager extends TaskTracker.Task {

    public TaskManager(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    private static int newTaskID = 0;

    static void getTaskID() {
        newTaskID++;
        TaskTracker.Task.setTaskID(newTaskID);

    }



}