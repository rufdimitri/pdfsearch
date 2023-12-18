package rd.pdfsearch;

import rd.util.JsonUtil;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainWindowWindowListener implements WindowListener {
    final MainWindow mainWindow;

    public MainWindowWindowListener(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.mainWindow.updatePreferences();
        JsonUtil.of(Preferences.class).marshallToFile(this.mainWindow.preferencesFile, this.mainWindow.preferences);
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
