package tasktracker.managers;

import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer,Node<Task>> taskHistoryMap;

    private LinkedListForTasks<Integer,Node<Task>> listOfTasks;

    public InMemoryHistoryManager() {
        this.taskHistoryMap = new HashMap<>();
        this.listOfTasks = new LinkedListForTasks<>();
    }

    @Override
    public void add(Task task) {
    int id = task.getTaskId();

    Node<Task> oldNode = taskHistoryMap.get(id);
    if (oldNode != null) {
        listOfTasks.removeNode(oldNode);
    }

    listOfTasks.linkLast(task);

    Node<Task> newNode = listOfTasks.get(id);
    taskHistoryMap.put(id,newNode);
    }

    public ArrayList<Task> getHistory() {
        return listOfTasks.getTasks();
    }

    public void remove(int taskId) {
    Node<Task> node = taskHistoryMap.get(taskId);
    if (node != null) {
        listOfTasks.removeNode(node);
        taskHistoryMap.remove(taskId);
    }
    }

}
