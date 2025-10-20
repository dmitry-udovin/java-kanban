package tasktracker.managers;

import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {


    private HistoryManager historyManager = Managers.getDefaultHistory();


    private static int taskID = 1;
    private HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private HashMap<Integer, Epic> epicHashMap = new HashMap<>();


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    // ПОЛУЧЕНИЕ СПИСКОВ ЗАДАЧ:

    @Override
    public ArrayList<Task> getTaskList() {

        ArrayList<Task> taskList = new ArrayList<>();
        for (Task task : taskHashMap.values()) {
            taskList.add(task);
        }
        return taskList;

    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {

        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : subtaskHashMap.values()) {
            subtaskList.add(subtask);
        }

        return subtaskList;

    }

    @Override
    public ArrayList<Epic> getEpicList() {

        ArrayList<Epic> epicList = new ArrayList<>();
        for (Epic epic : epicHashMap.values()) {
            epicList.add(epic);
        }

        return epicList;

    }

    // УДАЛЕНИЕ ВСЕХ ЗАДАЧ:

    @Override
    public void removeAllTasks() {
        ArrayList<Integer> ids = new ArrayList<>(taskHashMap.keySet());
        for (Integer id : ids) {
            historyManager.remove(id);
        }
        taskHashMap.clear();
    }

    @Override
    public void removeAllSubtasks() {

        ArrayList<Integer> subtaskIds = new ArrayList<>(subtaskHashMap.keySet());
        for (Integer id : subtaskIds) {
            historyManager.remove(id);
        }

        subtaskHashMap.clear();

        for (Epic epic : epicHashMap.values()) {
            updateEpicStatus(epic);
            epic.getTasksInEpic().clear();
        }


    }

    @Override
    public void removeAllEpicTasks() {

        ArrayList<Integer> subtaskIds = new ArrayList<>(subtaskHashMap.keySet());
        for (Integer id : subtaskIds) {
            historyManager.remove(id);
        }

        ArrayList<Integer> epicIds = new ArrayList<>(epicHashMap.keySet());
        for (Integer id : epicIds) {
            historyManager.remove(id);
        }

        for (Epic epic : epicHashMap.values()) {
            ArrayList<Subtask> tasksInEpic = epic.getTasksInEpic();
            tasksInEpic.clear();
        }
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    // ПОЛУЧЕНИЕ ЗАДАЧИ ПО ИДЕНТИФИКАТОРУ:

    @Override
    public Task getTaskWithID(int taskID) {
        Task storedTask = taskHashMap.get(taskID);

        if (storedTask == null) return null;

        historyManager.add(storedTask);
        return new Task(storedTask.getTaskName(), storedTask.getTaskDescription(), storedTask.getStatus(),
                storedTask.getTaskId());
    }

    @Override
    public Subtask getSubtaskWithID(int subtaskID) {

        Subtask storedSubtask = subtaskHashMap.get(subtaskID);

        if (storedSubtask == null) return null;



        historyManager.add(storedSubtask);
        return new Subtask(storedSubtask.getTaskName(),storedSubtask.getTaskDescription(),
                storedSubtask.getStatus(),storedSubtask.getTaskId());
    }

    @Override
    public Epic getEpictaskWithID(int epictaskID) {

        Epic storedEpic = epicHashMap.get(epictaskID);
        if (storedEpic == null) return null;

        historyManager.add(storedEpic);
        return new Epic(storedEpic.getTaskName(),storedEpic.getTaskDescription(),storedEpic.getTaskId());
    }

    // СОЗДАНИЕ НОВОЙ ЗАДАЧИ:

    @Override
    public int createNewTask(Task task) {

        int id = task.getTaskId();

        Task copyOfTask = new Task(task.getTaskName(), task.getTaskDescription(),
                task.getStatus(), id);
        copyOfTask.setTaskId(taskID);
        taskHashMap.put(id, copyOfTask);
        System.out.println("Новая задача под номером " + taskID + " успешно добавлена!");
        return taskID++;
    }

    @Override
    public int createNewSubtask(Subtask subtask) {

        int epicId = subtask.getEpicId();

        Subtask copyOfSubtask = new Subtask(subtask.getTaskName(), subtask.getTaskDescription(),
                subtask.getStatus(), epicId);

        subtaskHashMap.put(taskID, copyOfSubtask);


        Epic epic = epicHashMap.get(epicId);
        if (epic == null) {
            System.out.println("Не существует эпика с номером " + epicId + " для добавления подзадачи.");
            return -1;
        }

        if (copyOfSubtask.getTaskId() == epicId && subtask.getTaskId() != -1) {
            throw new IllegalArgumentException("Эпик не может быть своей же подзадачей");
        }

        epic.getTasksInEpic().add(copyOfSubtask);
        System.out.println("Добавлена новая подзадача в эпик под номером " + epicId);

        updateEpicStatus(epic);
        return taskID++;
    }

    @Override
    public int createNewEpic(Epic epic) {

        int id = epic.getTaskId();
        Epic copyOfEpic = new Epic(epic.getTaskName(), epic.getTaskDescription(), id);


        epicHashMap.put(taskID, copyOfEpic);
        System.out.println("Успешно добавлен новый эпик под номером " + id);
        return taskID++;
    }

    // ОБНОВЛЕНИЕ ЗАДАЧИ:

    @Override
    public void updateTask(Task task) {
        taskHashMap.put(task.getTaskId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {

        subtaskHashMap.put(subtask.getTaskId(), subtask);
        Epic epic = epicHashMap.get(subtask.getTaskId());

        epic.getTasksInEpic().remove(subtask.getTaskId());
        epic.getTasksInEpic().add(subtask);

        updateEpicStatus(epic);

    }

    @Override
    public void updateEpic(Epic epic) {

        epicHashMap.put(epic.getTaskId(), epic);

    }

    // УДАЛЕНИЕ ЗАДАЧИ ПО ИДЕНТИФИКАТОРУ:

    @Override
    public void deleteTask(int taskID) {
        if (taskHashMap.containsKey(taskID)) {
            taskHashMap.remove(taskID);
            historyManager.remove(taskID);
        } else {
            System.out.println("Нет задачи по указанному ID.");
        }
    }

    @Override
    public void deleteSubtask(int subtaskID) {


        if (subtaskHashMap.containsKey(subtaskID)) {

            Epic epic = epicHashMap.get(subtaskID);

            subtaskHashMap.remove(subtaskID);
            epic.getTasksInEpic().remove(subtaskID);

            updateEpicStatus(epic);
            historyManager.remove(subtaskID);

        } else {
            System.out.println("Подзадача с указанным ID не соответствует ни одному из эпиков.");
        }

    }

    @Override
    public void deleteEpic(int epicID) {
        if (epicHashMap.containsKey(epicID)) {
            Epic epic = epicHashMap.get(epicID);
            epicHashMap.remove(epicID);
            subtaskHashMap.remove(epicID);
            epic.getTasksInEpic().clear();

            historyManager.remove(epicID);
        } else {
            System.out.println("Нет эпика по указанному ID.");
        }
    }

    // ПОЛУЧЕНИЕ СПИСКА ПОДЗАДАЧ ОПРЕДЕЛЕННОГО ЭПИКА

    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(int epicID) {
        if (epicHashMap.containsKey(epicID)) {
            Epic epic = epicHashMap.get(epicID);
            return epic.getTasksInEpic();
        } else {
            System.out.println("Не существует эпика по указанному ID.");
            return new ArrayList<>();
        }
    }

    // ОБНОВЛЕНИЕ СТАТУСА ЭПИКА

    @Override
    public void updateEpicStatus(Epic epic) {
        List<Subtask> tasksInEpic = epic.getTasksInEpic();

        if (tasksInEpic == null || tasksInEpic.isEmpty()) {
            epic.setStatus(Task.Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (Subtask task : tasksInEpic) {
            if (task.getStatus() != Task.Status.DONE) allDone = false;
            if (task.getStatus() != Task.Status.NEW) allNew = false;


            if (allDone) {
                epic.setStatus(Task.Status.DONE);
            } else if (allNew) {
                epic.setStatus(Task.Status.NEW);
            } else {
                epic.setStatus(Task.Status.IN_PROGRESS);
            }


        }

    }

    public static int getTaskID() {
        return taskID;
    }

}