import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exceptions.TaskTimeException;
import tasktracker.managers.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void shouldInitManagerBeforeEach() {
        manager = createManager();
    }

    protected Optional<LocalDateTime> t(int y, int m, int d, int h, int min) {
        return Optional.of(LocalDateTime.of(y, m, d, h, min));
    }

    protected Duration mins(long m) {
        return Duration.ofMinutes(m);
    }

    protected Task makeTask(String name, Optional<LocalDateTime> start, Duration dur) {
        return new Task(name, "desc-" + name, Task.Status.NEW, start, dur);
    }

    protected Epic makeEpic(String name) {
        return new Epic(name, "epic-" + name, 0);
    }

    protected Subtask makeSubtask(String name, int epicId, Optional<LocalDateTime> start, Duration dur) {
        return new Subtask(name, "sub-" + name, Task.Status.NEW, epicId, start, dur);
    }

    @Test
    void shouldSetEpicStatusNewWhenAllSubtasksNew() {
        int epicId = manager.createNewEpic(makeEpic("E"));
        manager.createNewSubtask(makeSubtask("s1", epicId, t(2025, 1, 1, 10, 0), mins(10)));
        manager.createNewSubtask(makeSubtask("s2", epicId, t(2025, 1, 1, 11, 0), mins(20)));

        Epic epic = manager.getEpictaskWithID(epicId);
        assertEquals(Task.Status.NEW, epic.getStatus());
    }

    @Test
    void shouldSetEpicStatusDoneWhenAllSubtasksDone() {
        int epicId = manager.createNewEpic(makeEpic("E"));

        Subtask s1 = makeSubtask("s1", epicId, t(2025, 1, 1, 10, 0), mins(10));
        s1.setStatus(Task.Status.DONE);
        Subtask s2 = makeSubtask("s2", epicId, t(2025, 1, 1, 11, 0), mins(20));
        s2.setStatus(Task.Status.DONE);

        manager.createNewSubtask(s1);
        manager.createNewSubtask(s2);

        Epic epic = manager.getEpictaskWithID(epicId);
        assertEquals(Task.Status.DONE, epic.getStatus());
    }

    @Test
    void shouldSetEpicStatusInProgressWhenNewAndDone() {
        int epicId = manager.createNewEpic(makeEpic("E"));

        Subtask s1 = makeSubtask("s1", epicId, t(2025, 1, 1, 10, 0), mins(10));
        s1.setStatus(Task.Status.NEW);
        Subtask s2 = makeSubtask("s2", epicId, t(2025, 1, 1, 11, 0), mins(20));
        s2.setStatus(Task.Status.DONE);

        manager.createNewSubtask(s1);
        manager.createNewSubtask(s2);

        Epic epic = manager.getEpictaskWithID(epicId);
        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldSetEpicStatusInProgressWhenAllSubtasksInProgress() {
        int epicId = manager.createNewEpic(makeEpic("E"));

        Subtask s1 = makeSubtask("s1", epicId, t(2025, 1, 1, 10, 0), mins(10));
        s1.setStatus(Task.Status.IN_PROGRESS);
        Subtask s2 = makeSubtask("s2", epicId, t(2025, 1, 1, 11, 0), mins(20));
        s2.setStatus(Task.Status.IN_PROGRESS);

        manager.createNewSubtask(s1);
        manager.createNewSubtask(s2);

        Epic epic = manager.getEpictaskWithID(epicId);
        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldSubtaskContainEpicIdAndEpicContainSubtask() {
        int epicId = manager.createNewEpic(makeEpic("E"));
        int subId = manager.createNewSubtask(makeSubtask("S", epicId, t(2025, 1, 1, 9, 0), mins(5)));

        Subtask sub = manager.getSubtaskWithID(subId);
        Epic epic = manager.getEpictaskWithID(epicId);

        assertEquals(epicId, sub.getEpicId(), "у подзадачи должен быть корректный epicId");
        assertTrue(epic.getTasksInEpic().stream()
                .anyMatch(s -> s.getTaskId() == subId), "эпик должен содержать подзадачу");

        List<Subtask> fromEpic = manager.getSubtasksFromEpic(epicId);
        assertEquals(1, fromEpic.size());
        assertEquals(subId, fromEpic.get(0).getTaskId());
    }

    // ПРИОРИТЕТ

    @Test
    void shouldReturnTasksSortedByStartTimeAndNullsLast() {
        manager.createNewTask(makeTask("B", t(2025, 1, 1, 8, 0), mins(30)));
        manager.createNewTask(makeTask("A", t(2025, 1, 1, 9, 0), mins(30)));
        manager.createNewTask(makeTask("C", Optional.empty(), mins(10)));

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals("B", prioritized.get(0).getTaskName());
        assertEquals("A", prioritized.get(1).getTaskName());
        assertEquals("C", prioritized.get(2).getTaskName());
    }

    @Test
    void shouldRemoveTaskFromPrioritizedWhenDeleted() {
        int id = manager.createNewTask(makeTask("T", t(2025, 1, 1, 9, 0), mins(30)));
        assertEquals(1, manager.getPrioritizedTasks().size());
        manager.deleteTask(id);
        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    // ПРОВЕРКА ПЕРЕСЕЧЕНИЙ

    @Test
    void shouldThrowWhenCreateOverlappingTasks() {
        manager.createNewTask(makeTask("A", t(2025, 1, 1, 9, 0), mins(60)));   // 9:00–10:00

        Task overlapped = makeTask("B", t(2025, 1, 1, 9, 30), mins(30));       // 9:30–10:00

        assertThrows(TaskTimeException.class,
                () -> manager.createNewTask(overlapped),
                "добавление пересекающейся задачи должно бросать исключение");
    }

    @Test
    void shouldNotTreatTouchingBordersAsOverlap() {
        manager.createNewTask(makeTask("A", t(2025, 1, 1, 9, 0), mins(60)));   // до 10:00

        Task next = makeTask("B", t(2025, 1, 1, 10, 0), mins(30));             // с 10:00

        assertDoesNotThrow(() -> manager.createNewTask(next),
                "задачи, соприкасающиеся концами, не должны считаться пересечением");
    }

    @Test
    void shouldThrowWhenUpdateTaskToOverlappingInterval() {
        // A: 09:00–10:00
        int aId = manager.createNewTask(
                makeTask("A", t(2025, 1, 1, 9, 0), mins(60)));

        // B: 11:00–12:00 (пока не пересекается)
        int bId = manager.createNewTask(
                makeTask("B", t(2025, 1, 1, 11, 0), mins(60)));

        // хотим обновить B так, чтобы он пересёкся с A: 09:30–10:30
        Task updatedB = new Task(
                "B", "desc-B", Task.Status.NEW,
                t(2025, 1, 1, 9, 30), mins(60));
        updatedB.setTaskId(bId);

        assertThrows(IllegalStateException.class,
                () -> manager.updateTask(updatedB),
                "обновление задачи на пересекающий интервал должно бросать исключение");
    }

    // ВРЕМЯ ЭПИКА

    @Test
    void shouldCalculateEpicTimeFromSubtasks() {
        int epicId = manager.createNewEpic(makeEpic("E"));

        manager.createNewSubtask(makeSubtask("s1", epicId, t(2025, 1, 1, 9, 0), mins(30)));  // end 9:30
        manager.createNewSubtask(makeSubtask("s2", epicId, t(2025, 1, 1, 10, 0), mins(45))); // end 10:45

        Epic epic = manager.getEpictaskWithID(epicId);

        assertEquals(t(2025, 1, 1, 9, 0), epic.getStartTime(), "старт эпика = min(start сабтасок)");
        assertEquals(Optional.of(LocalDateTime.of(2025, 1, 1, 10, 45)),
                epic.getEndTime(), "конец эпика = max(end сабтасок)");
        assertEquals(Duration.ofMinutes(75), epic.getDuration(), "duration эпика = сумма durations сабтасок");
    }

    @Test
    void shouldResetEpicTimeWhenAllSubtasksRemoved() {
        int epicId = manager.createNewEpic(makeEpic("E"));
        int s1 = manager.createNewSubtask(makeSubtask("s1", epicId, t(2025, 1, 1, 9, 0), mins(30)));
        manager.deleteSubtask(s1);

        Epic epic = manager.getEpictaskWithID(epicId);
        assertEquals(Duration.ZERO, epic.getDuration());
        assertTrue(epic.getStartTime().isEmpty());
        assertTrue(epic.getEndTime().isEmpty());
    }

}
