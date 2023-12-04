package core.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHelper {

    private static LogHelper instance = null;

    private LogHelper() {
    }

    public static LogHelper getInstance() {
        if (instance == null) {
            instance = new LogHelper();
        }
        return instance;
    }

    public static Logger getLogger() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[3].getClassName();
        return LoggerFactory.getLogger(className);
    }

    public void error(String var1, Object... var2) {
        getLogger().error(var1, var2);
    }

    public void fail(String var1, Object... var2) {
        getLogger().error(var1, var2);
        throw new AssertionError(var1);
    }

    public void debug(String var1, Object... var2) {
        getLogger().debug(var1, var2);
    }

    public void info(String var1, Object... var2) {
        getLogger().info(var1, var2);
    }
}
