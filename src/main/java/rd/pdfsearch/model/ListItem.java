package rd.pdfsearch.model;

import java.nio.file.Path;

import static rd.pdfsearch.model.ListItem.Type.*;

public class ListItem {
    static public enum Type {FILE, WORD_POSITION, ERROR, OTHER};
    private final Object object;
    private final String text;
    private final Type objectType;

    public ListItem(String text) {
        this(text, null);
    }

    public ListItem (Throwable t) {
        this.text = "Error: " + getAllExceptionCauses(new StringBuilder(), t).toString();
        this.object = t;
        this.objectType = ERROR;
        t.printStackTrace(System.err);
    }

    public ListItem(String text, Object object) {
        this.text = text;
        this.object = object;
        this.objectType = OTHER;
    }

    public ListItem(String text, Path object) {
        this.text = text;
        this.object = object;
        this.objectType = FILE;
    }

    public ListItem(String text, Object object, Type objectType) {
        this.text = text;
        this.object = object;
        this.objectType = objectType;
    }

    @Override
    public String toString() {
        return text;
    }

    public Object getObject() {
        return object;
    }

    public String getText() {
        return text;
    }

    public Type getObjectType() {
        return objectType;
    }

    private StringBuilder getAllExceptionCauses(StringBuilder collector, Throwable throwable) {
        System.out.println("collector " + collector.toString());
        collector.append(throwable.toString());
        collector.append(". ");
        Throwable cause = throwable.getCause();
        if (cause != null) {
            collector.append(cause.toString());
            collector.append(". ");
            return getAllExceptionCauses(collector, cause);
        }
        return collector;
    }
}
