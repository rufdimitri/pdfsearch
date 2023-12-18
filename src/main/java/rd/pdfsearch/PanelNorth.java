package rd.pdfsearch;

import com.ztz.gridbagconstraintsbuilder.GridBagContraintsBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

public class PanelNorth extends JPanel {
    final JTextField tfSearchLocation;
    final JButton btSelectPath;
    final JTextField tfKeywords;
    final JTextField tfKeywordSeparator;
    final JButton btSearch;
    final MainWindow parent;

    final JFileChooser fileChooser = new JFileChooser();

    {
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    public PanelNorth(MainWindow parent) {
        setLayout(new GridBagLayout());
        this.parent = parent;


        GridBagContraintsBuilder constraintsBuilder = new GridBagContraintsBuilder();
        constraintsBuilder.x(0).y(0).insets(5);

        add(new JLabel("Search location:"), constraintsBuilder.fillNone().width(1).build());

        tfSearchLocation = new JTextField(parent.preferences.getSearchLocation());
        add(tfSearchLocation, constraintsBuilder.newCol().fillHorizontal(1).width(2).build());

        btSelectPath = new JButton("...");
        btSelectPath.addActionListener((ActionEvent e) -> {
            if (PanelNorth.this.fileChooser.showOpenDialog(PanelNorth.this) == JFileChooser.APPROVE_OPTION) {
                PanelNorth.this.tfSearchLocation.setText(
                        fileChooser.getSelectedFile().getAbsolutePath()
                );
            }
        });

        add(btSelectPath, constraintsBuilder.newCol().fillHorizontal(0.1).width(1).build());


        add(new JLabel("Keywords: "), constraintsBuilder.newRow().fillNone().width(1).build());

        tfKeywords = new JTextField(parent.preferences.getKeywords());
        add(tfKeywords, constraintsBuilder.newCol().fillHorizontal(1).width(1).build());

        add(new JLabel("Keywords separator: "), constraintsBuilder.newCol().fillNone().width(1).build());
        tfKeywordSeparator = new JTextField(parent.preferences.getKeywordsSeparator());
        add(tfKeywordSeparator, constraintsBuilder.newCol().fillHorizontal(0.1).width(1).build());

        btSearch = new JButton("Search");
        add(btSearch, constraintsBuilder.newRow().fillNone().width(1).build());
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
