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

                SearchCriteria searchCriteria = mainWindow.preferences.getSearchCriteria();

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
