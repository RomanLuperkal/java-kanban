package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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

    public Map<Integer, Subtask> getMapSubtasks() {
        return subtasks;
    }

    public void createSubtask(int id, Subtask subtask) {
        subtask.setId(id);
        subtask.setEpicID(getId());
        subtasks.put(id, subtask);
        calculationStartTime();
        calculationDuration();
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subtasks.equals(epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        //Optional<LocalDateTime> date = Optional.ofNullable(startTime);
        String startDateToString = Optional.ofNullable(startTime)
                .map(localDateTime -> localDateTime.format(formatter)).orElse("null");
        String durationToString = Optional.ofNullable(duration)
                .map(duration -> Long.toString(duration.toMinutes())).orElse("null");
        return getId() + "," + TaskType.EPIC + "," + this.name + "," + this.status + "," + this.description
                + "," + startDateToString + "," + durationToString;
    }

    private void calculationStartTime() {
        Comparator<Subtask> comparator = (sub1, sub2) -> {
            LocalDateTime dateTimeSub1 = sub1.getStartTime();
            LocalDateTime dateTimeSub2 = sub2.getStartTime();
            return -dateTimeSub1.compareTo(dateTimeSub2);
        };
        Optional<Subtask> maxStartDate = subtasks.values().stream().filter(sub -> sub.getStartTime() != null)
                .max(comparator);
        maxStartDate.ifPresent(subtask -> this.startTime = subtask.getStartTime());
    }

    private void calculationDuration() {
        Comparator<Subtask> comparator = (sub1, sub2) -> {
            LocalDateTime dateTimeSub1 = sub1.getEndTime();
            LocalDateTime dateTimeSub2 = sub2.getEndTime();
            return dateTimeSub1.compareTo(dateTimeSub2);
        };
        Optional<Subtask> duration = subtasks.values().stream().filter(sub -> sub.getEndTime() != null)
                .max(comparator);
        duration.ifPresent(subtask -> this.duration = Duration.between(this.startTime
                , subtask.getEndTime()));
    }
}