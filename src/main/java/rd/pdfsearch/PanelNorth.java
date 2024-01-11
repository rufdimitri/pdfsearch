package rd.pdfsearch;

import com.ztz.gridbagconstraintsbuilder.GridBagContraintsBuilder;
import rd.pdfsearch.listeners.BtSearchActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PanelNorth extends JPanel {
    public final JTextField tfSearchLocation;
    public final JButton btSelectPath;
    public final JTextField tfKeywords;
    public final JTextField tfKeywordSeparator;
    public final JButton btSearch;
    public final MainWindow mainWindow;
    public final JFileChooser fileChooser = new JFileChooser();

    public PanelNorth(MainWindow mainWindow) {
        setLayout(new GridBagLayout());
        this.mainWindow = mainWindow;
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        GridBagContraintsBuilder constraintsBuilder = new GridBagContraintsBuilder();
        constraintsBuilder.x(0).y(0).insets(5);

        add(new JLabel("Search location:"), constraintsBuilder.fillNone().width(1).build());

        tfSearchLocation = new JTextField(mainWindow.preferences.getSearchLocation());
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

        tfKeywords = new JTextField(mainWindow.preferences.getKeywords());
        add(tfKeywords, constraintsBuilder.newCol().fillHorizontal(1).width(1).build());

        add(new JLabel("Keywords separator: "), constraintsBuilder.newCol().fillNone().width(1).build());
        tfKeywordSeparator = new JTextField(mainWindow.preferences.getKeywordsSeparator());
        add(tfKeywordSeparator, constraintsBuilder.newCol().fillHorizontal(0.1).width(1).build());

        add(new JLabel("Scope: "), constraintsBuilder.newRow().fillNone().width(1).build());

        JPanel pnScope = new JPanel(new FlowLayout());
        add(pnScope, constraintsBuilder.newCol().fillNone().width(1).build());
        ButtonGroup grScope = new ButtonGroup();
        //add radiobutton Document
        JRadioButton rbDocument = new JRadioButton("Document", true);
        pnScope.add(rbDocument);
        grScope.add(rbDocument);
        //add radiobutton Range
        JRadioButton rbRange = new JRadioButton("Range");
        pnScope.add(rbRange);
        grScope.add(rbRange);

        btSearch = new JButton("Search");
        btSearch.addActionListener(new BtSearchActionListener(mainWindow));
        add(btSearch, constraintsBuilder.newRow().fillNone().width(1).build());
    }

}
