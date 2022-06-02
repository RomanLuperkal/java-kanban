package Tasks;

public class SimpleTask extends Task{
    public SimpleTask(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public SimpleTask changeStatus(Status status) {
        this.status = status;
        return this;
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
