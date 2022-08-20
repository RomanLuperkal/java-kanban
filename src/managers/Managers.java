package managers;

import java.io.File;
import java.io.IOException;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefault(File file) {
        return new FileBackedTasksManager(file);
    }

    public static TaskManager getDefault(String url) throws InterruptedException, IOException {
        return new HTTPTaskManager(url);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
