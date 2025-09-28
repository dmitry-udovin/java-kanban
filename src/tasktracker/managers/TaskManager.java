package tasktracker.managers;

import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getTaskList();

    List<Subtask> getSubtaskList();

    List<Epic> getEpicList();

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

    List<Subtask> getSubtasksFromEpic(int epicID);

    void updateEpicStatus(Epic epic);

    List<Task> getHistory();

}
