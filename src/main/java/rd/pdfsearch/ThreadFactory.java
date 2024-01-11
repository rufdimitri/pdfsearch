package rd.pdfsearch;

public class ThreadFactory implements java.util.concurrent.ThreadFactory {
    private static ThreadFactory threadFactory;
    private MainWindow mainWindow;

    private ThreadFactory() {}

    public static ThreadFactory getThreadFactory(MainWindow mainWindow) {
        if (threadFactory == null) {
            ThreadFactory newThreadFactory = new ThreadFactory();
            newThreadFactory.mainWindow = mainWindow;
            threadFactory = newThreadFactory;
            return newThreadFactory;
        }
        return threadFactory;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread();
        t.setUncaughtExceptionHandler(ExceptionHandler.getExceptionHandler(mainWindow));
        return t;
    }
}
