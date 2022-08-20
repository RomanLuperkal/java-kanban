package managers;

import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ValidationTaskManager {
    private final Map<LocalDateTime, Boolean> chart = new HashMap<>();

    public boolean validation(Task task) {
        LocalDateTime taskTime = task.getStartTime();
        Duration taskDuration = task.getDuration();
        if (taskTime == null) {
            return false;
        }
        for (int i = 0; i < taskDuration.toMinutes(); i++) {
            LocalDateTime checkTime = taskTime.plusMinutes(i);
            if (!(chart.get(checkTime) == null)) {
                return chart.get(checkTime);
            }
        }
        for (int i = 0; i < taskDuration.toMinutes(); i++) {
            chart.put(taskTime.plusMinutes(i), true);
        }
        return false;
    }

    public void deleteTaskDateTime(Task task) {
        LocalDateTime taskTime = task.getStartTime();
        Duration taskDuration = task.getDuration();
        if (taskTime != null) {
            for (int i = 0; i < taskDuration.toMinutes(); i++) {
                chart.remove(taskTime.plusMinutes(i));
            }
        }
    }

    public boolean validationUpdate(Task newTask, Task oldTask) {
        deleteTaskDateTime(oldTask);
        if (!validation(newTask)) {
            return false;
        } else {
            deleteTaskDateTime(newTask);
            validation(oldTask);
        }
        return true;
    }
}
