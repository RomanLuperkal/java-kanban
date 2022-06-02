package Tasks;

public class Subtask extends Task{

    public Subtask(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Subtask changeStatus(Status status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
