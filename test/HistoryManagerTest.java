import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.managers.HistoryManager;
import tasktracker.managers.InMemoryHistoryManager;
import tasktracker.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private HistoryManager historyManager;

    private Task createTask(int id) {
        return new Task("task", "desc", Task.Status.NEW, id);
    }

    @BeforeEach
    void createManager() {
        historyManager = new InMemoryHistoryManager();
    }


    @Test
    void shouldAddExistingMoveToTailWithoutDuplicate() {
        historyManager.add(createTask(1));
        historyManager.add(createTask(2));
        historyManager.add(createTask(3));

        historyManager.add(createTask(2));

        assertEquals(List.of(1, 3, 2), historyManager.getHistory().stream().map(Task::getTaskId).toList());

        assertEquals(3, historyManager.getHistory().size(), "must not create duplicate");

    }

}