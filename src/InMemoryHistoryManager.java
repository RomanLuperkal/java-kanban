import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    public List<Task> getHistory() throws NullPointerException{
        if (history.isEmpty()) {
            throw new NullPointerException("История просмотров пуста");
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
            history.remove(0);
            history.add(task);
        }
    }
}
