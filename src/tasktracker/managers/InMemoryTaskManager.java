package tasktracker.managers;

import tasktracker.exceptions.TaskTimeException;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {


    private HistoryManager historyManager = Managers.getDefaultHistory();


    protected static int taskID = 1;
    protected HashMap<Integer, Task> taskHashMap = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicHashMap = new HashMap<>();

    protected final Comparator<Task> priorityComparator = (task1, task2) -> {
        var task1Time = task1.getStartTime().get();
        var task2Time = task2.getStartTime().get();
        if (task1Time == null && task2Time == null) return Integer.compare(task1.getTaskId(), task2.getTaskId());
        if (task1Time == null) return 1;
        if (task2Time == null) return -1;
        int cmp = task1Time.compareTo(task2Time);
        return (cmp != 0) ? cmp : Integer.compare(task1.getTaskId(), task2.getTaskId());
    };

    private final TreeSet<Task> prioritized = new TreeSet<>(priorityComparator);

    private void priorityAdd(Task task) {
        if (task == null) return;
        if ((task.getType() == Task.TaskTypes.TASK || task.getType() == Task.TaskTypes.SUBTASK)
                && task.getStartTime().isPresent()) {
            prioritized.add(task);
        }
    }

    protected static Optional<LocalDateTime> endOf(Task task) {
        return task.getEndTime();
    }

    protected static boolean hasSchedule(Task task) {
        return task.getStartTime() != null && task.getStartTime().isPresent();
    }

    // пересечение двух задач по времени
    protected static boolean overlaps(Task a, Task b) {
        if (a == null || b == null) return false;
        if (!hasSchedule(a) || !hasSchedule(b)) return false;

        LocalDateTime aStart = a.getStartTime().get();
        LocalDateTime bStart = b.getStartTime().get();

        LocalDateTime aEnd = endOf(a).orElse(aStart);
        LocalDateTime bEnd = endOf(b).orElse(bStart);

        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    // проверка кандидата на пересечение с любой другой задачей

    public boolean overlapsAny(Task candidate) {
        if (candidate == null || !hasSchedule(candidate)) return false;

        final int id = candidate.getTaskId();

        return prioritized.stream()
                .filter(t -> t.getTaskId() != id)
                .anyMatch(t -> overlaps(t, candidate));
    }

    // глобальная проверка пересечений по всему приоритезированному списку
    public boolean hasAnyOverlaps() {
        var list = getPrioritizedTasks();
        for (int i = 0; i + 1 < list.size(); i++) {
            if (overlaps(list.get(i), list.get(i + 1))) return true;
        }
        return false;
    }

    public void throwExceptionIfTasksOverlap(Task candidate) {
        if (overlapsAny(candidate)) {
            throw new TaskTimeException("Задача пересекается по времени с уже запланированной: id=" + candidate.getTaskId());
        }
    }

    private void priorityRemovedById(int id) {
        prioritized.removeIf(task -> task.getTaskId() == id);
    }

    protected void updateEpicTimeFields(Epic epic) {
        if (epic == null) return;

        List<Subtask> subtasks = epic.getTasksInEpic();
        if (subtasks == null || subtasks.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(Optional.empty());
            epic.setEndTime(Optional.empty());
            return;
        }

        Duration total = subtasks.stream()
                .filter(subtask -> subtask != null && subtask.getDuration() != null)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        Optional<LocalDateTime> minStart = subtasks.stream()
                .filter(s -> s != null)
                .map(Subtask::getStartTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> maxEnd = subtasks.stream()
                .filter(s -> s != null)
                .map(Subtask::getEndTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(LocalDateTime::compareTo);

        epic.setDuration(total);
        epic.setStartTime(minStart);
        epic.setEndTime(maxEnd);

    }

    protected int nextId() {
        return taskID++;
    }

    protected void setNextId(int next) {
        taskID = next;
    }

    // вставка заранее созданных объектов (при загрузке из файла)
    protected void putTaskInternal(Task task) {
        taskHashMap.put(task.getTaskId(), task);
    }

    protected void putEpicInternal(Epic epic) {
        epicHashMap.put(epic.getTaskId(), epic);
    }

    protected void putSubtaskInternal(Subtask subtask) {
        subtaskHashMap.put(subtask.getTaskId(), subtask);
        Epic epicFromHashMap = epicHashMap.get(subtask.getEpicId());
        if (epicFromHashMap != null) {
            epicFromHashMap.getTasksInEpic().add(subtask);
            updateEpicStatus(epicFromHashMap);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    // ПОЛУЧЕНИЕ СПИСКОВ ЗАДАЧ:

    @Override
    public ArrayList<Task> getTaskList() {
        return taskHashMap.values().stream()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        return subtaskHashMap.values().stream()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return epicHashMap.values().stream()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // УДАЛЕНИЕ ВСЕХ ЗАДАЧ:

    @Override
    public void removeAllTasks() {
        taskHashMap.keySet().forEach(id -> {
            priorityRemovedById(id);
            historyManager.remove(id);
        });
        taskHashMap.clear();
    }

    @Override
    public void removeAllSubtasks() {


        subtaskHashMap.keySet().forEach(id -> {
            priorityRemovedById(id);
            historyManager.remove(id);
        });
        subtaskHashMap.clear();

        epicHashMap.values().forEach(epic -> {
            epic.getTasksInEpic().clear();
            updateEpicStatus(epic);
            updateEpicTimeFields(epic);
        });

    }

    @Override
    public void removeAllEpicTasks() {


        subtaskHashMap.keySet().forEach(id -> {
            historyManager.remove(id);
            priorityRemovedById(id);
        });

        epicHashMap.keySet().forEach(id -> {
            historyManager.remove(id);
        });

        epicHashMap.values().forEach(epic -> {
            epic.getTasksInEpic().clear();
        });

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
                storedTask.getTaskId(), storedTask.getStartTime(), storedTask.getDuration());
    }

    @Override
    public Subtask getSubtaskWithID(int subtaskID) {

        Subtask storedSubtask = subtaskHashMap.get(subtaskID);

        if (storedSubtask == null) return null;


        historyManager.add(storedSubtask);

        Subtask copy = new Subtask(storedSubtask.getTaskName(), storedSubtask.getTaskDescription(),
                storedSubtask.getStatus(), storedSubtask.getEpicId(), storedSubtask.getStartTime(), storedSubtask.getDuration());

        copy.setTaskId(storedSubtask.getTaskId());

        return copy;
    }

    @Override
    public Epic getEpictaskWithID(int epictaskID) {

        Epic storedEpic = epicHashMap.get(epictaskID);
        if (storedEpic == null) return null;

        historyManager.add(storedEpic);

        Epic copy = new Epic(storedEpic.getTaskName(), storedEpic.getTaskDescription(), storedEpic.getTaskId());

        copy.setStatus(storedEpic.getStatus());

        copy.setStartTime(storedEpic.getStartTime());
        copy.setDuration(storedEpic.getDuration());
        copy.setEndTime(storedEpic.getEndTime());

        for (Subtask s : storedEpic.getTasksInEpic()) {
            copy.getTasksInEpic().add(s);
        }

        return copy;
    }

    // СОЗДАНИЕ НОВОЙ ЗАДАЧИ:

    @Override
    public int createNewTask(Task task) {

        int newTaskId = taskID++;
        task.setTaskId(newTaskId);
        Task copyOfTask = new Task(task.getTaskName(), task.getTaskDescription(),
                task.getStatus(), newTaskId, task.getStartTime(), task.getDuration());
        copyOfTask.setTaskId(newTaskId);

        throwExceptionIfTasksOverlap(copyOfTask);

        taskHashMap.put(newTaskId, copyOfTask);

        priorityAdd(copyOfTask);

        System.out.println("Новая задача под номером " + newTaskId + " успешно добавлена!");
        return newTaskId;
    }

    @Override
    public int createNewSubtask(Subtask subtask) {

        int epicId = subtask.getEpicId();
        int newId = taskID++;

        Subtask copyOfSubtask = new Subtask(subtask.getTaskName(), subtask.getTaskDescription(),
                subtask.getStatus(), epicId, subtask.getStartTime(), subtask.getDuration());

        copyOfSubtask.setTaskId(newId);

        throwExceptionIfTasksOverlap(copyOfSubtask);

        subtaskHashMap.put(newId, copyOfSubtask);

        Epic epic = epicHashMap.get(epicId);
        if (epic == null) {
            System.out.println("Не существует эпика с номером " + epicId + " для добавления подзадачи.");
            return -1;
        }

        if (subtask.getTaskId() == epicId && subtask.getTaskId() != -1) {
            throw new IllegalArgumentException("Эпик не может быть своей же подзадачей");
        }

        epic.getTasksInEpic().add(copyOfSubtask);
        System.out.println("Добавлена новая подзадача в эпик под номером " + epicId);

        subtask.setTaskId(newId);

        priorityAdd(copyOfSubtask);
        updateEpicTimeFields(epic);

        updateEpicStatus(epic);
        return newId;
    }

    @Override
    public int createNewEpic(Epic epic) {

        int newEpicId = taskID++;
        epic.setTaskId(newEpicId);
        Epic copyOfEpic = new Epic(epic.getTaskName(), epic.getTaskDescription(), newEpicId);


        epicHashMap.put(newEpicId, copyOfEpic);
        System.out.println("Успешно добавлен новый эпик под номером " + newEpicId);
        return newEpicId;
    }

    // ОБНОВЛЕНИЕ ЗАДАЧИ:

    @Override
    public void updateTask(Task task) {
        if (task == null) return;
        int id = task.getTaskId();
        Task stored = taskHashMap.get(id);
        if (stored == null) return;

        priorityRemovedById(id);

        stored.setTaskName(task.getTaskName());
        stored.setTaskDescription(task.getTaskDescription());
        stored.setStatus(task.getStatus());

        stored.setStartTime(task.getStartTime() != null ? task.getStartTime() : Optional.empty());
        stored.setDuration(task.getDuration() != null ? task.getDuration() : Duration.ZERO);

        throwExceptionIfTasksOverlap(stored);

        priorityAdd(stored);
    }

    @Override
    public void updateSubtask(Subtask updateSubtask) {

        if (updateSubtask == null) return;

        int id = updateSubtask.getTaskId();
        if (id <= 0) {
            throw new IllegalArgumentException("updateSubtask: у подзадачи должен быть валидный taskId");
        }

        Subtask storedSubtask = subtaskHashMap.get(id);
        if (storedSubtask == null) return;

        priorityRemovedById(id);

        storedSubtask.setTaskName(updateSubtask.getTaskName());
        storedSubtask.setTaskDescription(updateSubtask.getTaskDescription());
        storedSubtask.setStatus(updateSubtask.getStatus());

        storedSubtask.setStartTime(updateSubtask.getStartTime() != null ? updateSubtask.getStartTime() : Optional.empty());
        storedSubtask.setDuration(updateSubtask.getDuration() != null ? updateSubtask.getDuration() : Duration.ZERO);

        throwExceptionIfTasksOverlap(storedSubtask);

        priorityAdd(storedSubtask);

        subtaskHashMap.put(id, storedSubtask); // явное обновление

        Epic epic = epicHashMap.get(storedSubtask.getEpicId());

        if (epic != null) {
            var list = epic.getTasksInEpic();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getTaskId() == id) {
                    list.set(i, storedSubtask);
                    break;
                }
            }
            updateEpicStatus(epic);
            updateEpicTimeFields(epic);
        }

    }

    @Override
    public void updateEpic(Epic epic) {

        epicHashMap.put(epic.getTaskId(), epic);

    }

    // УДАЛЕНИЕ ЗАДАЧИ ПО ИДЕНТИФИКАТОРУ:

    @Override
    public void deleteTask(int taskID) {
        if (taskHashMap.containsKey(taskID)) {
            Task removed = taskHashMap.remove(taskID);
            if (removed != null) {
                priorityRemovedById(taskID);
                historyManager.remove(taskID);
            }
        } else {
            System.out.println("Нет задачи по указанному ID.");
        }


    }

    @Override
    public void deleteSubtask(int subtaskID) {


        if (subtaskHashMap.containsKey(subtaskID)) {

            Subtask subtask = subtaskHashMap.get(subtaskID);
            Epic epic = epicHashMap.get(subtask.getEpicId());
            priorityRemovedById(subtaskID);
            if (epic != null) {
                epic.getTasksInEpic().remove(subtask);
                updateEpicStatus(epic);
                updateEpicTimeFields(epic);
            }


            subtaskHashMap.remove(subtaskID);
            historyManager.remove(subtaskID);

        } else {
            System.out.println("Подзадача с указанным ID не соответствует ни одному из эпиков.");
        }

    }

    @Override
    public void deleteEpic(int epicID) {
        if (epicHashMap.containsKey(epicID)) {
            Epic epic = epicHashMap.remove(epicID);

            epic.getTasksInEpic().forEach(subtask ->
            {
                subtaskHashMap.remove(subtask.getTaskId());
                priorityRemovedById(subtask.getTaskId());
                historyManager.remove(subtask.getTaskId());
            });

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

        List<Subtask> subtasks = epic.getTasksInEpic();
        if (subtasks == null || subtasks.isEmpty()) {
            epic.setStatus(Task.Status.NEW);
            return;
        }

        boolean allDone = subtasks.stream()
                .filter(subtask -> subtask != null)
                .allMatch(subtask -> subtask.getStatus() == Task.Status.DONE);

        boolean allNew = subtasks.stream()
                .filter(subtask -> subtask != null)
                .allMatch(subtask -> subtask.getStatus() == Task.Status.NEW);

        if (allDone) {
            epic.setStatus(Task.Status.DONE);
        } else if (allNew) {
            epic.setStatus(Task.Status.NEW);
        } else {
            epic.setStatus(Task.Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritized.stream().collect(Collectors.toList());
    }

    @Override
    public Optional<LocalDateTime> getEndTimeForEpic(Epic epic) {
        List<Subtask> tasksInEpicList = epic.getTasksInEpic();
        if (tasksInEpicList.isEmpty()) return Optional.empty();

        Optional<LocalDateTime> maxEnd = Optional.empty();
        for (Subtask subtask : tasksInEpicList) {
            var end = subtask.getEndTime();

            if (end.isPresent()) {
                if (maxEnd.isEmpty() || end.get().isAfter(maxEnd.get())) {
                    maxEnd = end;
                }
            }
        }
        return maxEnd;
    }

    public static int getTaskID() {
        return taskID;
    }

}