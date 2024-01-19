package rd.pdfsearch;

import com.google.gson.reflect.TypeToken;
import rd.pdfsearch.listeners.MainWindowListener;
import rd.pdfsearch.model.CachedPdfFile;
import rd.pdfsearch.model.Preferences;
import rd.pdfsearch.model.SearchCriteria;
import rd.util.JsonUtil;
import rd.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
    public final BlockingQueue<Object> outputQueue = new LinkedBlockingQueue<>(10);
    public final BlockingQueue<Throwable> errorQueue = new LinkedBlockingQueue<>(10);
    public Map<Integer, List<CachedPdfFile>> cachedFilesPerFileIdentityHashCode = new HashMap<>();
    private PDFSearchRequest searchRequest;

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

            preferences = Preferences.builder().keywordsSeparator(", ").rangeSize(200).build();
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
        SwingUtil.changeFontRecursive(this, font1);

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
        preferences.setSearchLocation(this.panelNorth.tfSearchLocation.getText());

        //add regex quotation (\Q \E) to escape regex special characters that could appear in tfKeywordSeparator
        String splitter = "\\Q" + this.panelNorth.tfKeywordSeparator.getText() + "\\E";

        List<String> keywords = Arrays.stream(this.panelNorth.tfKeywords.getText().trim().split(splitter))
                .filter(keyword -> !keyword.isBlank())
                .map(String::toLowerCase)
                .toList();

        int rangeSize;
        try {
            rangeSize = Integer.parseInt(this.panelNorth.tfRange.getText());
        } catch (NumberFormatException numberFormatException) {
            throw new RuntimeException("Could not parse range size.", numberFormatException);
        }

        SearchCriteria searchCriteria = this.panelNorth.rbRange.isSelected()
                ? new SearchCriteria(keywords, SearchCriteria.WordScopeType.RANGE, rangeSize)
                : new SearchCriteria(keywords, SearchCriteria.WordScopeType.DOCUMENT, rangeSize);
        preferences.setSearchCriteria(searchCriteria);

        preferences.setKeywordsSeparator(this.panelNorth.tfKeywordSeparator.getText());

        JsonUtil.marshallToFile(this.PREFERENCES_FILE, this.preferences);
    }

    private void startDaemons() {
        Thread outputReader = new Thread(() -> {
            while (true) {
                try {
                    Object outputObject = outputQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (!Objects.isNull(outputObject)) {
                        panelSouth.writeOutput(outputObject);
                    }
                } catch (Exception exception) {
                    panelSouth.writeOutput(exception);
                }
            }
        });
        outputReader.setUncaughtExceptionHandler(ExceptionHandler.getExceptionHandler(this));
        outputReader.setDaemon(true);
        outputReader.start();

        Thread errorReader = new Thread(() -> {
            while (true) {
                try {
                    Object outputObject = errorQueue.poll(100, TimeUnit.MILLISECONDS);;
                    if (!Objects.isNull(outputObject)) {
                        panelSouth.writeOutput(outputObject);
                    }
                } catch (Exception exception) {
                    panelSouth.writeOutput(exception);
                }
            }
        });
        errorReader.setUncaughtExceptionHandler(ExceptionHandler.getExceptionHandler(this));
        errorReader.setDaemon(true);
        errorReader.start();
    }

    public void updateStatus(String text) {
        this.panelNorth.lbStatus.setText(text);
    }

    public PDFSearchRequest getSearchRequest() {
        synchronized (this) {
            return searchRequest;
        }
    }

    public void setSearchRequest(PDFSearchRequest searchRequest) {
        synchronized (this) {
            this.searchRequest = searchRequest;
        }
    }

}
