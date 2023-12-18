package rd.pdfsearch;

import javax.swing.*;
import java.awt.*;

public class PanelCenter extends JPanel {
    final MainWindow parent;
    final JTextArea taContent;

    public PanelCenter(MainWindow parent) {
        setLayout(new BorderLayout());
        this.parent = parent;
        //setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        taContent = new JTextArea();
        taContent.setMargin(new Insets(5,5,5,5));
        add(taContent);
    }
}
