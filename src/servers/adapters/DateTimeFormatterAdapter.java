package servers.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterAdapter extends TypeAdapter<DateTimeFormatter> {
    private static String formatter = "dd.MM.yyyy HH:mm";

    @Override
    public void write(final JsonWriter jsonWriter, final DateTimeFormatter dateTimeFormatter) throws IOException {
        jsonWriter.value(formatter);
    }

    @Override
    public DateTimeFormatter read(final JsonReader jsonReader) throws IOException {
        return DateTimeFormatter.ofPattern(jsonReader.nextString());
    }
}

