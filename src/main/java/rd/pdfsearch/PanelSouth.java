package rd.pdfsearch;

import rd.pdfsearch.model.ListItem;
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
                    if (selectedValue != null && selectedValue.getPath() != null) {
                        if (isWindows) {
                            try {
                                Runtime.getRuntime().exec(new String[] {"cmd.exe", "/c", "start", "", selectedValue.getPath().toString()});
                            } catch (IOException ex) {
                                PanelSouth.this.writeOutput(ex);
                            }
                        }
                    }
                }
            }
        });


        JScrollPane scrollPane = new JScrollPane(outputList);
        add(scrollPane);
    }

    /**
     * Add new element to output list
     * @param object (NotNull) object of one of types: Throwable, String, Path, ListItem
     *
     */
    public void writeOutput(Object object) {
        Objects.requireNonNull(object);
        ListItem element;
        if (object instanceof Throwable) {
            element = new ListItem((Throwable) object);
        } else if (object instanceof String) {
            element = new ListItem((String) object);
        } else if (object instanceof Path) {
            element = new ListItem((Path) object);
        } else if (object instanceof ListItem) {
            element = (ListItem) object;
        } else {
            element = new ListItem(new RuntimeException("Unknown output type: " + object.getClass().getName()));
        }
        synchronized (outputListModel) {
            outputListModel.addElement(element);
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
