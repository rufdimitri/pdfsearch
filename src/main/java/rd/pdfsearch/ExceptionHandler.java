package rd.pdfsearch;

import rd.pdfsearch.model.ListItem;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static ExceptionHandler exceptionHandler;
    private MainWindow mainWindow;

    private ExceptionHandler() {}

    public static Thread.UncaughtExceptionHandler getExceptionHandler(MainWindow mainWindow) {
        if (exceptionHandler == null) {
            ExceptionHandler newExceptionHandler = new ExceptionHandler();
            newExceptionHandler.mainWindow = mainWindow;
            exceptionHandler = newExceptionHandler;
            return newExceptionHandler;
        }
        return exceptionHandler;
    }

    @Override
    public void uncaughtException(Thread t, Throwable exception) {
        exception.printStackTrace(System.err);
        if (!mainWindow.isVisible()) {
            String solution = String.format("Possible solution: delete %s and %s files from current directory of this program.", mainWindow.PREFERENCES_FILE, mainWindow.CACHED_PDF_FILENAME);
            JOptionPane.showMessageDialog(null, exception + "\n\n" + solution, "PdfSearch", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        mainWindow.panelSouth.writeOutput(exception);
    }
}
