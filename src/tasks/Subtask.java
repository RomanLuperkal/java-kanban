package tasks;

public class Subtask extends Task {
    private Integer epicID;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(Integer id, String name, Status status, String description, Integer epicID) {
        super(id, name, status, description);
        this.epicID = epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return getId() + "," + Tasks.SUBTASK + "," + this.name + "," + this.status + "," + this.description + ","
                + epicID;
    }
}
