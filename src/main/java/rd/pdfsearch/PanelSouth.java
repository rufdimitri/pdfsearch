package rd.pdfsearch;

import rd.pdfsearch.model.ListItem;
import rd.pdfsearch.model.WordPosition;
import rd.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class PanelSouth extends JPanel {
    public final JList<ListItem> outputList;
    public final DefaultListModel<ListItem> outputListModel = new DefaultListModel<>();
    private boolean fixedCellHeightSet = false;
    private final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    public PanelSouth(MainWindow mainWindow) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        outputList = new JList<>(outputListModel); //data has type Object[]
        outputList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        outputList.setLayoutOrientation(JList.VERTICAL);
        outputList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    ListItem selectedValue = outputList.getSelectedValue();
                    if (selectedValue != null && selectedValue.getObject() instanceof Path) {
                        if (isWindows) {
                            try {
                                Runtime.getRuntime().exec(new String[] {"cmd.exe", "/c", "start", "", selectedValue.getObject().toString()});
                            } catch (IOException ex) {
                                PanelSouth.this.writeOutput(new ListItem(ex));
                            }
                        }
                    }
                }
            }
        });

        outputList.addListSelectionListener(listSelectionEvent -> {
            ListItem selectedItem = outputList.getSelectedValue();
            if (selectedItem.getObjectType() == ListItem.Type.WORD_POSITION && selectedItem.getObject() instanceof WordPosition) {
                WordPosition wordPosition = (WordPosition) selectedItem.getObject();
                mainWindow.panelCenter.taContent.setText(wordPosition.context());
                //TODO select (mark) word in context
                //TODO fix  word only found once per page, even if it appears more than once
            } else {
                mainWindow.panelCenter.taContent.setText("");
            }
        });

        JScrollPane scrollPane = new JScrollPane(outputList);
        add(scrollPane);
    }

    /**
     * Add new element to output list
     *
     */
    public void writeOutput(ListItem listItem) {
        Objects.requireNonNull(listItem);

        synchronized (outputListModel) {
            outputListModel.addElement(listItem);
        }
    }

    public void writeError(Throwable throwable) {
        Objects.requireNonNull(throwable);
        ListItem listItem = new ListItem(throwable);
        writeOutput(listItem);
    }

    public void clearOutput() {
        synchronized (outputListModel) {
            outputListModel.clear();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (!fixedCellHeightSet) {
            outputList.setFixedCellHeight(SwingUtil.getFontHeight(outputList.getGraphics(), outputList.getFont()) + 5);
            fixedCellHeightSet = true;
        }
    }
}
