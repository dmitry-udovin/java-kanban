package TaskTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {


    private InMemoryHistoryManager manager = new InMemoryHistoryManager();
    private static int taskID = 0;
    private HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private ArrayList<Task> taskHistoryList;


    public InMemoryTaskManager() {
        this.taskHistoryList = new ArrayList<>();
    }


    @Override
    public InMemoryHistoryManager getManager() {
        return manager;
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
        taskHashMap.clear();
    }

    @Override
    public void removeAllSubtasks() {

        subtaskHashMap.clear();

        for (Epic epic : epicHashMap.values()) {
            updateEpicStatus(epic);
            epic.getTasksInEpic().clear();
        }


    }

    @Override
    public void removeAllEpicTasks() {
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
        manager.add(taskHashMap.get(taskID));
        return taskHashMap.get(taskID);
    }

    @Override
    public Subtask getSubtaskWithID(int subtaskID) {
        manager.add(subtaskHashMap.get(subtaskID));
        return subtaskHashMap.get(subtaskID);
    }

    @Override
    public Epic getEpictaskWithID(int epictaskID) {
        manager.add(epicHashMap.get(epictaskID));
        return epicHashMap.get(epictaskID);
    }

    // СОЗДАНИЕ НОВОЙ ЗАДАЧИ:

    @Override
    public void createNewTask(Task task) {

        taskHashMap.put(taskID, task);
        System.out.println("Новая задача под номером " + taskID + " успешно добавлена!");
        taskID++;
    }

    @Override
    public void createNewSubtask(Subtask subtask) {


        if (epicHashMap.containsKey(subtask.getTaskId())) {
            subtaskHashMap.put(taskID, subtask);
            Epic epic = epicHashMap.get(subtask.getTaskId());
            epic.getTasksInEpic().add(subtask);

            System.out.println("Добавлена новая подзадача в эпик под номером " + epic.getTaskId());

            updateEpicStatus(epic);
        } else {
            int epicNumber = subtask.getTaskId();
            System.out.println("Не существует эпика с номером " + epicNumber + " для добавления подзадачи.");
        }


    }

    @Override
    public void createNewEpic(Epic epic) {

        epicHashMap.put(taskID, epic);
        epic.setTaskId(taskID);
        System.out.println("Успешно добавлен новый эпик под номером " + taskID);
        taskID++;
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


//    @Override
//    public ArrayList<Task> getHistory() {
//
//        return taskHistoryList;
//
//    }

}