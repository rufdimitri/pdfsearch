package rd.pdfsearch;

import com.google.gson.reflect.TypeToken;
import rd.pdfsearch.listeners.MainWindowListener;
import rd.pdfsearch.model.CachedPdfFile;
import rd.pdfsearch.model.ListItem;
import rd.pdfsearch.model.Preferences;
import rd.util.JsonUtil;
import rd.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class MainWindow extends JFrame {
    public final String PREFERENCES_FILE = "preferences.pdfsearch";
    public final String CACHED_PDF_FILENAME = "cachedPdf.pdfsearch";
    public final PanelNorth panelNorth;
    public final PanelCenter panelCenter;
    public final PanelSouth panelSouth;
    public final int initWidth;
    public final int initHeight;

    public Preferences preferences;
    public Future<?> fileSearchFuture;
    public final BlockingQueue<ListItem> outputQueue = new LinkedBlockingQueue<>(10);
    public final BlockingQueue<ListItem> errorQueue = new LinkedBlockingQueue<>(10);
    public Map<Integer, List<CachedPdfFile>> cachedFilesPerFileIdentityHashCode = new HashMap<>();


    public MainWindow(String title, int initWidth, int initHeight) {
        super(title);

        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getExceptionHandler(this));
        setSize(initWidth, initHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new MainWindowListener(this));
        this.initWidth = initWidth;
        this.initHeight = initHeight;
        setLayout(new BorderLayout());

        //Load preferences
        try {
            preferences = JsonUtil.unmarshallFromFile(PREFERENCES_FILE, new TypeToken<>(){});
        } catch (Exception exception) {
            if (exception.getCause().getClass() != FileNotFoundException.class) throw exception;

            preferences = new Preferences();
            preferences.setKeywordsSeparator(",");
        }

        panelNorth = new PanelNorth(this);
        add(panelNorth, BorderLayout.NORTH);

        panelCenter = new PanelCenter(this);
        add(panelCenter, BorderLayout.CENTER);

        panelSouth = new PanelSouth(this);
        add(panelSouth, BorderLayout.SOUTH);

        //Set Font for all child Components
        Map<TextAttribute, Object> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.FAMILY, "Tahoma");
        fontAttributes.put(TextAttribute.SIZE, 14);
        Font font1 = Font.getFont(fontAttributes);
        SwingUtil.changeFont(this, font1);

        startDaemons();

        setVisible(true);
    }

    public int getInitWidth() {
        return initWidth;
    }

    public int getInitHeight() {
        return initHeight;
    }

    public void savePreferences() {
        preferences.setSearchLocation(panelNorth.tfSearchLocation.getText());
        preferences.setKeywords(panelNorth.tfKeywords.getText());
        preferences.setKeywordsSeparator(panelNorth.tfKeywordSeparator.getText());

        JsonUtil.marshallToFile(PREFERENCES_FILE, preferences);
    }

    private void startDaemons() {
        Thread outputReader = new Thread(() -> {
            while (true) {
                try {
                    ListItem listItem = outputQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (!Objects.isNull(listItem)) {
                        panelSouth.writeOutput(listItem);
                    }
                } catch (Exception exception) {
                    panelSouth.writeOutput(new ListItem(exception));
                }
            }
        });
        outputReader.setUncaughtExceptionHandler(ExceptionHandler.getExceptionHandler(this));
        outputReader.setDaemon(true);
        outputReader.start();

        Thread errorReader = new Thread(() -> {
            while (true) {
                try {
                    ListItem listItem = errorQueue.poll(100, TimeUnit.MILLISECONDS);;
                    if (!Objects.isNull(listItem)) {
                        panelSouth.writeOutput(listItem);
                    }
                } catch (Exception exception) {
                    panelSouth.writeOutput(new ListItem(exception));
                }
            }
        });
        errorReader.setUncaughtExceptionHandler(ExceptionHandler.getExceptionHandler(this));
        errorReader.setDaemon(true);
        errorReader.start();
    }

}
