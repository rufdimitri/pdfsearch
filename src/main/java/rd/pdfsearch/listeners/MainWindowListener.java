package rd.pdfsearch.listeners;

import rd.pdfsearch.MainWindow;
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
        this.mainWindow.updateStatus("Saving settings and closing...");
        SwingUtilities.invokeLater(() -> {
            this.mainWindow.savePreferences();
            Optional.ofNullable(this.mainWindow.fileSearchFuture).ifPresent((future) -> future.cancel(true));
            if (!mainWindow.cachedFilesPerFileIdentityHashCode.isEmpty()) {
                JsonUtil.marshallToFile(mainWindow.CACHED_PDF_FILENAME, mainWindow.cachedFilesPerFileIdentityHashCode);
            }
            this.mainWindow.dispose();
            System.exit(0);
        });
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
