package tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {

    Map<Integer, Subtask> subtasks;

    public Epic(String name, String description) {

        super(name, description);
        subtasks = new HashMap<>();
    }

    public Epic(String name, Status status, String description) {
        super(name, status, description);
        subtasks = new HashMap<>();
    }

    public void checkSubtasksStatus() {
        int count = 0;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.status == Status.IN_PROGRESS) {
                this.status = Status.IN_PROGRESS;
            } else if (subtask.status == Status.DONE) {
                this.status = Status.IN_PROGRESS;
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

    public Subtask getSubtask(int id) throws IllegalStateException {
        if (subtasks.get(id) == null) {
            throw new IllegalStateException("Такой подзадачи нет");
        } else {
            return subtasks.get(id);
        }
    }

    public Map<Integer, Subtask> getMapSubtasks() {
        return subtasks;
    }

    public void createSubtask(int id, Subtask subtask) {
        subtask.setId(id);
        subtask.setEpicID(getId());
        subtasks.put(id, subtask);
    }

    @Override
    public String toString() {
        return getId() + "," + TaskType.EPIC + "," + this.name + "," + this.status + "," + this.description;
    }
}