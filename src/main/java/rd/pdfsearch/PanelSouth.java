package rd.pdfsearch;

import javax.swing.*;

public class PanelSouth extends JPanel {
    public final JList<String> outputList;
    public final DefaultListModel<String> outputListModel = new DefaultListModel<>();

    public PanelSouth(MainWindow mainWindow) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        outputList = new JList<>(outputListModel); //data has type Object[]
        outputList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        outputList.setLayoutOrientation(JList.VERTICAL);
        outputList.setVisibleRowCount(15);
        System.out.println(outputListModel.capacity());

        //add empty element and select it. somehow helps it to fix error where list is not empty but is displayed as empty
        outputListModel.addElement(" ");
        outputList.setSelectedIndex(0);

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
    }

    public void clearOutput() {
        synchronized (outputListModel) {
            outputListModel.clear();
        }
    }


}
