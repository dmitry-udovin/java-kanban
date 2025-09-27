package TaskTracker;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getTaskList();

    ArrayList<Subtask> getSubtaskList();

    ArrayList<Epic> getEpicList();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpicTasks();

    Task getTaskWithID(int taskID);

    Subtask getSubtaskWithID(int subtaskID);

    Epic getEpictaskWithID(int epictaskID);

    int createNewTask(Task task); // поменял void на int

    int createNewSubtask(Subtask subtask); // поменял void на int

    int createNewEpic(Epic epic); // поменял void на int

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int taskID);

    void deleteSubtask(int subtaskID);

    void deleteEpic(int epicID);

    ArrayList<Subtask> getSubtasksFromEpic(int epicID);

    void updateEpicStatus(Epic epic);

    InMemoryHistoryManager getManager();


}
