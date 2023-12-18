package rd.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class JsonUtil <T> {
    public Class<T> objectClass;

    private JsonUtil(Class<T> objectClass) {
        this.objectClass = objectClass;
    }

    public static <T> JsonUtil<T> of (Class<T> objectClass) {
        return new JsonUtil<>(objectClass);
    }

    public T unmarshallFromFile(String fileName) {
        try (Reader reader = new FileReader(fileName, StandardCharsets.UTF_8)) {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();

            return gson.fromJson(reader, objectClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void marshallToFile(String fileName, T object) {
        try (Writer writer = new FileWriter(fileName, StandardCharsets.UTF_8)) {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();

            gson.toJson(object, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
