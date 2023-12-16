package rd.pdfsearch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

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
        setLayout(new GridBagLayout());
        this.parent = parent;

        add(new JLabel("Search location:"), constraints(Map.of("gridx", "0", "gridy", "0", "fill", String.valueOf(GridBagConstraints.NONE))));
        edPath = new JEditorPane();
        //edPath.setPreferredSize(new Dimension(parent.getInitWidth(), (int)edPath.getPreferredSize().getHeight()));
        add(edPath, constraints(Map.of("gridx", "1", "gridy", "0", "fill", String.valueOf(GridBagConstraints.HORIZONTAL), "weightx", "0.5")));

        btSelectPath = new JButton("...");
        btSelectPath.addActionListener((ActionEvent e) -> {
            if (PanelNorth.this.fileChooser.showOpenDialog(PanelNorth.this) == JFileChooser.APPROVE_OPTION) {
                PanelNorth.this.edPath.setText(
                        fileChooser.getSelectedFile().getAbsolutePath()
                );
            }
        });
        add(btSelectPath, constraints(Map.of("gridx", "2", "gridy", "0", "fill", String.valueOf(GridBagConstraints.NONE))));

        add(new JLabel("Keywords: "), constraints(Map.of("gridx", "0", "gridy", "1", "fill", String.valueOf(GridBagConstraints.NONE))));

        edKeywords = new JEditorPane();
        add(edKeywords, constraints(Map.of("gridx", "1", "gridy", "1", "fill", String.valueOf(GridBagConstraints.HORIZONTAL), "weightx", "0.5")));

        btSearch = new JButton("Search");
        add(btSearch, constraints(Map.of("gridx", "0", "gridy", "2", "fill", String.valueOf(GridBagConstraints.NONE))));
    }
    static GridBagConstraints constraints(Map<String,String> values) {
        GridBagConstraints constraints = new GridBagConstraints();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (entry.getKey().equals("gridx")) constraints.gridx = Integer.parseInt(entry.getValue());
            if (entry.getKey().equals("gridy")) constraints.gridy = Integer.parseInt(entry.getValue());
            if (entry.getKey().equals("fill")) constraints.fill = Integer.parseInt(entry.getValue());
            if (entry.getKey().equals("weightx")) constraints.weightx = Double.parseDouble(entry.getValue());
            if (entry.getKey().equals("weighty")) constraints.weighty = Double.parseDouble(entry.getValue());
        }

        return constraints;
    }
}
