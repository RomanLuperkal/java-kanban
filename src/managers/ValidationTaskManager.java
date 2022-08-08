package managers;

import tasks.Task;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ValidationTaskManager {
    private final Map<LocalDateTime, Boolean> chart = new HashMap<>();

    public boolean validation(Task task) {
        LocalDateTime taskTime = task.getStartTime();
        if (taskTime == null) {
            return false;
        }
        if (chart.get(taskTime) == null) {
            chart.put(taskTime, true);
            return false;
        }
        return chart.get(taskTime);
    }

    public void deleteTaskDateTime(LocalDateTime taskDateTime) {
        if (taskDateTime != null) {
            chart.remove(taskDateTime);
        }
    }
}
