package tasktracker.managers;

import tasktracker.tasks.Task;
import tasktracker.utilities.LinkedListForTasks;
import tasktracker.utilities.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node<Task>> taskHistoryMap;

    private LinkedListForTasks listOfTasks;

    public InMemoryHistoryManager() {
        this.taskHistoryMap = new HashMap<>();
        this.listOfTasks = new LinkedListForTasks();
    }

    @Override
    public void add(Task task) {

        if (task == null) return;

        int id = task.getTaskId();

        Node<Task> oldNode = taskHistoryMap.remove(id);
        if (oldNode != null) {
            listOfTasks.removeNode(oldNode);
        }

        Node<Task> newNode = listOfTasks.linkLast(task);
        taskHistoryMap.put(id, newNode);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return listOfTasks.getTasks();
    }

    @Override
    public void remove(int taskId) {
        Node<Task> node = taskHistoryMap.remove(taskId);
        if (node != null) listOfTasks.removeNode(node);
    }


}
