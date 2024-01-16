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
        this("Error: " + t.toString() + " " + t.getMessage(), null);
        t.printStackTrace(System.err);
    }

    private ListItem(String text, Path path) {
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
}
