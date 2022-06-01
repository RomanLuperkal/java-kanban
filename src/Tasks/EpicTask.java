package Tasks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EpicTask extends Task{

    Map<Integer, Subtask> listSubtasks;

    public EpicTask(String name, String description) {
        listSubtasks = new HashMap<>();
        this.name = name;
        this.description = description;
    }

    public void checkSubtasksStatus () {
        int count = 0;

        for (Subtask subtask : listSubtasks.values()) {
            if (subtask.status == Status.IN_PROGRESS) {
                this.status = Status.IN_PROGRESS;
            } else if (subtask.status == Status.DONE) {
                count++;
            }
            if (count == listSubtasks.size()) {
                this.status = Status.DONE;
            }
        }
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(listSubtasks.values());
    }

    public void deleteSubtasks() {
        listSubtasks.clear();
    }

    public Subtask getSubtask(int id) throws NullPointerException{
        if (listSubtasks.get(id) == null) {
            throw new NullPointerException("Такой подзадачи нет");
        } else {
            return listSubtasks.get(id);
        }
    }

    public Map<Integer, Subtask> getMapSubtasks() {
        return listSubtasks;
    }

    public void createSubtask(int id, Subtask subtask) {
        subtask.setId(id);
        listSubtasks.put(id, subtask);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "listSubtasks=" + listSubtasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
