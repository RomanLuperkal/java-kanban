package Tasks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpicTask extends Task{

    Map<Integer, Subtask> subtasks;

    public EpicTask(String name, String description) {
        subtasks = new HashMap<>();
        this.name = name;
        this.description = description;
    }

    public void checkSubtasksStatus () {
        int count = 0;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.status == Status.IN_PROGRESS) {
                this.status = Status.IN_PROGRESS;
            } else if (subtask.status == Status.DONE) {
                count++;
            }
            if (count == subtasks.size()) {
                this.status = Status.DONE;
            }
        }
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteSubtasks() {
        subtasks.clear();
    }

    public Subtask getSubtask(int id) throws NullPointerException{
        if (subtasks.get(id) == null) {
            throw new NullPointerException("Такой подзадачи нет");
        } else {
            return subtasks.get(id);
        }
    }

    public Map<Integer, Subtask> getMapSubtasks() {
        return subtasks;
    }

    public void createSubtask(int id, Subtask subtask) {
        subtask.setId(id);
        subtasks.put(id, subtask);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "listSubtasks=" + subtasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}