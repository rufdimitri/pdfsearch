package rd.pdfsearch.model;

public class ListItem {
    private final Object object;
    private final String text;

    public ListItem(String text) {
        this(text, null);
    }

    public ListItem (Throwable t) {
        this.text = "Error: " + getAllExceptionCauses(new StringBuilder(), t).toString();
        this.object = t;
        t.printStackTrace(System.err);
    }

    public ListItem(String text, Object object) {
        this.text = text;
        this.object = object;
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
