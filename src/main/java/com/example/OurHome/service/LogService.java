package com.example.OurHome.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
public class LogService {

    private static final Logger customLogger = Logger.getLogger("LogService");

    public LogService() {
        initializeFileHandler();
    }

    private void initializeFileHandler() {
        String logFilePath = "src/main/resources/OurHome.log"; // Файлът за custom логове

        try {
            FileHandler fileHandler = new FileHandler(logFilePath, true);
            fileHandler.setFormatter(new SimpleFormatter() {
                private static final String format = "%1$tF %1$tT %2$s%n"; // YYYY-MM-DD HH:MM:SS Message

                @Override
                public synchronized String format(java.util.logging.LogRecord record) {
                    return String.format(format, record.getMillis(), record.getMessage());
                }
            });
            customLogger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Поддържа {} за форматиране на съобщения (подобно на SLF4J)
    public void info(String message, Object... args) {
        customLogger.info(formatMessage(message, args));
    }

    public void warn(String message, Object... args) {
        customLogger.warning(formatMessage(message, args));
    }

    public void error(String message, Object... args) {
        Throwable throwable = extractThrowable(args);
        String formattedMessage = formatMessage(message, args);

        if (throwable != null) {
            String stackTrace = getStackTrace(throwable);
            customLogger.severe(formattedMessage + " | Exception: " + stackTrace);
        } else {
            customLogger.severe(formattedMessage);
        }
    }

    private String formatMessage(String message, Object... args) {
        return message.replace("{}", "%s").formatted(args); // Заменя {} с %s и форматира текста
    }

    private Throwable extractThrowable(Object... args) {
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            return (Throwable) args[args.length - 1];
        }
        return null;
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}