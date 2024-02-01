package rd.pdfsearch;

import com.ztz.gridbagconstraintsbuilder.GridBagContraintsBuilder;
import rd.pdfsearch.listeners.BtSearchActionListener;
import rd.pdfsearch.model.SearchCriteria;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static rd.pdfsearch.model.SearchCriteria.WordScopeType.DOCUMENT;

public class PanelNorth extends JPanel {
    public final JTextField tfSearchLocation;
    public final JButton btSelectPath;
    public final JTextField tfKeywords;
    public final JTextField tfKeywordSeparator;
    public final JButton btSearch;
    public final MainWindow mainWindow;
    public final JFileChooser fileChooser = new JFileChooser();
    public final JTextField tfRange;
    public final JRadioButton rbRange;
    public final JPanel pnScope;
    public final JLabel lbStatus;
    private Timer keywordsUpdateTimer;
    private final JLabel lbKeywords;

    public PanelNorth(MainWindow mainWindow) {
        setLayout(new GridBagLayout());
        this.mainWindow = mainWindow;
        this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        GridBagContraintsBuilder constraintsBuilder = new GridBagContraintsBuilder();
        constraintsBuilder.x(0).y(0).insets(5);

        //-------------------------------
        //init search location components
        add(new JLabel("Search location:"), constraintsBuilder.fillNone().width(1).build());

        this.tfSearchLocation = new JTextField(mainWindow.preferences.getSearchLocation());
        add(this.tfSearchLocation, constraintsBuilder.newCol().fillHorizontal(1).width(2).build());

        this.btSelectPath = new JButton("...");
        this.btSelectPath.addActionListener((ActionEvent e) -> {
            if (PanelNorth.this.fileChooser.showOpenDialog(PanelNorth.this) == JFileChooser.APPROVE_OPTION) {
                PanelNorth.this.tfSearchLocation.setText(
                        fileChooser.getSelectedFile().getAbsolutePath()
                );
            }
        });

        add(this.btSelectPath, constraintsBuilder.newCol().fillHorizontal(0.1).width(1).build());

        //-------------------------------
        //init keywords row components
        add(new JLabel("Keywords: "), constraintsBuilder.newRow().fillNone().width(1).build());

        KeyAdapter keyAdapterKeywords = new KeyAdapter() {
            TimerTask lastTask;

            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println(System.currentTimeMillis());
                if (keywordsUpdateTimer == null) keywordsUpdateTimer = new Timer(true);
                if (lastTask != null) lastTask.cancel();

                lastTask = new TimerTask() {
                    @Override
                    public void run() {
                        List<String> keywords = mainWindow.getKeywords();
                        lbKeywords.setText(String.join("|", keywords));
                    }
                };
                keywordsUpdateTimer.schedule(lastTask, 500);
            }
        };
        keyAdapterKeywords.keyTyped(null);

        this.lbKeywords = new JLabel("");
        this.tfKeywords = new JTextField(String.join(mainWindow.preferences.getKeywordsSeparator(), mainWindow.preferences.getSearchCriteria().getKeywords()));
        add(this.tfKeywords, constraintsBuilder.newCol().fillHorizontal(1).width(1).build());
        this.tfKeywords.addKeyListener(keyAdapterKeywords);

        add(new JLabel("Keywords separator: "), constraintsBuilder.newCol().fillNone().width(1).build());
        this.tfKeywordSeparator = new JTextField(mainWindow.preferences.getKeywordsSeparator());
        tfKeywordSeparator.addKeyListener(keyAdapterKeywords);

        add(this.tfKeywordSeparator, constraintsBuilder.newCol().fillHorizontal(0.1).width(1).build());
        add(lbKeywords, constraintsBuilder.newRow().newCol().fillNone().width(1).build());
        //-------------------------------
        //init scope components
        add(new JLabel("Scope: "), constraintsBuilder.newRow().fillNone().width(1).build());

        this.pnScope = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(this.pnScope, constraintsBuilder.newCol().fillHorizontal(1).width(1).build());
        ButtonGroup grScope = new ButtonGroup();
        //add radiobutton Document
        JRadioButton rbDocument = new JRadioButton("Document", mainWindow.preferences.getSearchCriteria().getWordScopeType() == DOCUMENT);
        this.pnScope.add(rbDocument);
        grScope.add(rbDocument);
        //add radiobutton Range
        this.rbRange = new JRadioButton("Range", !rbDocument.isSelected());
        this.pnScope.add(this.rbRange);
        grScope.add(this.rbRange);
        //add text field "Range"
        this.tfRange = new JTextField(Integer.toString(mainWindow.preferences.getSearchCriteria().getRangeSize()));
        this.tfRange.setColumns(5);
        this.pnScope.add(this.tfRange);
        //add EventListeners for radiobuttons
        Iterator<AbstractButton> rbGroupIterator = grScope.getElements().asIterator();
        while (rbGroupIterator.hasNext()) {
            AbstractButton radioButton = rbGroupIterator.next();
            radioButton.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    this.tfRange.setVisible(this.rbRange.isSelected());
                    this.pnScope.updateUI();
                });
            });
        }
        //add help label
        JLabel lbScopeHelp = new JLabel("(?)");
        lbScopeHelp.setToolTipText("Define scope, where the keywords should be: 1) in the same Document or 2) within a Range of n characters between words");
        this.pnScope.add(lbScopeHelp);

        //-------------------------------
        //init search button
        this.btSearch = new JButton("Search");
        this.btSearch.addActionListener(new BtSearchActionListener(mainWindow));
        add(this.btSearch, constraintsBuilder.newCol().newCol().fillHorizontal(0.1).width(1).build());
        //-------------------------------
        //init status bar
        this.lbStatus = new JLabel(" ");
        add(this.lbStatus, constraintsBuilder.newRow().width(0).build());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        this.tfRange.setVisible(this.rbRange.isSelected());
        this.pnScope.updateUI();

    }

}
