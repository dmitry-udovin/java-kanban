package tasktracker.managers;

import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class LinkedListForTasks<K,V> extends LinkedHashMap<Integer, Node<Task>> {

    private Node<Task> head;
    private Node<Task> tail;

    public void linkLast(Task task) {

        if (task == null) return;

        int id = task.getTaskId();

        Node<Task> existing = super.get(id);
        if (existing != null) {
            if (existing != tail) {
                Node<Task> p = existing.prev;
                Node<Task> q = existing.next;
                if (p != null) p.next = q;
                else head = q;
                if (q != null) q.prev = p;
                else tail = p;
                existing.prev = existing.next = null;

                appendTail(existing);
            }
            return;
        }

        Node<Task> node = new Node<>(task);

        appendTail(node);
        super.put(id,node);

    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> res = new ArrayList<>(super.size());
        for(Node<Task> cur = head; cur != null; cur = cur.next) {
            res.add(cur.data);
        }
        return res;
    }

    public void removeNode(Node<Task> node) {
        if (node == null) return;

        int key = (node.data != null) ? node.data.getTaskId() : -1;

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

        if (key != -1) {
            super.remove(key);
        }

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