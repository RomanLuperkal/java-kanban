package servers.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.Status;

import java.io.IOException;

public class StatusAdapter extends TypeAdapter<Status> {
    @Override
    public void write(JsonWriter jsonWriter, Status status) throws IOException {
        jsonWriter.value(status.toString());
    }

    @Override
    public Status read(JsonReader jsonReader) throws IOException {
        return Status.parseStatus(jsonReader.nextString());
    }
}
