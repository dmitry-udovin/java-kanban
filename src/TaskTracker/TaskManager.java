package TaskTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static TaskTracker.Epic.epicHashMap;
import static TaskTracker.Epic.idEpicHashMap;
import static TaskTracker.Subtask.subtaskHashMap;

public class TaskManager extends TaskTracker.Task {

    ArrayList<Set<Integer>> taskKeys = new ArrayList<>();
    ArrayList<Set<Integer>> subtaskKeys = new ArrayList<>();
    ArrayList<Set<Integer>> epicKeys = new ArrayList<>();

    public TaskManager(String taskName, String taskDescription, int taskNumber, Status status) {
        super(taskName, taskDescription, taskNumber, status);
    }


    // ПОЛУЧЕНИЕ СПИСКОВ ЗАДАЧ:

    public void getTaskList() {

        taskKeys.add(taskHashMap.keySet());
        System.out.println("Вывод списка задач заданного типа: ");
        for (Set<Integer> key : taskKeys) {
            taskHashMap.get(key);
        }

    }

    public void getSubtaskList() {

        subtaskKeys.add(subtaskHashMap.keySet());
        System.out.println("Вывод списка задач заданного типа: ");
        for (Set<Integer> key : subtaskKeys) {
            subtaskHashMap.get(key);
        }

    }

    public void getEpicList() {


        epicKeys.add(epicHashMap.keySet());
        System.out.println("Вывод списка задач заданного типа: ");
        for (Set<Integer> key : epicKeys) {
           epicHashMap.get(key);
        }

    }

    // УДАЛЕНИЕ ВСЕХ ЗАДАЧ:

    public void removeAllTasks() {
        taskKeys.add(taskHashMap.keySet());
        for (Set<Integer> key : taskKeys) {
            taskHashMap.remove(key);
        }
    }

    public void removeAllSubtasks() {
        subtaskKeys.add(subtaskHashMap.keySet());
        for (Set<Integer> key : subtaskKeys) {
            subtaskHashMap.remove(key);
        }
    }

    public void removeAllEpicTasks() {
        epicKeys.add(epicHashMap.keySet());
        for (Set<Integer> key : epicKeys) {
            epicHashMap.remove(key);
        }
    }

    // ПОЛУЧЕНИЕ ЗАДАЧИ ПО ИДЕНТИФИКАТОРУ:

    public Task getTaskWithID(int taskID) {
        return taskHashMap.get(taskID);
    }

    public Subtask getSubtaskWithID(int subtaskID) {
        return subtaskHashMap.get(subtaskID);
    }

    public ArrayList<Subtask> getEpictaskWithID(int epictaskID) {
        return epicHashMap.get(epictaskID);
    }

    // СОЗДАНИЕ НОВОЙ ЗАДАЧИ:

    public void createNewTask(Task task) {
        int taskID = getTaskID();
        taskHashMap.put(taskID, task);
        System.out.println("Новая задача под номером " + taskID + " успешно добавлена!");
    }

    public void createNewSubtask(Subtask subtask) {
        int taskID = getTaskID();
        subtaskHashMap.put(taskID, subtask);
        System.out.println("Успешно добавлена новая подзадача под номером " + taskID);
    }

    public void createNewEpic(ArrayList<Subtask> tasksInEpic) {
        int taskID = getTaskID();
        epicHashMap.put(taskID, tasksInEpic);
        System.out.println("Успешно добавлен новый эпик под номером " + taskID);
    }

    // ОБНОВЛЕНИЕ ЗАДАЧИ:

    public void updateTask(Task task) {
        if (taskHashMap.containsKey(task.taskNumber)) {
            taskHashMap.put(task.taskNumber, task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtaskHashMap.containsKey(subtask.taskNumber)) {
            subtaskHashMap.put(subtask.taskNumber, subtask);
        }
    }

    public void updateEpictask(ArrayList<Subtask> newTasksInEpic, Epic oldEpic) {
        if (epicHashMap.containsKey(oldEpic.taskNumber)) {
            epicHashMap.put(oldEpic.taskNumber, newTasksInEpic);
        }
    }

    // УДАЛЕНИЕ ЗАДАЧИ ПО ИДЕНТИФИКАТОРУ:

    public void deleteTask(int taskID) {
        taskHashMap.remove(taskID);
    }

    public void deleteSubtask(int subtaskID) {
        subtaskHashMap.remove(subtaskID);
    }

    public void deleteEpic(int epicID) {
        epicHashMap.remove(epicID);
    }

    // ПОЛУЧЕНИЕ СПИСКА ПОДЗАДАЧ ОПРЕДЕЛЕННОГО ЭПИКА

    public ArrayList<Subtask> getSubtasksFromEpic(int epicID) {
        if (epicHashMap.containsKey(epicID)) {

            Status required;
            ArrayList<Subtask> subtasks = epicHashMap.get(epicID);
            Epic epic = getEpicById(epicID);

            for (Subtask s : subtasks) {

                required = Status.NEW;

                while (s.getStatus() == required) {
                    epic.setStatus(Status.NEW);
                }
                if (s.getStatus() != required) {
                    epic.setStatus(Status.IN_PROGRESS);
                }

                required = Status.DONE;

                while (s.getStatus() == required && s.getStatus() != Status.NEW) {
                    epic.setStatus(Status.DONE);
                }
                if (s.getStatus() != required) {
                    epic.setStatus(Status.IN_PROGRESS);
                }

            }

            return epicHashMap.get(epicID);
        } else {
            System.out.println("Не существует эпика по указанному ID.");
            return new ArrayList<>();
        }
    }

// МЕТОД ДЛЯ ПОЛУЧЕНИЯ СООТВЕТСТВУЮЩЕГО ОБЬЕКТА Epic по его ID (используется для установки статуса)

    public Epic getEpicById(int epicID) {
        return idEpicHashMap.get(epicID);
    }

}