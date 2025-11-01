package tasktracker.managers;

import tasktracker.exceptions.ManagerSaveException;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;
    private boolean confirmSave = false;

    public FileBackedTaskManager(String fileName) {
        this.path = Paths.get(fileName).toAbsolutePath();
    }

    public void save() {
        if (confirmSave) return;
        try {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                bw.write("id,type,name,status,description,epic");
                bw.newLine();

                for (Task task : getTaskList()) {
                    bw.write(toString(task));
                    bw.newLine();
                }

                for (Epic epic : getEpicList()) {
                    bw.write(toString(epic));
                    bw.newLine();
                }

                for (Subtask subtask : getSubtaskList()) {
                    bw.write(toString(subtask));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в файл: " + path, e);
        }
    }


    public String toString(Task task) {

        String id = String.valueOf(task.getTaskId());
        String type = task.getType().name();
        String name = task.getTaskName();
        String status = task.getStatus().name();
        String desc = task.getTaskDescription();
        String epic = "";

        if (task.getType() == Task.TaskTypes.SUBTASK) {
            epic = String.valueOf(((Subtask) task).getEpicId());
        }
        return String.join(",", id, type, name, status, desc, epic);
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toString());
        manager.confirmSave = true;
        try {
            if (!Files.exists(file)) {
                // пустой менеджер
                return manager;
            }
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                return manager;
            }

            String header = "id,type,name,status,description,epic";
            if (!lines.get(0).trim().equalsIgnoreCase(header)) {
                throw new ManagerSaveException("Некорректный заголовок CSV: " + lines.get(0));
            }

            int maxId = 0;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }

                Task parsed = manager.fromString(line);
                if (parsed == null) {
                    continue;
                }

                maxId = Math.max(maxId, parsed.getTaskId());

                switch (parsed.getType()) {
                    case TASK -> manager.putTaskInternal(parsed);
                    case EPIC -> manager.putEpicInternal((Epic) parsed);
                    case SUBTASK -> manager.putSubtaskInternal((Subtask) parsed);
                }
            }

            manager.setNextId(maxId + 1);

            for (Epic epic : manager.epicHashMap.values()) {
                manager.updateEpicStatus(epic);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла: " + file, e);
        } finally {
            manager.confirmSave = false;
        }

        manager.save();
        return manager;

    }

    public Task fromString(String line) {

        String[] a = line.split(",", -1);

        if (a.length < 6) {
            throw new ManagerSaveException("CSV: ожидается 6 колонок, строка: " + line);
        }

        int id = Integer.parseInt(a[0]);
        Task.TaskTypes type = Task.TaskTypes.valueOf(a[1]);
        String name = a[2];
        Task.Status status = Task.Status.valueOf(a[3]);
        String desc = a[4];
        String epicCol = a[5];

        switch (type) {
            case TASK -> {
                return new Task(name, desc, status, id);
            }
            case EPIC -> {
                Epic epic = new Epic(name, desc, id);
                epic.setStatus(status);
                return epic;
            }
            case SUBTASK -> {
                int epicId = epicCol.isEmpty() ? 0 : Integer.parseInt(epicCol);
                Subtask subtask = new Subtask(name, desc, status, epicId);
                subtask.setTaskId(id);
                return subtask;
            }
            default -> {
                throw new ManagerSaveException("Неизвестный тип: " + type);
            }

        }

    }

    // переопределение методов InMemoryTaskManager

    @Override
    public int createNewTask(Task task) {
        int id = super.createNewTask(task);
        save();
        return id;
    }

    @Override
    public int createNewEpic(Epic epic) {
        int id = super.createNewEpic(epic);
        save();
        return id;
    }

    @Override
    public int createNewSubtask(Subtask subtask) {
        int id = super.createNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask updateSubtask) {
        super.updateSubtask(updateSubtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int taskID) {
        super.deleteTask(taskID);
        save();
    }

    @Override
    public void deleteSubtask(int subtaskID) {
        super.deleteSubtask(subtaskID);
        save();
    }

    @Override
    public void deleteEpic(int epicID) {
        super.deleteEpic(epicID);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        save();
    }

}
