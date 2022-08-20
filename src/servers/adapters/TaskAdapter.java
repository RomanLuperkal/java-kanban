package servers.adapters;

import com.google.gson.*;
import tasks.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TaskAdapter implements JsonDeserializer<Task> {
    private String taskTypeElementName;
    private Gson gson;
    private Map<String, Class<? extends Task>> taskTypeRegistry;

    public TaskAdapter(String taskTypeElementName) {
        this.taskTypeElementName = taskTypeElementName;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(DateTimeFormatter.class, new DateTimeFormatterAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .serializeNulls()
                .create();
        this.taskTypeRegistry = new HashMap<>();
    }

    public void registerBarnType(String taskTypeName, Class<? extends Task> animalType) {
        taskTypeRegistry.put(taskTypeName, animalType);
    }

    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject taskObject = json.getAsJsonObject();
        JsonElement taskTypeElement = taskObject.get(taskTypeElementName);

        Class<? extends Task> animalType = taskTypeRegistry.get(taskTypeElement.getAsString());
        return gson.fromJson(taskObject, animalType);
    }
}
