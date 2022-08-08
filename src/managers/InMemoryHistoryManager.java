package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> map = new HashMap<>();

    public void linkList(Task element) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        map.put(element.getId(), newNode);
    }

    public List<Task> getTasks() throws IllegalStateException {
        List<Task> tasksHistory = new ArrayList<>();
        Node node = head;
        if (node == null || node.task == null) {
            throw new IllegalStateException("История просмотров пуста");
        }
        while (node != null) {
            tasksHistory.add(node.task);
            node = node.next;
        }
        return tasksHistory;
    }

    public List<Task> getHistory() throws IllegalStateException {
        return getTasks();
    }

    public void add(Task task) {
        if (map.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkList(task);
    }

    public void remove(int id) {
        Optional<Node> node = Optional.ofNullable(map.get(id));
        node.ifPresent(this::removeNode);
    }

    public void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;
        if (prev == null && next == null) {
            head = null;
            tail = null;
            return;
        }
        if (prev == null) {
            head = next;
            head.prev = null;
            return;
        }
        if (next == null) {
            prev.next = null;
            tail = prev;
            return;
        }
        prev.next = next;
        next.prev = prev;
    }

    private static class Node {
        private final Task task;
        private Node next;
        private Node prev;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }
}
