package rd.pdfsearch;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame {
    final JPanel panelNorth;
    final JPanel panelCenter;
    final int initWidth;
    final int initHeight;

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

        //Set Font for all child Components
        Map<TextAttribute, Object> fontAtrributes = new HashMap<TextAttribute, Object>();
        fontAtrributes.put(TextAttribute.FAMILY, "Tahoma");
        fontAtrributes.put(TextAttribute.SIZE, 14);
        Font font1 = Font.getFont(fontAtrributes);

        SwingUtil.changeFont(this, font1);

        setVisible(true);
    }

    public int getInitWidth() {
        return initWidth;
    }

    public int getInitHeight() {
        return initHeight;
    }

}
