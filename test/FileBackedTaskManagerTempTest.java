import org.junit.jupiter.api.Test;
import tasktracker.managers.FileBackedTaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTempTest {

    private Path tempCsv(String suffix) throws Exception {
        File f = File.createTempFile("kanban", suffix);
        f.deleteOnExit();
        return f.toPath();
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

        //задача
        var task = new Task("task", "desc", Task.Status.NEW);
        int taskId = manager.createNewTask(task);

        //эпик
        var epic = new Epic("epic", "e-desc", 0);
        int epicId = manager.createNewEpic(epic);

        //подзадача
        var subtask = new Subtask("subtask", "s-desc", Task.Status.NEW, epicId);
        int subId = manager.createNewSubtask(subtask);

        String text = Files.readString(csv);

        assertTrue(text.startsWith("id,type,name,status,description,epic"), "Должен быть заголовок CSV");
        assertTrue(text.contains(taskId + ",TASK,task,NEW,desc,"), "Task-строка должна присутствовать");
        assertTrue(text.contains(epicId + ",EPIC,epic"), "Epic-строка должна присутствовать");
        assertTrue(text.contains(subId + ",SUBTASK,subtask,NEW,s-desc," + epicId),
                "Subtask-строка должна содержать epicId");

    }

    @Test
    void shouldLoadSeveralTasksFromCsv_UsingTempFile() throws Exception {
        Path csv = tempCsv(".csv");
        var manager = new FileBackedTaskManager(csv.toString());

        var t = new Task("T", "D", Task.Status.IN_PROGRESS);
        int taskId = manager.createNewTask(t);

        var e = new Epic("E", "ED", 0);
        int epicId = manager.createNewEpic(e);

        var s1 = new Subtask("S1", "SD1", Task.Status.NEW, epicId);
        int s1Id = manager.createNewSubtask(s1);

        var s2 = new Subtask("S2", "SD2", Task.Status.DONE, epicId);
        int s2Id = manager.createNewSubtask(s2);

        var loaded = FileBackedTaskManager.loadFromFile(csv);

        // проверяем их количество в менеджере

        assertEquals(1, loaded.getTaskList().size());
        assertEquals(1, loaded.getEpicList().size());
        assertEquals(2, loaded.getSubtaskList().size());

        // поля задачи

        var tLoaded = loaded.getTaskWithID(taskId);
        assertNotNull(tLoaded);
        assertEquals("T", tLoaded.getTaskName());
        assertEquals("D", tLoaded.getTaskDescription());
        assertEquals(Task.Status.IN_PROGRESS, tLoaded.getStatus());

        // эпик и его подзадачи

        var epicLoaded = loaded.getEpictaskWithID(epicId);

        assertEquals("E", epicLoaded.getTaskName());
        assertNotNull(epicLoaded);
        List<Integer> subIds = epicLoaded.getTasksInEpic().stream().map(Subtask::getTaskId).toList();

        assertTrue(subIds.containsAll(List.of(s1Id, s2Id)));

        // сабстаски имеют корректный id
        assertEquals(epicId, loaded.getSubtaskWithID(s1Id).getEpicId());
        assertEquals(epicId, loaded.getSubtaskWithID(s2Id).getEpicId());

        // статус загруженного эпика
        assertEquals(Task.Status.IN_PROGRESS, epicLoaded.getStatus());

    }

    @Test
    void shouldDeleteSubtask_RemovesLinkFromEpic_AndSave() throws Exception {
        Path csv = tempCsv(".csv");
        var manager = new FileBackedTaskManager(csv.toString());

        var epic = new Epic("E", "ED", 0);
        int epicId = manager.createNewEpic(epic);

        var s1 = new Subtask("S1", "SD1", Task.Status.NEW, epicId);
        int s1Id = manager.createNewSubtask(s1);

        var s2 = new Subtask("S2", "SD2", Task.Status.NEW, epicId);
        int s2Id = manager.createNewSubtask(s2);

        manager.deleteSubtask(s1Id);
        var currentEpic = manager.getEpictaskWithID(epicId);
        List<Integer> idsNow = currentEpic.getTasksInEpic().stream().map(Subtask::getTaskId).toList();
        assertFalse(idsNow.contains(s1Id));
        assertTrue(idsNow.contains(s2Id));
        assertEquals(Task.Status.NEW, currentEpic.getStatus());

        // проверка после перезагрузки
        var reloaded = FileBackedTaskManager.loadFromFile(csv);
        var reloadEpic = reloaded.getEpictaskWithID(epicId);
        List<Integer> idsReload = reloadEpic.getTasksInEpic().stream().map(Subtask::getTaskId).toList();
        assertFalse(idsReload.contains(s1Id));
        assertTrue(idsReload.contains(s2Id));

    }

    @Test
    void idCounterShouldContinueAfterReload() throws Exception {
        Path csv = tempCsv(".csv");
        var manager = new FileBackedTaskManager(csv.toString());

        int task1Id = manager.createNewTask(new Task("task1", "desc1", Task.Status.NEW));
        int task2Id = manager.createNewTask(new Task("task2", "desc2", Task.Status.NEW));

        var loaded = FileBackedTaskManager.loadFromFile(csv);
        int task3Id = loaded.createNewTask(new Task("task3", "desc3", Task.Status.NEW));

        assertTrue(task3Id > Math.max(task1Id, task2Id), "после перезагрузки новый айди" +
                " должен продолжаться после максимального");

        String text = Files.readString(csv);
        assertTrue(text.contains(task3Id + ",TASK,task3,NEW,desc3,"), "новая задача должна сохраниться " +
                "после перезагрузки");

    }

}
