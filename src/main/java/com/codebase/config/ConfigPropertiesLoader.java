package com.codebase.config;

import com.codebase.helpers.ErrorHandler;
import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigPropertiesLoader {

    private static final Logger logger = LoggerUtil.getLogger(ConfigPropertiesLoader.class);

    // Thread-safe cache for storing loaded properties files with their aliases.
    public static final Map<String, ConfigProperties> loadedFilesMap = new ConcurrentHashMap<>();


    public static void loadConfigurationFile(String alias, String filePath) throws IOException {
        // Check if the file is already loaded
        if (loadedFilesMap.containsKey(alias)) {
            logger.info("Properties file with alias '{}' already loaded.", alias);
            return;
        }

        // Use computeIfAbsent to load and cache the configuration file atomically
        loadedFilesMap.computeIfAbsent(alias, key -> {
            try {
                ConfigProperties loader = ConfigProperties.loadAndCacheConfigFile(key, filePath);
                logger.info("Properties file '{}' loaded with alias '{}' and stored in the map.", filePath, key);
                return loader;
            } catch (IOException ioError) {
                logger.error("IO error occurred while loading file '{}': {}", filePath, ioError.getMessage());
                throw new RuntimeException("Failed to load configuration file: " + filePath, ioError);
            } catch (Exception error) {
                logger.error("Unexpected error while loading file '{}': {}", filePath, error.getMessage());
                throw new RuntimeException("Failed to load configuration file: " + filePath, error);
            }
        });
    }

    /**
     * Retrieves a property value from a loaded properties file by its alias and key.
     *
     * @param alias The alias of the loaded properties file.
     * @param key   The key of the property to retrieve.
     * @return The value of the specified property.
     * @throws IllegalStateException If the properties file with the given alias has not been loaded.
     * @throws RuntimeException      If an unexpected error occurs during retrieval.
     */
    public static String getPropertyKey(String alias, String key) {
        try {
            if (!loadedFilesMap.containsKey(alias)) {
                throw new IllegalStateException(
                        "Configuration file with alias '" + alias + "' not loaded. Call loadConfigurationFile() first.");
            }
            return loadedFilesMap.get(alias).getPropertyKey(key);
        } catch (Exception error) {
            ErrorHandler.logError(error,
                    "getPropertyKey",
                    "Failed to retrieve property value for alias '" + alias + "' and key '" + key + "'");
            throw error;
        }
    }

    /**
     * Checks if the loadedFilesMap contains an alias with existing data.
     * If it does, the method fails and throws an exception.
     *
     * @param alias The alias to check in the map.
     */
    public static void checkAliasData(String alias) {
        if (loadedFilesMap.containsKey(alias)) {
            ConfigProperties configProperties = loadedFilesMap.get(alias);

            // Check if the alias has any property keys
            if (configProperties != null && !configProperties.getPropertyKey(alias).isEmpty()) {
                throw new IllegalStateException(
                        String.format("Alias '%s' already has data in the loadedFilesMap.", alias)
                );
            }
        }
    }
}
