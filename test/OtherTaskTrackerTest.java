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
        int epicId = manager.createNewEpic(epic);

        Subtask subtask = new Subtask("subtask", "desc",
                Task.Status.NEW, manager.getEpictaskWithID(epicId).getTaskId());
        int subtaskId = manager.createNewSubtask(subtask);

        assertEquals(epicId, manager.getSubtaskWithID(subtaskId).getEpicId());

        manager.deleteSubtask(subtaskId);

        assertNull(manager.getSubtaskWithID(subtaskId));

    }

    @Test
    void epicShouldDropDeletedSubtaskId() {
        Epic epic = new Epic("epic", "desc", 1);
        int epicId = manager.createNewEpic(epic);

        Subtask subtask1 = new Subtask("s1", "desc",
                Task.Status.NEW, epicId);
        Subtask subtask2 = new Subtask("s2", "desc",
                Task.Status.IN_PROGRESS, epicId);
        int subtaskId1 = manager.createNewSubtask(subtask1);
        int subtaskId2 = manager.createNewSubtask(subtask2);

        // оба id внутри эпика
        Epic epicFromManager = manager.getEpictaskWithID(epicId);
        assertFalse(epicFromManager.getTasksInEpic().isEmpty());
        assertEquals(2, epicFromManager.getTasksInEpic().size(), "в эпике должно храниться 2 подзадачи");

        // удаляем одну подзадачу
        manager.deleteSubtask(subtaskId1);

        // в эпике не должно остаться subtaskId1
        Epic after = manager.getEpictaskWithID(epicId);
        assertFalse(after.getTasksInEpic().isEmpty());
        assertEquals(1, after.getTasksInEpic().size(), "после удаления должна остаться 1 подзадача");

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

        assertFalse(manager.getEpictaskWithID(epicId).getTasksInEpic().isEmpty());

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

        assertFalse(manager.getEpictaskWithID(epic1.getTaskId()).getTasksInEpic().isEmpty(),
                "подзадача должна оставаться в исходном эпике");
        assertTrue(manager.getEpictaskWithID(epic2.getTaskId()).getTasksInEpic().isEmpty(),
                "подзадача не должна появляться во втором эпике после внешнего изменения");

    }

    @Test
    void externalSubtaskStatusChangeMustNotUpdateEpic() {
        Epic epic = new Epic("epic", "desc", 1);
        int epicId = manager.createNewEpic(epic);

        Subtask subtask = new Subtask("subtask", "desc",
                Task.Status.DONE, epic.getTaskId());

        int subtaskId = manager.createNewSubtask(subtask);

        subtask.setStatus(Task.Status.NEW);

        Task.Status epicStatusBefore = manager.getEpictaskWithID(epicId).getStatus();
        assertEquals(Task.Status.DONE, epicStatusBefore,
                "статус эпика не должен измениться от внешнего сеттера");

        Subtask sUpdated = new Subtask(subtask.getTaskName(), subtask.getTaskDescription(),
                Task.Status.NEW, epicId);

        sUpdated.setTaskId(subtaskId);
        manager.updateSubtask(sUpdated);

        Task.Status epicStatusAfter = manager.getEpictaskWithID(epicId).getStatus();
        assertEquals(Task.Status.NEW, epicStatusAfter,
                "после update через менеджер статус эпика должен быть перерасчитан");

    }

}
