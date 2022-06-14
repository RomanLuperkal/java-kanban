package managers;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new LinkedList<>();
    }

    public List<Task> getHistory() throws IllegalStateException {
        if (history.isEmpty()) {
            throw new IllegalStateException("История просмотров пуста");
        } else {
            return history;
        }
    }

    public void add(Task task) {
        addToHistory(task);
    }

    private void addToHistory(Task task) {
        if (history.size() < 10) {
            history.add(task);
        } else {
            ((LinkedList<Task>) history).removeFirst();
            history.add(task);
        }
    }
}
