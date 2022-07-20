package tasks;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected Integer id;

    public Task(String name, String description) {

        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(Integer id, String name, Status status, String description) {
        this.id = id;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task that = (Task) o;
        return id.equals(that.id) && name.equals(that.name) && description.equals(that.description) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }

    @Override
    public String toString() {
        return getId() + "," + Tasks.TASK + "," + this.name + "," + this.status + "," + this.description;
    }
}
