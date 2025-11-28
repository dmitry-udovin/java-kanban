package tasktracker.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class OptionalTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!(type.getType() instanceof ParameterizedType)) return null;

        ParameterizedType pType = (ParameterizedType) type.getType();
        if (pType.getRawType() != Optional.class) return null;

        Type innerType = pType.getActualTypeArguments()[0];
        TypeAdapter<?> innerAdapter = gson.getAdapter(TypeToken.get(innerType));

        return (TypeAdapter<T>) new OptionalAdapter<>(innerAdapter);
    }

    private static final class OptionalAdapter<E> extends TypeAdapter<Optional<E>> {
        private final TypeAdapter<E> inner;

        OptionalAdapter(TypeAdapter<E> inner) {
            this.inner = inner;
        }

        @Override
        public void write(JsonWriter out, Optional<E> value) throws IOException {
            if (value == null || value.isEmpty()) {
                out.nullValue();
            } else {
                inner.write(out, value.get());
            }
        }

        @Override
        public Optional<E> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return Optional.empty();
            }
            E val = inner.read(in);
            return Optional.ofNullable(val);
        }
    }
}
