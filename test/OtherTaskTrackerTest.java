import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.managers.Managers;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

public class OtherTaskTrackerTest {

    TaskManager manager;

    @BeforeEach
    void createManager() {
        manager = Managers.getDefault();
    }

    @Test
    void removedSubtaskShouldNotKeepEpicLink() {
        Epic epic = new Epic("epic", "desc", 1);
        manager.createNewEpic(epic);

        Subtask subtask = new Subtask("subtask", "desc",
                Task.Status.NEW, epic.getTaskId());
        manager.createNewSubtask(subtask);
        int subtaskId = subtask.getTaskId();
        int epicId = epic.getTaskId();

        assertEquals(epicId, manager.getSubtaskWithID(subtaskId).getEpicId());

        manager.deleteSubtask(subtaskId);

        assertNull(manager.getSubtaskWithID(subtaskId));

    }

    @Test
    void epicShouldDropDeletedSubtaskId() {
        Epic epic = new Epic("epic", "desc", 1);
        manager.createNewEpic(epic);
        int epicId = epic.getTaskId();

        Subtask subtask1 = new Subtask("s1", "desc",
                Task.Status.NEW, epicId);
        Subtask subtask2 = new Subtask("s2", "desc",
                Task.Status.IN_PROGRESS, epicId);
        manager.createNewSubtask(subtask1);
        manager.createNewSubtask(subtask2);

        int id1 = subtask1.getTaskId();
        int id2 = subtask2.getTaskId();

        // оба id внутри эпика
        Epic fromManager = manager.getEpictaskWithID(epicId);
        assertTrue(fromManager.getTasksInEpic().contains(id1));
        assertTrue(fromManager.getTasksInEpic().contains(id2));

        // удаляем одну подзадачу
        manager.deleteSubtask(id1);

        // в эпике не должно остаться id1
        Epic after = manager.getEpictaskWithID(epicId);
        assertFalse(after.getTasksInEpic().contains(id1));
        assertTrue(after.getTasksInEpic().contains(id2));

    }

    @Test
    void epicShouldBeCleanAfterRemoveAllSubtasks() {
        Epic epic = new Epic("epic", "desc", 1);
        manager.createNewEpic(epic);

        int epicId = epic.getTaskId();

        Subtask subtask1 = new Subtask("s1", "desc",
                Task.Status.NEW, epicId);
        Subtask subtask2 = new Subtask("s2", "desc",
                Task.Status.IN_PROGRESS, epicId);

        manager.createNewSubtask(subtask1);
        manager.createNewSubtask(subtask2);

        int id1 = subtask1.getTaskId();
        int id2 = subtask2.getTaskId();

        assertTrue(manager.getEpictaskWithID(epicId).getTasksInEpic().contains(id1));
        assertTrue(manager.getEpictaskWithID(epicId).getTasksInEpic().contains(id2));

        manager.removeAllSubtasks();

        Epic epicAfterClean = manager.getEpictaskWithID(epicId);
        assertTrue(epicAfterClean.getTasksInEpic().isEmpty(), "эпик все ещё хранит подзадачи");
    }

    // ЦЕЛОСТНОСТЬ ДАННЫХ ВНУТРИ МЕНЕДЖЕРА

    @Test
    void externalIdChangeMustNotAffectOnManager() {
        Task task = new Task("task", "desc", Task.Status.NEW);
        manager.createNewTask(task);
        int oldId = task.getTaskId();

        task.setTaskId(9999);

        Task fromManagerOld = manager.getTaskWithID(oldId);
        assertNotNull(fromManagerOld, "по старому id задача должна находиться");
        assertEquals(oldId, fromManagerOld.getTaskId(), "id внутри менеджера не должен измениться");

        Task fromManagerNew = manager.getTaskWithID(9999);
        assertNull(fromManagerNew, "по присвоенному id задачи в менеджере быть не должно");

    }

    @Test
    void externalEpicIdChangeMustNotMoveSubtaskBetweenEpics() {
        Epic epic1 = new Epic("epic", "desc", 1);
        Epic epic2 = new Epic("epic", "desc", 2);

        manager.createNewEpic(epic1);
        manager.createNewEpic(epic2);

        Subtask subtask = new Subtask("subtask", "desc",
                Task.Status.NEW, epic1.getTaskId());
        manager.createNewSubtask(subtask);
        int subtaskId = subtask.getTaskId();

        // попытка перепривязать подзадачу к другому эпику
        subtask.setTaskId(epic2.getTaskId());

        assertTrue(manager.getEpictaskWithID(epic1.getTaskId()).getTasksInEpic().contains(subtaskId),
                "подзадача должна оставаться в исходном эпике");
        assertFalse(manager.getEpictaskWithID(epic2.getTaskId()).getTasksInEpic().contains(subtaskId),
                "подзадача не должна появляться во втором эпике после внешнего изменения");

    }

    @Test
    void externalSubtaskStatusChangeMustNotUpdateEpic() {
        Epic epic = new Epic("epic", "desc", 1);
        manager.createNewEpic(epic);

        Subtask subtask = new Subtask("subtask", "desc",
                Task.Status.NEW, epic.getTaskId());

        manager.createNewSubtask(subtask);

        subtask.setStatus(Task.Status.DONE);

        Task.Status epicStatusBefore = manager.getEpictaskWithID(epic.getTaskId()).getStatus();
        assertEquals(Task.Status.NEW, epicStatusBefore,
                "статус эпика не должен измениться от внешнего сеттера");

        Subtask sUpdated = new Subtask(subtask.getTaskName(), subtask.getTaskDescription(),
                Task.Status.DONE, subtask.getEpicId());

        manager.updateSubtask(sUpdated);

        Task.Status epicStatusAfter = manager.getEpictaskWithID(epic.getTaskId()).getStatus();
        assertEquals(Task.Status.DONE, epicStatusAfter,
                "после update через менеджер статус эпика должен быть перерасчитан");

    }

}
