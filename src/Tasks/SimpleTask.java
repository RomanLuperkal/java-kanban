package Tasks;

public class SimpleTask extends Task{
    public SimpleTask(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public SimpleTask(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return "SimpleTask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
