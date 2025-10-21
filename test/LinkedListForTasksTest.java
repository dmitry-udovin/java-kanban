import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.managers.LinkedListForTasks;
import tasktracker.managers.Node;
import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LinkedListForTasksTest {

    private Task createNewTask(int id) {
        return new Task("Task", "desc", Task.Status.NEW, id);
    }

    private List<Integer> getTasksIdList(List<Task> tasks) {
        List<Integer> out = new ArrayList<>();
        for (Task x : tasks) out.add(x.getTaskId());
        return out;
    }

    private LinkedListForTasks list;

    @BeforeEach
    void setUp() {
        list = new LinkedListForTasks();
    }

    @Test
    void insertsIntoEmptyList() {
        list.linkLast(createNewTask(1));

        assertEquals(1, list.getTasks().size(), "size after first insert");
        assertEquals(List.of(1), getTasksIdList(list.getTasks()));
    }

    @Test
    void shouldAppendTasksInRightOrder() {
        list.linkLast(createNewTask(1));
        list.linkLast(createNewTask(2));
        list.linkLast(createNewTask(3));

        assertEquals(3, list.getTasks().size());
        assertEquals(List.of(1, 2, 3), getTasksIdList(list.getTasks()));
    }

    @Test
    void shouldCorrectRemoveHead() {
        Node<Task> n1 = list.linkLast(createNewTask(1));
        list.linkLast(createNewTask(2));
        list.linkLast(createNewTask(3));

        list.removeNode(n1);

        assertEquals(2, list.getTasks().size());
        assertEquals(List.of(2, 3), getTasksIdList(list.getTasks()));
        assertNull(n1.prev);
        assertNull(n1.next);
    }

    @Test
    void shouldCorrectRemoveTail() {
        list.linkLast(createNewTask(1));
        list.linkLast(createNewTask(2));
        Node<Task> n3 = list.linkLast(createNewTask(3));

        list.removeNode(n3);

        assertEquals(2, list.getTasks().size());
        assertEquals(List.of(1, 2), getTasksIdList(list.getTasks()));
        assertNull(n3.prev);
        assertNull(n3.next);
    }

    @Test
    void shouldCorrectRemoveSingleElementList() {
        Node<Task> one = list.linkLast(createNewTask(15));

        list.removeNode(one);

        assertEquals(0, list.getTasks().size());
        assertTrue(list.getTasks().isEmpty());
    }
}