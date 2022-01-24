package se.liu.chessGame;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class logs all exceptions that happens while running the program.
 * It follows the singleton design pattern with only on instance of the class.
 * This is due to not requiring a field in every class using the logger.
 */
public class ExceptionLogger
{
    private Logger logger = null;
    final static private ExceptionLogger EXCEPTION_LOGGER = new ExceptionLogger();

    private ExceptionLogger() {
        String stringFile = System.getProperty("user.dir") + File.separator + "logger.txt";
        try {
            FileHandler fileHandler = new FileHandler(stringFile, true);
            logger = Logger.getLogger(ExceptionLogger.class.getName());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {// If the logger can't be initiated, no exceptions can't be logged and every instance of using the logger
            // will not work.
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static ExceptionLogger getExceptionLogger() {
        return EXCEPTION_LOGGER;
    }

    public void logException(Level level, String message, Exception e) {
        logger.log(level, message, e);
    }
}
