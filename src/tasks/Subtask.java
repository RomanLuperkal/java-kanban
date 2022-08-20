package tasks;

import exceptions.TaskDateDurationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Subtask extends Task {
    private Integer epicID;

    public Subtask(String name, String description) {
        super(name, description);
        type = TaskType.SUBTASK.toString();
    }

    public Subtask(Integer id, String name, Status status, String description, Integer epicID) {
        super(name, status, description);
        this.epicID = epicID;
        this.id = id;
        type = TaskType.SUBTASK.toString();
    }

    public Subtask(Integer id, String name, Status status, String description, String DataTime, Integer durationMin, Integer epicID) {
        super(name, status, description);
        this.startTime = LocalDateTime.parse(DataTime, formatter);
        this.duration = Duration.ofMinutes(durationMin);
        this.epicID = epicID;
        this.id = id;
        type = TaskType.SUBTASK.toString();
    }

    public Subtask(String name, String description, String DataTime, Integer durationMin) {
        super(name, description);
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime DateTask = LocalDateTime.parse(DataTime, formatter);
        if (timeNow.isAfter(DateTask)) {
            throw new TaskDateDurationException("Время начала не может быть раньше нынешнего момента времени");
        }
        if (durationMin <= 0) {
            throw new TaskDateDurationException("Продолжительность не может быть отрицательной либо равна 0");
        }
        this.startTime = DateTask;
        this.duration = Duration.ofMinutes(durationMin);
        this.status = Status.NEW;
        type = TaskType.SUBTASK.toString();
    }

    public LocalDateTime getEndTime() {
        if (this.startTime != null) {
            return startTime.plus(duration);
        }
        return null;
    }


    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    public Integer getEpicID() {
        return epicID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.deepEquals(epicID, subtask.epicID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicID);
    }

    @Override
    public String toString() {
        String startDateToString = Optional.ofNullable(startTime)
                .map(localDateTime -> localDateTime.format(formatter)).orElse("null");
        String durationToString = Optional.ofNullable(duration)
                .map(duration -> Long.toString(duration.toMinutes())).orElse("null");
        return getId() + "," + TaskType.SUBTASK + "," + this.name + "," + this.status + "," + this.description + ","
                + this.epicID + "," + startDateToString + "," + durationToString;
    }
}
