package com.codebase.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LoggerUtil {

    // Private constructor to prevent instantiation
    private LoggerUtil() {}

    /**
     * Retrieves a logger for the specified class.
     *
     * @param clazz the class for which the logger is to be retrieved
     * @return the logger for the specified class
     */
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
}
