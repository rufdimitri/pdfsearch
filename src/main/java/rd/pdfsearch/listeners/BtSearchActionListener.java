package rd.pdfsearch.listeners;

import rd.pdfsearch.MainWindow;
import rd.pdfsearch.PDFSearchRequest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BtSearchActionListener implements ActionListener {
    private final MainWindow mainWindow;

    public BtSearchActionListener(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainWindow.panelNorth.btSearch.setEnabled(false);
        mainWindow.panelSouth.clearOutput();
        mainWindow.savePreferences();
        String filename = mainWindow.panelNorth.tfSearchLocation.getText().replaceAll("\"", "");

        //add regex quotation (\Q \E) to escape regex special characters that could appear in tfKeywordSeparator
        String splitter = "\\Q" + mainWindow.panelNorth.tfKeywordSeparator.getText() + "\\E";

        String[] keywordsArray = mainWindow.panelNorth.tfKeywords.getText()
                .split(splitter);

        for (int i = 0; i < keywordsArray.length; ++i) {
            keywordsArray[i] = keywordsArray[i].trim().toLowerCase();
        }

        mainWindow.fileSearchFuture = mainWindow.executorService.submit(() -> {
            new PDFSearchRequest(mainWindow.outputQueue, mainWindow.errorQueue).searchInMultipleFiles(filename, ".pdf", List.of(keywordsArray));
            mainWindow.panelNorth.btSearch.setEnabled(true);
        });
    }
}
