package rd.pdfsearch;

import rd.pdfsearch.model.ListItem;
import rd.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public class PanelSouth extends JPanel {
    public final JList<ListItem> outputList;
    public final DefaultListModel<ListItem> outputListModel = new DefaultListModel<>();
    private boolean fixedCellHeightSet = false;

    public PanelSouth(MainWindow mainWindow) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        outputList = new JList<>(outputListModel); //data has type Object[]
        outputList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        outputList.setLayoutOrientation(JList.VERTICAL);
        System.out.println(outputListModel.capacity());


        JScrollPane scrollPane = new JScrollPane(outputList);
        add(scrollPane);
    }

    public void writeOutput(ListItem listItem) {
        synchronized (outputListModel) {
            outputListModel.addElement(listItem);
        }
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
