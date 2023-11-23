package rd.pdfsearch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelNorth extends JPanel {
    JEditorPane edPath;
    JButton btSelectPath;
    JEditorPane edKeywords;
    JButton btSearch;
    MainWindow parent;
    final JFileChooser fileChooser = new JFileChooser();
    {
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    public PanelNorth(MainWindow parent) {
        setLayout(new FlowLayout(FlowLayout.LEADING));
        this.parent = parent;

        add(new JLabel("Search location:"));
        edPath = new JEditorPane();
        edPath.setPreferredSize(new Dimension(parent.getInitWidth()/2, (int)edPath.getPreferredSize().getHeight()));
        add(edPath, BorderLayout.WEST);

        btSelectPath = new JButton("...");
        btSelectPath.addActionListener((ActionEvent e) -> {
            if (PanelNorth.this.fileChooser.showOpenDialog(PanelNorth.this) == JFileChooser.APPROVE_OPTION) {
                PanelNorth.this.edPath.setText(
                        fileChooser.getSelectedFile().getAbsolutePath()
                );
            }
        });
        add(btSelectPath);

        add(new JLabel("Keywords: "));

        edKeywords = new JEditorPane();
        add(edKeywords);

        btSearch = new JButton("Search");
        add(btSearch);
    }
}
