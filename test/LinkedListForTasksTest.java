import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.managers.LinkedListForTasks;
import tasktracker.managers.Node;
import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LinkedListForTasksTest {

    private Task createTask(int id) {
        return new Task("Task", "desc", Task.Status.NEW, id);
    }

    private List<Integer> getIdsList(List<Task> tasks) {
        List<Integer> out = new ArrayList<>();
        for (Task x : tasks) out.add(x.getTaskId());
        return out;
    }

    private LinkedListForTasks<Integer, Node<Task>> list;

    @BeforeEach
    public void createNewLinkedList() {
        list = new LinkedListForTasks<>();
    }

    @Test
    void insertsIntoEmptyList() {
        list.linkLast(createTask(1));

        assertEquals(1, list.size(), "size after first insert");
        assertEquals(List.of(1), getIdsList(list.getTasks()));

    }

    @Test
    void shouldAppendTasksInRightOrder() {
        list.linkLast(createTask(1));
        list.linkLast(createTask(2));
        list.linkLast(createTask(3));

        assertEquals(3, list.size());
        assertEquals(List.of(1, 2, 3), getIdsList(list.getTasks()));

    }

    @Test
    void shouldInsertExistingNodeToTailWithoutDuplicate() {
        list.linkLast(createTask(1));
        list.linkLast(createTask(2));
        list.linkLast(createTask(3));

        list.linkLast(createTask(2));

        assertEquals(List.of(1, 3, 2), getIdsList(list.getTasks()));
        assertEquals(3, list.size(), "must not create duplicate");

    }

    @Test
    void shouldCorrectRemoveHead() {

        list.linkLast(createTask(1));
        list.linkLast(createTask(2));
        list.linkLast(createTask(3));

        Node<Task> headNode = list.get(1);

        list.removeNode(headNode);

        assertEquals(2, list.size());
        assertEquals(List.of(2, 3), getIdsList(list.getTasks()));

        assertNull(headNode.prev);
        assertNull(headNode.next);

    }

    @Test
    void shouldCorrectRemoveTail() {
        list.linkLast(createTask(1));
        list.linkLast(createTask(2));
        list.linkLast(createTask(3));

        Node<Task> tailNode = list.get(3);

        list.removeNode(tailNode);

        assertEquals(2, list.size());
        assertEquals(List.of(1, 2), getIdsList(list.getTasks()));
        assertNull(tailNode.prev);
        assertNull(tailNode.next);

    }

    @Test
    void shouldCorrectRemoveSingleElementList() {
        list.linkLast(createTask(15));

        Node<Task> oneNode = list.get(15);

        list.removeNode(oneNode);

        assertEquals(0, list.size());
        assertTrue(list.getTasks().isEmpty());

    }

}
