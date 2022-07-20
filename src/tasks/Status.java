package tasks;

public enum Status {

    NEW, IN_PROGRESS, DONE;

    public static Status parseStatus(String str) {
        switch (str) {
            case "IN_PROGRESS":
                return Status.IN_PROGRESS;
            case "DONE":
                return Status.DONE;
            case "NEW":
                return Status.NEW;
        }
        return null;
    }
}
