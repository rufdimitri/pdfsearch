package rd.pdfsearch;

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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exception.printStackTrace(new PrintStream(baos, true, StandardCharsets.UTF_8));
        String output = baos.toString(StandardCharsets.UTF_8);
        mainWindow.panelSouth.outputError(exception);
    }
}
