package rd.pdfsearch;

import javax.swing.*;
import java.util.Vector;

public class PanelSouth extends JPanel {
    public final JList<String> outputList;
    public final Vector<String> outputListData = new Vector<String>();

    public PanelSouth(MainWindow mainWindow) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
//        setPreferredSize(new Dimension(200, 200));

        outputList = new JList<String>(); //data has type Object[]
        outputList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        outputList.setLayoutOrientation(JList.VERTICAL);
        outputList.setVisibleRowCount(5);

        JScrollPane scrollPane = new JScrollPane(outputList);

        add(scrollPane);
    }

    public void outputPrintln(String line) {
        outputListData.add(line);
        outputList.setListData(outputListData);
    }

    public void outputError(Throwable t) {
        String line = "Error: " + t.toString() + " " + t.getMessage();
        outputListData.add(line);
        outputList.setListData(outputListData);
    }

    public void clearOutput() {
        outputListData.clear();
        outputList.setListData(outputListData);
    }


}
