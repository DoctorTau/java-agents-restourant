package com.agents;

import java.io.IOException;
import java.util.logging.*;

public class MyLogger {

    private static Logger logger;
    private static final String LOGGER_NAME = "my-logger";

    static {
        logger = Logger.getLogger(LOGGER_NAME);
        logger.setLevel(Level.ALL);

        // Create console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);

        // Create file handler
        try {
            FileHandler fileHandler = new FileHandler("logs.json");
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new JsonFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create file handler", e);
        }

        logger.addHandler(consoleHandler);
    }

    public static Logger getLogger() {
        return logger;
    }

    private static class JsonFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format(
                    "{\"level\": \"%s\", \"message\": \"%s\"}%n",
                    record.getLevel(), record.getMessage());
        }
    }

}
