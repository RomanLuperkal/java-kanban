package tasks;

import exceptions.TaskDateDurationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class SimpleTask extends Task {


    public SimpleTask(String name, String description) {
        super(name, description);
    }

    public SimpleTask(Integer id, String name, Status status, String description) {
        super(name, status, description);
        this.id = id;
    }

    public SimpleTask(Integer id, String name, Status status, String description, String dataTime, Integer durationMin) {
        super(name, status, description);
        this.startTime = LocalDateTime.parse(dataTime, formatter);
        this.duration = Duration.ofMinutes(durationMin);
        this.id = id;
    }

    public SimpleTask(String name, String description, String DataTime, Integer durationMin) {
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
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plus(duration);
        }
        return null;
    }


    @Override
    public String toString() {
        String startDateToString = Optional.ofNullable(startTime)
                .map(localDateTime -> localDateTime.format(formatter)).orElse("null");
        String durationToString = Optional.ofNullable(duration)
                .map(duration -> Long.toString(duration.toMinutes())).orElse("null");
        return getId() + "," + TaskType.TASK + "," + this.name + "," + this.status + "," + this.description + ","
                + startDateToString + "," + durationToString;
    }
}
