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
import java.util.function.Consumer;

public class BtSearchActionListener implements ActionListener {
    private final MainWindow mainWindow;

    public BtSearchActionListener(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            try {
                mainWindow.updateStatus("Search started. Loading caches.");
                mainWindow.panelNorth.btSearch.setEnabled(false);
                mainWindow.panelSouth.clearOutput();
                mainWindow.savePreferences();
                if (mainWindow.cachedFilesPerFileIdentityHashCode == null || mainWindow.cachedFilesPerFileIdentityHashCode.isEmpty()) {
                    mainWindow.cachedFilesPerFileIdentityHashCode = JsonUtil.unmarshallFromFileOrDefault(mainWindow.CACHED_PDF_FILENAME, new TypeToken<>() {
                    }, new HashMap<>());
                }
                String filename = mainWindow.panelNorth.tfSearchLocation.getText().replaceAll("\"", "");

                SearchCriteria searchCriteria = mainWindow.preferences.getSearchCriteria();

                new Thread(() -> {
                    Consumer<String> updateStatus = mainWindow::updateStatus;
                    new PDFSearchRequest(mainWindow.outputQueue, mainWindow.errorQueue, mainWindow.cachedFilesPerFileIdentityHashCode, updateStatus)
                            .searchInMultipleFiles(filename, ".pdf", searchCriteria);
                    mainWindow.updateStatus("Search finished. Saving caches.");
                    JsonUtil.marshallToFile(mainWindow.CACHED_PDF_FILENAME, mainWindow.cachedFilesPerFileIdentityHashCode);
                    mainWindow.updateStatus("Caches saved.");
                }).start();
            } finally {
                mainWindow.panelNorth.btSearch.setEnabled(true);
            }
        });
    }
}
