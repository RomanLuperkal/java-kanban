package tasks;

public class Subtask extends Task {
    private Integer epicID;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, Status status, String description, Integer epicID) {
        super(name, status, description);
        this.epicID = epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    public Integer getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return getId() + "," + TaskType.SUBTASK + "," + this.name + "," + this.status + "," + this.description + ","
                + epicID;
    }
}
