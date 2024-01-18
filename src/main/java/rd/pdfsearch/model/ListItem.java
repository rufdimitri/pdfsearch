package rd.pdfsearch.model;

import java.nio.file.Path;

public class ListItem {
    private final Path path;
    private final String text;

    public ListItem(Path path) {
        this(path.getFileName().toString() + " : " + path.getParent().toAbsolutePath().toString(), path);
    }

    public ListItem(String text) {
        this(text, null);
    }

    public ListItem (Throwable t) {
        this.text = "Error: " + getAllExceptionCauses(new StringBuilder(), t).toString();
        this.path = null;
        t.printStackTrace(System.err);
    }

    public ListItem(String text, Path path) {
        this.text = text;
        this.path = path;
    }

    @Override
    public String toString() {
        return text;
    }

    public Path getPath() {
        return path;
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
