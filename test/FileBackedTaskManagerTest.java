import org.junit.jupiter.api.Test;
import tasktracker.managers.FileBackedTaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private Path csvPath;

    private Path tempCsv(String suffix) throws Exception {
        File f = File.createTempFile("kanban", suffix);
        f.deleteOnExit();
        return f.toPath();
    }

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            csvPath = tempCsv(".csv");
            return new FileBackedTaskManager(csvPath.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldSaveAndLoadUsingTempFile() throws Exception {
        Path csv = tempCsv(".csv");

        var m = new FileBackedTaskManager(csv.toString());
        m.save();

        assertTrue(Files.exists(csv));
        var loaded = FileBackedTaskManager.loadFromFile(csv);
        assertTrue(loaded.getTaskList().isEmpty());
        assertTrue(loaded.getSubtaskList().isEmpty());
        assertTrue(loaded.getEpicList().isEmpty());
    }

    @Test
    void shouldSaveSeveralTasksToCsv_UsingTempFile() throws Exception {
        Path csv = tempCsv(".csv");
        var manager = new FileBackedTaskManager(csv.toString());

        // задача
        var task = new Task("task", "desc", Task.Status.NEW,
                Optional.of(LocalDateTime.of(2025, 1, 1, 9, 0)), Duration.ofMinutes(30));
        int taskId = manager.createNewTask(task);

        // эпик
        var epic = new Epic("epic", "e-desc", 0);
        int epicId = manager.createNewEpic(epic);

        // подзадача
        var subtask = new Subtask("subtask", "s-desc", Task.Status.NEW,
                epicId, Optional.of(LocalDateTime.of(2025, 1, 1, 10, 0)), Duration.ofMinutes(15));
        int subId = manager.createNewSubtask(subtask);

        String text = Files.readString(csv);

        assertTrue(text.startsWith("id,type,name,status,description,epic,startTime,duration"),
                "должен быть заголовок CSV");
        assertTrue(text.contains(taskId + ",TASK,task,NEW,desc,"),
                "Task-строка должна присутствовать");
        assertTrue(text.contains(epicId + ",EPIC,epic"), "Epic-строка должна присутствовать");
        assertTrue(text.contains(subId + ",SUBTASK,subtask,NEW,s-desc," + epicId),
                "Subtask-строка должна содержать epicId");
    }

    @Test
    void shouldLoadSeveralTasksFromCsv_UsingTempFile() throws Exception {
        Path csv = tempCsv(".csv");
        var manager = new FileBackedTaskManager(csv.toString());

        var t = new Task("T", "D", Task.Status.IN_PROGRESS,
                Optional.of(LocalDateTime.of(2025, 1, 1, 8, 0)), Duration.ofMinutes(20));
        int taskId = manager.createNewTask(t);

        var e = new Epic("E", "ED", 0);
        int epicId = manager.createNewEpic(e);

        var s1 = new Subtask("S1", "SD1", Task.Status.NEW, epicId,
                Optional.of(LocalDateTime.of(2025, 1, 1, 9, 0)), Duration.ofMinutes(10));
        int s1Id = manager.createNewSubtask(s1);

        var s2 = new Subtask("S2", "SD2", Task.Status.DONE, epicId,
                Optional.of(LocalDateTime.of(2025, 1, 1, 10, 0)), Duration.ofMinutes(15));
        int s2Id = manager.createNewSubtask(s2);

        var loaded = FileBackedTaskManager.loadFromFile(csv);

        assertEquals(1, loaded.getTaskList().size());
        assertEquals(1, loaded.getEpicList().size());
        assertEquals(2, loaded.getSubtaskList().size());

        var tLoaded = loaded.getTaskWithID(taskId);
        assertNotNull(tLoaded);
        assertEquals("T", tLoaded.getTaskName());
        assertEquals("D", tLoaded.getTaskDescription());
        assertEquals(Task.Status.IN_PROGRESS, tLoaded.getStatus());

        var epicLoaded = loaded.getEpictaskWithID(epicId);
        assertNotNull(epicLoaded);
        assertEquals("E", epicLoaded.getTaskName());

        List<Integer> subIds = epicLoaded.getTasksInEpic().stream().map(Subtask::getTaskId).toList();
        assertTrue(subIds.containsAll(List.of(s1Id, s2Id)));

        assertEquals(epicId, loaded.getSubtaskWithID(s1Id).getEpicId());
        assertEquals(epicId, loaded.getSubtaskWithID(s2Id).getEpicId());

        assertEquals(Task.Status.IN_PROGRESS, epicLoaded.getStatus());
    }

    @Test
    void shouldDeleteSubtask_RemovesLinkFromEpic_AndSave() throws Exception {
        Path csv = tempCsv(".csv");
        var manager = new FileBackedTaskManager(csv.toString());

        var epic = new Epic("E", "ED", 0);
        int epicId = manager.createNewEpic(epic);

        var s1 = new Subtask("S1", "SD1", Task.Status.NEW, epicId,
                Optional.of(LocalDateTime.of(2025, 1, 1, 9, 0)), Duration.ofMinutes(10));
        int s1Id = manager.createNewSubtask(s1);

        var s2 = new Subtask("S2", "SD2", Task.Status.NEW, epicId,
                Optional.of(LocalDateTime.of(2025, 1, 1, 10, 0)), Duration.ofMinutes(10));
        int s2Id = manager.createNewSubtask(s2);

        manager.deleteSubtask(s1Id);

        var currentEpic = manager.getEpictaskWithID(epicId);
        List<Integer> idsNow = currentEpic.getTasksInEpic().stream().map(Subtask::getTaskId).toList();
        assertFalse(idsNow.contains(s1Id));
        assertTrue(idsNow.contains(s2Id));
        assertEquals(Task.Status.NEW, currentEpic.getStatus());

        var reloaded = FileBackedTaskManager.loadFromFile(csv);
        var reloadEpic = reloaded.getEpictaskWithID(epicId);
        List<Integer> idsReload = reloadEpic.getTasksInEpic().stream().map(Subtask::getTaskId).toList();
        assertFalse(idsReload.contains(s1Id));
        assertTrue(idsReload.contains(s2Id));
    }

    @Test
    void shouldContinueIdCounterAfterReload() throws Exception {
        Path csv = tempCsv(".csv");
        var manager = new FileBackedTaskManager(csv.toString());

        int task1Id = manager.createNewTask(new Task("task1", "desc1", Task.Status.NEW,
                Optional.empty(), Duration.ZERO));
        int task2Id = manager.createNewTask(new Task("task2", "desc2", Task.Status.NEW,
                Optional.empty(), Duration.ZERO));

        var loaded = FileBackedTaskManager.loadFromFile(csv);
        int task3Id = loaded.createNewTask(new Task("task3", "desc3", Task.Status.NEW,
                Optional.empty(), Duration.ZERO));

        assertTrue(task3Id > Math.max(task1Id, task2Id),
                "после перезагрузки новый айди должен продолжаться после максимального");

        String text = Files.readString(csv);
        assertTrue(text.contains(task3Id + ",TASK,task3,NEW,desc3,"),
                "новая задача должна сохраниться после перезагрузки");
    }

}