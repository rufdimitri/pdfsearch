package rd.pdfsearch.listeners;

import com.google.gson.reflect.TypeToken;
import rd.pdfsearch.MainWindow;
import rd.pdfsearch.PDFSearchRequest;
import rd.pdfsearch.model.SearchCriteria;
import rd.util.JsonUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

public class BtSearchActionListener implements ActionListener {
    private final MainWindow mainWindow;

    public BtSearchActionListener(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            try {
                mainWindow.panelNorth.btSearch.setEnabled(false);
                mainWindow.panelSouth.clearOutput();
                mainWindow.savePreferences();
                if (mainWindow.cachedFilesPerFileIdentityHashCode == null || mainWindow.cachedFilesPerFileIdentityHashCode.isEmpty()) {
                    mainWindow.cachedFilesPerFileIdentityHashCode = JsonUtil.unmarshallFromFileOrDefault(mainWindow.CACHED_PDF_FILENAME, new TypeToken<>() {
                    }, new HashMap<>());
                }
                String filename = mainWindow.panelNorth.tfSearchLocation.getText().replaceAll("\"", "");

                //add regex quotation (\Q \E) to escape regex special characters that could appear in tfKeywordSeparator
                String splitter = "\\Q" + mainWindow.panelNorth.tfKeywordSeparator.getText() + "\\E";

                String[] keywordsArray = mainWindow.panelNorth.tfKeywords.getText()
                        .split(splitter);

                for (int i = 0; i < keywordsArray.length; ++i) {
                    keywordsArray[i] = keywordsArray[i].trim().toLowerCase();
                }

                int rangeSize = 0;
                try {
                    rangeSize = Integer.parseInt(mainWindow.panelNorth.tfRange.getText());
                } catch (NumberFormatException numberFormatException) {
                    throw new RuntimeException("Could not parse range size.", numberFormatException);
                }

                SearchCriteria searchCriteria = mainWindow.panelNorth.rbRange.isSelected()
                        ? new SearchCriteria(List.of(keywordsArray), SearchCriteria.WordScopeType.RANGE, rangeSize)
                        : new SearchCriteria(List.of(keywordsArray), SearchCriteria.WordScopeType.DOCUMENT);

                new Thread(() -> {
                    new PDFSearchRequest(mainWindow.outputQueue, mainWindow.errorQueue, mainWindow.cachedFilesPerFileIdentityHashCode)
                            .searchInMultipleFiles(filename, ".pdf", searchCriteria);
                    JsonUtil.marshallToFile(mainWindow.CACHED_PDF_FILENAME, mainWindow.cachedFilesPerFileIdentityHashCode);
                }).start();
            } finally {
                mainWindow.panelNorth.btSearch.setEnabled(true);
            }
        });
    }
}
