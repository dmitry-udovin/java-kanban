package TaskTracker;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private static ArrayList<Task> taskHistoryList;

    InMemoryHistoryManager() {
        this.taskHistoryList = new ArrayList<>();
    }

@Override
    public void add(Task task) {
    if(taskHistoryList.size() == 10) {
        taskHistoryList.remove(0);
    }
    Task copy = new Task(task.getTaskName(),task.getTaskDescription(),task.getStatus(),task.getTaskId());
    taskHistoryList.add(copy);
}


    public ArrayList<Task> getHistory() {
    return taskHistoryList;
}

}
