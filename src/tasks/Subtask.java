package tasks;

public class Subtask extends Task {

    public Subtask(String name, String description) {
        super(name, description);
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
