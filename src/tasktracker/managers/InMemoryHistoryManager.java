package tasktracker.managers;

import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> taskHistory;

    public InMemoryHistoryManager() {
        this.taskHistory = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (taskHistory.size() == 10) {
            taskHistory.remove(0);
        }
        Task copy;
        if (task instanceof Task) {
            copy = new Task(task.getTaskName(), task.getTaskDescription(), task.getStatus(), task.getTaskId());
            taskHistory.add(copy);
        } else if (task instanceof Subtask) {
            copy = new Subtask(task.getTaskName(), task.getTaskDescription(), task.getStatus(), task.getTaskId());
            taskHistory.add(copy);
        } else if (task instanceof Epic) {
            copy = new Epic(task.getTaskName(), task.getTaskDescription(), task.getTaskId());
            taskHistory.add(copy);
        }
    }


    public ArrayList<Task> getHistory() {
        return taskHistory;
    }

}
