package com.codebase.config;

import com.codebase.ConfigPaths.DotEnvFilePaths;
import com.codebase.helpers.Base64Utility;
import com.codebase.helpers.ErrorHandler;
import com.codebase.utils.LoggerUtil;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DotenvConfig {

    private static final Logger logger = LoggerUtil.getLogger(DotenvConfig.class);
    protected final Dotenv dotenv;

    /**
     * Constructor for loading environment variables from the specified file.
     *
     * @param envName The name of the environment file (e.g., ".env.dev").
     */
    public DotenvConfig(String envName) {
        try {
            this.dotenv = Dotenv.configure()
                    .directory(DotEnvFilePaths.getEnvDirectoryPath())
                    .filename(envName).load();
        } catch (Exception error) {
            ErrorHandler.logError(error, "DotenvConfig Constructor", "Failed to load dotenv variables");
            throw error;
        }
    }

    /**
     * Loads environment properties from the specified environment file.
     *
     * @param envName The name of the environment file (e.g., ".env.dev").
     * @return A ConfigProperties instance or null if an error occurs.
     */
    public static DotenvConfig loadEnvironment(String envName) throws IOException {
        try {
            logger.info("Environment '{}' was loaded successfully", envName);
            return new DotenvConfig(envName);
        } catch (Exception error) {
            ErrorHandler.logError(error, "loadEnvironment", "Failed to load " + "'" + envName + "'" + " environment");
            throw error;
        }
    }

    /**
     * Retrieves the value of the specified environment key.
     *
     * @param key The key of the environment variable to retrieve.
     * @return The value of the environment variable or null if not found.
     */
    public String getEnvironmentKey(String key) {
        try {
            return dotenv.get(key);
        } catch (Exception error) {
            ErrorHandler.logError(error, "getEnvironmentKey", "Failed to load environment key");
            throw error;
        }
    }

    /**
     * Retrieves the value of the specified environment key with a default value if the key is not found.
     *
     * @param key The key of the environment variable to retrieve.
     * @param defaultValue The value to return if the environment key is not found.
     * @return The value of the environment variable or the default value if not found.
     */
    public String getEnvironmentKey(String key, String defaultValue) {
        try {
            return dotenv.get(key, defaultValue);
        } catch (Exception error) {
            ErrorHandler.logError(error, "getEnvironmentKey", "Failed to load environment key");
            throw error;
        }
    }

    /**
     * Retrieves a secret key stored as a Base64-encoded string and decodes it into a SecretKey.
     *
     * @param key The key of the environment variable containing the encoded secret key.
     * @return The decoded SecretKey.
     * @throws IllegalArgumentException If the secret key is missing or invalid.
     */
    public SecretKey getSecretKey(String key) {
        String encodedKey = getEnvironmentKey(key);
        if (encodedKey == null) {
            logger.error("Secret key for '{}' is missing or not configured", key);
            throw new IllegalArgumentException("Secret key for '" + key + "' is missing");
        }
        return Base64Utility.decodeSecretKey(encodedKey);
    }

    /**
     * Validates that all required environment keys are present.
     *
     * @param requiredKeys The keys of the environment variables that must be present.
     */
    public void validateRequiredEnvironmentKeys(String... requiredKeys) {
        try {
            List<String> missingKeys = new ArrayList<>();
            for (String key : requiredKeys) {
                if (dotenv.get(key) == null) {
                    missingKeys.add(key);
                }
            }
            if (!missingKeys.isEmpty()) {
                ErrorHandler.logAndThrowError("Missing required  environment variables: {}", String.join(", ", missingKeys));
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "validateRequiredVariables", "Failed to validate required environment keys");
            throw error;
        }
    }
}
