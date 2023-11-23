package rd.pdfsearch;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JPanel panelNorth;
    private JPanel panelCenter;
    private int initWidth;
    private int initHeight;
    public MainWindow(String title, int initWidth, int initHeight) {
        super(title);
        setSize(initWidth, initHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.initWidth = initWidth;
        this.initHeight = initHeight;
        setLayout(new BorderLayout());

        panelNorth = new PanelNorth(this);
        add(panelNorth, BorderLayout.NORTH);

        panelCenter = new PanelCenter(this);
        add(panelCenter);

        setVisible(true);
    }

    public int getInitWidth() {
        return initWidth;
    }

    public int getInitHeight() {
        return initHeight;
    }

}
