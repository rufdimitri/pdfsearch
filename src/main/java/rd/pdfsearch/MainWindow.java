package rd.pdfsearch;

import com.google.gson.reflect.TypeToken;
import rd.pdfsearch.listeners.MainWindowListener;
import rd.pdfsearch.model.CachedPdfFile;
import rd.pdfsearch.model.ListItem;
import rd.pdfsearch.model.Preferences;
import rd.pdfsearch.model.SearchCriteria;
import rd.util.Concurrent;
import rd.util.JsonUtil;
import rd.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.*;
import java.util.concurrent.BlockingQueue;
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
    public final BlockingQueue<ListItem> outputQueue = new LinkedBlockingQueue<>(10);
    public final BlockingQueue<Throwable> errorQueue = new LinkedBlockingQueue<>(10);
    public Map<Integer, List<CachedPdfFile>> cachedFilesPerFileIdentityHashCode = Concurrent.concurrentMap(new HashMap<>());
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

            preferences = Preferences.builder().keywordsSeparator(" ").rangeSize(200).build();
        }

        panelNorth = new PanelNorth(this);
        add(panelNorth, BorderLayout.NORTH);

        Dimension minimumSize = new Dimension(100, 100);
        panelCenter = new PanelCenter(this);
        panelCenter.setMinimumSize(minimumSize);
        panelCenter.setPreferredSize(new Dimension(MainWindow.this.getWidth(), MainWindow.this.getHeight() / 3));
        panelSouth = new PanelSouth(this);
        panelSouth.setMinimumSize(minimumSize);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelCenter, panelSouth);
        add(splitPane, BorderLayout.CENTER);

        //Set Font for all child Components
        Map<TextAttribute, Object> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.FAMILY, "Tahoma");
        fontAttributes.put(TextAttribute.SIZE, 14);
        Font font1 = Font.getFont(fontAttributes);
        SwingUtil.changeFontRecursive(this, font1);

        //create menu
        //Create the menu bar.
        JMenuBar menuBar = new JMenuBar();

        //Build the first menu.
        JMenu menu = new JMenu("Help");
        menuBar.add(menu);

        //a group of JMenuItems
        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(event -> {
            JOptionPane.showMessageDialog(null, "This program is used to search through .pdf files. \n"
                + "• Choose a folder where pdf files are \n"
                + "• Type in keywords that a pdf file should contain. Use keyword separator symbol to split different keywords (by default - space) \n"
                + "• Choose scope type: \n  - Document - find files that contain all of the keywords \n"
                + "  - Range - find files where the keywords are within defined range of characters \n"
                + "• Click Search button to start search \n"
                + "• Check results below, click on a word to see it's context, double-click on a file to open it."
            );
        });
        menu.add(helpItem);

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(event -> {
            JOptionPane.showMessageDialog(null, "Author: Dimitri Ruf. \nCheck out my github: \ngithub.com/rufdimitri");
        });
        menu.add(aboutItem);

        this.setJMenuBar(menuBar);

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
        updatePreferences();
        JsonUtil.marshallToFile(this.PREFERENCES_FILE, this.preferences);
    }

    public void updatePreferences() {
        preferences.setSearchLocation(this.panelNorth.tfSearchLocation.getText());

        int rangeSize;
        try {
            rangeSize = Integer.parseInt(this.panelNorth.tfRange.getText());
        } catch (NumberFormatException numberFormatException) {
            throw new RuntimeException("Could not parse range size.", numberFormatException);
        }

        SearchCriteria searchCriteria = this.panelNorth.rbRange.isSelected()
                ? new SearchCriteria(getKeywords(), SearchCriteria.WordScopeType.RANGE, rangeSize)
                : new SearchCriteria(getKeywords(), SearchCriteria.WordScopeType.DOCUMENT, rangeSize);
        preferences.setSearchCriteria(searchCriteria);

        preferences.setKeywordsSeparator(this.panelNorth.tfKeywordSeparator.getText());
    }

    public List<String> getKeywords() {
        //add regex quotation (\Q \E) to escape regex special characters that could appear in tfKeywordSeparator
        String splitter = "\\Q" + this.panelNorth.tfKeywordSeparator.getText() + "\\E";

        return Arrays.stream(this.panelNorth.tfKeywords.getText().trim().split(splitter))
                .filter(keyword -> !keyword.isBlank())
                .map(String::toLowerCase)
                .toList();
    }

    private void startDaemons() {
        Thread outputReader = new Thread(() -> {
            while (true) {
                try {
                    ListItem outputObject = outputQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (!Objects.isNull(outputObject)) {
                        panelSouth.writeOutput(outputObject);
                    }
                } catch (Exception exception) {
                    panelSouth.writeError(exception);
                }
            }
        });
        outputReader.setUncaughtExceptionHandler(ExceptionHandler.getExceptionHandler(this));
        outputReader.setDaemon(true);
        outputReader.start();

        Thread errorReader = new Thread(() -> {
            while (true) {
                try {
                    Throwable outputObject = errorQueue.poll(100, TimeUnit.MILLISECONDS);;
                    if (!Objects.isNull(outputObject)) {
                        panelSouth.writeError(outputObject);
                    }
                } catch (Exception exception) {
                    panelSouth.writeError(exception);
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
