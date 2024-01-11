package rd.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Util-Class for JSON marshall and unmarshall using Gson library
 * version: 240110
 */
public class JsonUtil {
    private JsonUtil() {
    }

    /** Creates object from JSON-string, stored in a file
     * @param fileName where the JSON-string, representing the object to create, is saved     *
     * @param typeToken type of the object
     *   can be created using <code>{@literal new TypeToken<PUT-HERE-CLASS-WITH-GENERIC-PARAMETERS>() {}}</code>
     *   <br/><br/>Example: <code>{@literal new TypeToken<Map<Integer, String>>() {}}</code>
     * @return unmarshalled object from json of same type as typeObject
     */
    public static <T> T unmarshallFromFile(String fileName, TypeToken<T> typeToken) {
        try (Reader reader = new FileReader(fileName, StandardCharsets.UTF_8)) {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            return gson.fromJson(reader, typeToken.getType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** same as {@link #unmarshallFromFile(String, TypeToken)} method, returning defaultValue if Exception happens
     * @param fileName where the JSON-string, representing the object to create, is saved     *
     * @param typeToken type of the object
     *   can be created using <code>{@literal new TypeToken<PUT-HERE-CLASS-WITH-GENERIC-PARAMETERS>() {}}</code>
     *   <br/><br/>Example: <code>{@literal new TypeToken<Map<Integer, String>>() {}}</code>
     * @param defaultValue value returned if Exception happens
     * @return unmarshalled object from json of same type as typeObject
     */
    public static <T> T unmarshallFromFileOrDefault(String fileName, TypeToken<T> typeToken, T defaultValue) {
        try {
            T object = unmarshallFromFile(fileName, typeToken);
            //TODO uncomment following line (commented for debug purpose for TODO with exception hanging at SearchRequest constructor)
            //if (object == null) return defaultValue;
            return object;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /** Creates JSON-string from object and writes it to file
     * @param fileName path to file, where to write JSON-string
     * @param object object that should be marshalled to JSON-string
     */
    public static <T> void marshallToFile(String fileName, T object) {
        try (Writer writer = new FileWriter(fileName, StandardCharsets.UTF_8)) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            gson.toJson(object, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates JSON-string from object
     * @param object object that should be marshalled to JSON-string
     * @return JSON-string representing the object
     */
    public static <T> String marshallToJson(T object) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            return gson.toJson(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates object from JSON-string
     * @param json JSON-string representing the object to create
     * @param typeToken type of the object
     *   can be created using <code>{@literal new TypeToken<PUT-HERE-CLASS-WITH-GENERIC-PARAMETERS>() {}}</code>
     *   <br/><br/>Example: <code>{@literal new TypeToken<Map<Integer, String>>() {}}</code>
     * @return unmarshalled object from json of same type as typeObject
     */
    public static <T>T unmarshallFromJson(String json, TypeToken<T> typeToken) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            return gson.fromJson(json, typeToken.getType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
