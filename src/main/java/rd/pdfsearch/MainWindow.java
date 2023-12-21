package rd.pdfsearch;

import rd.pdfsearch.listeners.MainWindowWindowListener;
import rd.util.JsonUtil;
import rd.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame {
    public final PanelNorth panelNorth;
    public final PanelCenter panelCenter;
    public final int initWidth;
    public final int initHeight;
    public final String preferencesFile = "preferences.pdfsearch";
    public Preferences preferences;

    public MainWindow(String title, int initWidth, int initHeight) {
        super(title);
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            exception.printStackTrace(new PrintStream(baos, true, StandardCharsets.UTF_8));
            String output = baos.toString(StandardCharsets.UTF_8);
            System.err.println(output);
            JOptionPane.showMessageDialog(null, "Error: " + output, "PdfSearch", JOptionPane.ERROR_MESSAGE);
        });
        setSize(initWidth, initHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new MainWindowWindowListener(this));
        this.initWidth = initWidth;
        this.initHeight = initHeight;
        setLayout(new BorderLayout());

        //Load preferences
        try {
            preferences = JsonUtil.of(Preferences.class).unmarshallFromFile(preferencesFile);
        } catch (Exception exception) {
            if (exception.getCause().getClass() != FileNotFoundException.class) throw exception;

            preferences = new Preferences();
            preferences.setKeywordsSeparator(",");
        }

        panelNorth = new PanelNorth(this);
        add(panelNorth, BorderLayout.NORTH);

        panelCenter = new PanelCenter(this);
        add(panelCenter);

        //Set Font for all child Components
        Map<TextAttribute, Object> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.FAMILY, "Tahoma");
        fontAttributes.put(TextAttribute.SIZE, 14);
        Font font1 = Font.getFont(fontAttributes);

        SwingUtil.changeFont(this, font1);

        setVisible(true);
    }

    public int getInitWidth() {
        return initWidth;
    }

    public int getInitHeight() {
        return initHeight;
    }

    public void updatePreferences() {
        preferences.setSearchLocation(panelNorth.tfSearchLocation.getText());
        preferences.setKeywords(panelNorth.tfKeywords.getText());
        preferences.setKeywordsSeparator(panelNorth.tfKeywordSeparator.getText());
    }
}
