package Tasks;

public class Subtask extends Task{

    public Subtask(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Subtask(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
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
