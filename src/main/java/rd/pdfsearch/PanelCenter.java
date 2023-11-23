package rd.pdfsearch;

import javax.swing.*;
import java.awt.*;

public class PanelCenter extends JPanel {
    MainWindow parent;
    JEditorPane content;

    public PanelCenter(MainWindow parent) {
        setLayout(new BorderLayout());
        this.parent = parent;
        //setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        content = new JEditorPane();
        content.setMargin(new Insets(5,5,5,5));
        add(content);
    }
}
