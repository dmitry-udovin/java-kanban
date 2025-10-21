package tasktracker.managers;

import tasktracker.tasks.Task;

import java.util.ArrayList;

public class LinkedListForTasks {

    private Node<Task> head;
    private Node<Task> tail;

    public Node<Task> linkLast(Task task) {

        if (task == null) return null;

        Node<Task> node = new Node<>(task);
        appendTail(node);
        return node;

    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> res = new ArrayList<>();
        for(Node<Task> cur = head; cur != null; cur = cur.next) {
            res.add(cur.data);
        }
        return res;
    }

    public void removeNode(Node<Task> node) {
        if (node == null) return;

        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;

        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }

        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }

        node.prev = null;
        node.next = null;
    }

    public void appendTail(Node<Task> n) {
        if (tail == null) {
            head = tail = n;
        } else {
            tail.next = n;
            n.prev = tail;
            tail = n;
        }
    }

}