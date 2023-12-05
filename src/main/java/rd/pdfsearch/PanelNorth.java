package rd.pdfsearch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PanelNorth extends JPanel {
    JEditorPane edPath;
    JButton btSelectPath;
    JEditorPane edKeywords;
    JButton btSearch;
    MainWindow parent;
    JPanel pnDirectory = new JPanel();
    final JFileChooser fileChooser = new JFileChooser();
    {
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    public PanelNorth(MainWindow parent) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.parent = parent;

        initPnDirectory();
        add(pnDirectory);

        add(new JLabel("Keywords: "));

        edKeywords = new JEditorPane();
        add(edKeywords);

        btSearch = new JButton("Search");
        add(btSearch);
    }

    private void initPnDirectory() {
        // TODO
        // Using GridBagLayout
        // https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
        GridBagLayout gbl = new GridBagLayout();
        pnDirectory.setLayout(gbl);
        GridBagConstraints constraints = gbl.getConstraints(pnDirectory);
        gbl.setConstraints(pnDirectory, new GridBagConstraints());
        pnDirectory.add(new JLabel("Search location:"));
        edPath = new JEditorPane();
        //edPath.setPreferredSize(new Dimension(parent.getInitWidth(), (int)edPath.getPreferredSize().getHeight()));
        pnDirectory.add(edPath);

        btSelectPath = new JButton("...");
        btSelectPath.addActionListener((ActionEvent e) -> {
            if (PanelNorth.this.fileChooser.showOpenDialog(PanelNorth.this) == JFileChooser.APPROVE_OPTION) {
                PanelNorth.this.edPath.setText(
                        fileChooser.getSelectedFile().getAbsolutePath()
                );
            }
        });
        pnDirectory.add(btSelectPath);
    }
}
