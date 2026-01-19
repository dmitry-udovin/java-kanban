package tasktracker.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    private static final DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void write(JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
        jsonWriter.value(localDate.format(dtf1));
    }

    @Override
    public LocalDate read(JsonReader jsonReader) throws IOException {
        JsonToken peek = jsonReader.peek();
        if ("NULL".equals(peek.name())) {
            return null;
        }

        String value = jsonReader.nextString();

        try {
            return LocalDate.parse(value, dtf1);
        } catch (Exception exp1) {
            try {
                return LocalDate.parse(value, dtf2);
            } catch (Exception exp2) {
                try {
                    return LocalDate.parse(value, dtf3);
                } catch (Exception exp3) {
                    return null;
                }
            }
        }
    }
}