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
    taskHistoryList.add(task);
}


    public ArrayList<Task> getHistory() {
    return taskHistoryList;
}

}
