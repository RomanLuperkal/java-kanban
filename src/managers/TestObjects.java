package managers;

import tasks.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface TestObjects {
    LocalDateTime localDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    SimpleTask task = new SimpleTask("Task", "Description task"
            , localDateTime.plusMinutes(10).format(formatter), 60);
    Epic epicTask1 = new Epic("Epic 1", "Description epic task");
    Epic epicTask2 = new Epic("Epic 2", "Description epic task");
    Subtask subtask1ForEpicTask1 = new Subtask("sub1 for ep1", "Description sub1 for ep 1"
            , localDateTime.plusMinutes(100).format(formatter), 60);
    Subtask subtask2ForEpicTask1 = new Subtask("sub2 for ep1", "Description sub2 for ep 1"
            , localDateTime.plusHours(5).format(formatter), 60);
    Subtask subtask3ForEpicTask1 = new Subtask("sub3 for ep2", "Description sub3 for ep 2");
}
