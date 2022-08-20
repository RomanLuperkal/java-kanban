package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class Task implements Comparable<Task> {
    protected String type = "Task";
    protected String name;
    protected String description;
    protected Status status;
    protected Integer id;
    protected LocalDateTime startTime;
    protected Duration duration;
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String name, Status status, String description) {
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public int compareTo(Task task) {
        LocalDateTime thisDate = this.getStartTime();
        LocalDateTime dateTask = task.getStartTime();
        if (thisDate == null && dateTask == null) {
            return 1;
        }
        if (dateTask == null) {
            return -1;
        }
        if (thisDate == null) {
            return 1;
        }
        return thisDate.compareTo(dateTask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;

        return name.equals(task.name) && description.equals(task.description) && status == task.status
                && Objects.deepEquals(id, task.id) && Objects.deepEquals(startTime, task.startTime)
                && Objects.deepEquals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id, startTime, duration, formatter);
    }
}
