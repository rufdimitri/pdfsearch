package rd.pdfsearch.listeners;

import rd.pdfsearch.MainWindow;

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
        this.mainWindow.savePreferences();
        this.mainWindow.executorService.shutdownNow();
        Optional.ofNullable(this.mainWindow.fileSearchFuture).ifPresent((future) -> future.cancel(true));
        this.mainWindow.dispose();
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
