package rd.pdfsearch.listeners;

import com.google.gson.reflect.TypeToken;
import rd.pdfsearch.MainWindow;
import rd.pdfsearch.PDFSearchRequest;
import rd.pdfsearch.model.SearchCriteria;
import rd.util.Concurrent;
import rd.util.JsonUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public class BtStopActionListener implements ActionListener {
    private final MainWindow mainWindow;

    public BtStopActionListener(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainWindow.panelNorth.btStop.setEnabled(false);
        Optional.ofNullable(mainWindow.getSearchRequest()).ifPresent(PDFSearchRequest::interruptSearch);
        mainWindow.panelNorth.btSearch.setEnabled(true);
    }
}
