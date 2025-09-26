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

    void createNewTask(Task task);

    void createNewSubtask(Subtask subtask);

    void createNewEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int taskID);

    void deleteSubtask(int subtaskID);

    void deleteEpic(int epicID);

    ArrayList<Subtask> getSubtasksFromEpic(int epicID);

    void updateEpicStatus(Epic epic);

    InMemoryHistoryManager getManager();

    //ArrayList<Task> getHistory();

}
