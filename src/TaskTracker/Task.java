package TaskTracker;

import java.util.HashMap;
import java.util.Objects;

public class Task {


    private String taskName;
    private String taskDescription;
    public int taskNumber;

    public static int getTaskID() {
        return taskID;
    }

    private static int taskID = 0; // метод в TaskManager для увеличения счётчика

    static HashMap<Integer, Task> taskHashMap = new HashMap<>();

    public Task(String taskName, String taskDescription, int taskNumber) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskNumber = taskNumber;
        taskID++;
     //   saveNewTask();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(taskName, otherTask.taskName) &&
                Objects.equals(taskDescription, otherTask.taskDescription);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (taskName != null) {
            hash = hash + taskName.hashCode();
        }

        hash = hash * 31;
        if (taskDescription != null) {
            hash = hash + taskDescription.hashCode();
        }

        return hash;

    }


//    public void saveNewTask() {
//        if (this.getClass() == Task.class) {
//            taskHashMap.put(taskID, taskName);
//        }
//    }

    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }


}
