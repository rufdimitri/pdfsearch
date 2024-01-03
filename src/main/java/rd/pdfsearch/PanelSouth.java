package rd.pdfsearch;

import rd.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class PanelSouth extends JPanel {
    public final JList<String> outputList;
    public final DefaultListModel<String> outputListModel = new DefaultListModel<>();
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

    public void outputPrintln(String line) {
        synchronized (outputListModel) {
            outputListModel.addElement(line);
        }
    }

    public void outputError(Throwable t) {
        String line = "Error: " + t.toString() + " " + t.getMessage();
        synchronized (outputListModel) {
            outputListModel.addElement(line);
        }
        t.printStackTrace(System.err);
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
