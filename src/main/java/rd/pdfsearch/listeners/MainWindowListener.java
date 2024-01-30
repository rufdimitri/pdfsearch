package rd.pdfsearch.listeners;

import rd.pdfsearch.MainWindow;
import rd.pdfsearch.PDFSearchRequest;
import rd.util.JsonUtil;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Optional;

public class MainWindowListener implements WindowListener {
    final MainWindow mainWindow;

    public MainWindowListener(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            this.mainWindow.updateStatus("Saving settings and closing...");
            Optional.ofNullable(mainWindow.getSearchRequest()).ifPresent(PDFSearchRequest::interruptSearch);
            this.mainWindow.savePreferences();
            Optional.ofNullable(this.mainWindow.fileSearchFuture).ifPresent((future) -> future.cancel(true));
            if (!mainWindow.cachedFilesPerFileIdentityHashCode.isEmpty()) {
                JsonUtil.marshallToFile(mainWindow.CACHED_PDF_FILENAME, mainWindow.cachedFilesPerFileIdentityHashCode);
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Error: " + exception.toString(), mainWindow.getTitle(), JOptionPane.ERROR_MESSAGE);
        } finally {
            this.mainWindow.dispose();
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

}
