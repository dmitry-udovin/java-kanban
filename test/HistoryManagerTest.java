import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.managers.HistoryManager;
import tasktracker.managers.Managers;
import tasktracker.tasks.Task;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    private HistoryManager history;
    private Task t1;
    private Task t2;
    private Task t3;

    @BeforeEach
    void shouldInitHistoryBeforeEach() {
        history = Managers.getDefaultHistory();

        t1 = new Task("T1", "d1", Task.Status.NEW, Optional.empty(), Duration.ZERO);
        t1.setTaskId(1);
        t2 = new Task("T2", "d2", Task.Status.NEW, Optional.empty(), Duration.ZERO);
        t2.setTaskId(2);
        t3 = new Task("T3", "d3", Task.Status.NEW, Optional.empty(), Duration.ZERO);
        t3.setTaskId(3);
    }

    @Test
    void shouldReturnEmptyListWhenHistoryIsEmpty() {
        assertTrue(history.getHistory().isEmpty());
    }

    @Test
    void shouldNotDuplicateTasksInHistory() {
        history.add(t1);
        history.add(t1);
        history.add(t1);
        List<Task> list = history.getHistory();
        assertEquals(1, list.size());
        assertEquals(t1, list.get(0));
    }

    @Test
    void shouldRemoveTasksFromBeginningMiddleAndEnd() {
        history.add(t1);
        history.add(t2);
        history.add(t3);

        history.remove(1); // remove from beginning
        assertEquals(List.of(t2, t3), history.getHistory());

        history.remove(2); // remove from middle
        assertEquals(List.of(t3), history.getHistory());

        history.remove(3); // remove from end
        assertTrue(history.getHistory().isEmpty());
    }

}