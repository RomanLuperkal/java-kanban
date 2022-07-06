package managers;


import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    final private Map<Integer, Node> map;

    public InMemoryHistoryManager() {
        map = new HashMap<>();
    }

    public void linkList(Task element) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        map.put(element.getId(), newNode);
    }

    public List<Task> getTasks() throws IllegalStateException {
        List<Task> tasksHistory = new ArrayList<>();
        Node node = head;
        if (node.getData() == null) {
            throw new IllegalStateException("История просмотров пуста");
        }
        while (true) {
            if (node == null) {
                break;
            }
            tasksHistory.add(node.getData());
            node = node.getNext();
        }
        return tasksHistory;
    }

    public List<Task> getHistory() throws IllegalStateException {
        return getTasks();
    }

    public void add(Task task) {
        if (map.containsKey(task.getId())) {
            remove(task.getId());
            linkList(task);
        } else {
            linkList(task);
        }
    }

    public void remove(int id) {
        removeNode(map.get(id));
    }

    public void removeNode(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();
        if (prev == null) {
            if (next != null) {
                head = next;
                head.setPrev(null);
                return;
            }
            tail = null;
            return;
        }
        if (next == null) {
            prev.setNext(null);
            tail = prev;
            return;
        }
        prev.setNext(next);
        next.setPrev(prev);
    }


}
