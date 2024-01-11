package rd.pdfsearch.listeners;

import com.google.gson.reflect.TypeToken;
import rd.pdfsearch.MainWindow;
import rd.pdfsearch.PDFSearchRequest;
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
            mainWindow.panelNorth.btSearch.setEnabled(false);
            mainWindow.panelSouth.clearOutput();
            mainWindow.savePreferences();
            if (mainWindow.cachedFilesPerFileIdentityHashCode == null || mainWindow.cachedFilesPerFileIdentityHashCode.isEmpty()) {
                mainWindow.cachedFilesPerFileIdentityHashCode = JsonUtil.unmarshallFromFileOrDefault(mainWindow.CACHED_PDF_FILENAME, new TypeToken<>(){}, new HashMap<>());
            }
            String filename = mainWindow.panelNorth.tfSearchLocation.getText().replaceAll("\"", "");

            //add regex quotation (\Q \E) to escape regex special characters that could appear in tfKeywordSeparator
            String splitter = "\\Q" + mainWindow.panelNorth.tfKeywordSeparator.getText() + "\\E";

            String[] keywordsArray = mainWindow.panelNorth.tfKeywords.getText()
                    .split(splitter);

            for (int i = 0; i < keywordsArray.length; ++i) {
                keywordsArray[i] = keywordsArray[i].trim().toLowerCase();
            }

            new Thread(() -> {
                try {
                    new PDFSearchRequest(mainWindow.outputQueue, mainWindow.errorQueue, mainWindow.cachedFilesPerFileIdentityHashCode)
                            .searchInMultipleFiles(filename, ".pdf", List.of(keywordsArray));
                    JsonUtil.marshallToFile(mainWindow.CACHED_PDF_FILENAME, mainWindow.cachedFilesPerFileIdentityHashCode);
                } finally {
                    mainWindow.panelNorth.btSearch.setEnabled(true);
                }
            }).start();
        });
    }
}
