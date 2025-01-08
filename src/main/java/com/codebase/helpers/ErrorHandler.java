package com.codebase.helpers;


import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

public class ErrorHandler {

    private static final Logger logger = LoggerUtil.getLogger(ErrorHandler.class);


    public static void logError(Throwable error, String methodName, String errorMessage) {
        // Validate input parameters
        if (error == null) {
            throw new IllegalArgumentException("Error cannot be null");
        }
        if (methodName == null || methodName.isBlank()) {
            throw new IllegalArgumentException("Method name cannot be null or empty");
        }

        // Construct a detailed error message
        String detailedMessage = String.format(
                "An error occurred in method '%s': %s. Details: %s",
                methodName,
                errorMessage != null ? errorMessage : "No additional error message provided",
                error.getMessage()
        );

        // Log the error and stack trace
        logger.error(detailedMessage, error);
    }

    /**
     * Logs an error message and throws a RuntimeException with the same message.
     *
     * @param errorMessage The error message to log and include in the exception.
     * @param context Additional context information to include in the log message.
     * @throws IllegalArgumentException if the errorMessage is null or empty.
     */
    public static void logAndThrowError(String errorMessage, String context) {
        // Validate input parameters
        if (errorMessage == null || errorMessage.isEmpty()) {
            throw new IllegalArgumentException("Error message cannot be null or empty");
        }

        // Construct a detailed log message
        String detailedMessage = String.format("Error occurred: %s. Context: %s", errorMessage, context);

        // Log the error message
        logger.error(detailedMessage);

        // Throw a RuntimeException with the detailed message
        throw new RuntimeException(detailedMessage);
    }
}
