package com.codebase.config;

import com.codebase.helpers.ErrorHandler;
import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigProperties {

    /**
     * This class is designed to access configuration properties exclusively through the
     * ConfigPropertiesLoader class. Direct calls to retrieve properties from this class
     * are discouraged for the following reasons:
     * -
     * 1. **Performance**: Accessing properties from an in-memory map is significantly
     *    faster than repeatedly reading from a file.
     * 2. **Consistency**: Properties are loaded once, minimizing the risk of
     *    inconsistencies that may arise from file changes during execution.
     * -
     * Always use the ConfigPropertiesLoader class to ensure optimal performance and
     * maintain consistency in property access.
     */

    private static final Logger logger = LoggerUtil.getLogger(ConfigProperties.class);

    // Cache to store loaded properties files with their aliases.
    private static final Map<String, ConfigProperties> loadedConfigMap = new HashMap<>();

    // The properties file content.
    private final Properties properties;

    /**
     * The path of the properties file being loaded.
     */
    private final String configFilePath;

    /**
     * Constructs a new {@code ConfigProperties} and loads the specified properties file.
     *
     * @param configFilePath The path to the properties file.
     * @throws IOException              If the properties file cannot be found or loaded.
     * @throws IllegalArgumentException If the file path is null or empty.
     */
    public ConfigProperties(String configFilePath) throws IOException {
        if (configFilePath == null || configFilePath.isEmpty()) {
            logger.warn("Configuration file path cannot be null or empty.");
            throw new IllegalArgumentException("Configuration file path cannot be null or empty.");
        }

        this.properties = new Properties();
        this.configFilePath = configFilePath;
        loadProperties();
    }

    /**
     * Loads a properties file and caches it with an alias.
     *
     * @param alias    The alias to identify the loaded properties file.
     * @param filePath The path to the properties file.
     * @return The {@code ConfigProperties} instance for the loaded file.
     * @throws IOException If the properties file cannot be found or loaded.
     */
    public static ConfigProperties loadAndCacheConfigFile(String alias, String filePath) throws IOException {
        if (loadedConfigMap.containsKey(alias)) {
            logger.info("Properties file '{}' is already loaded.", alias);
            return loadedConfigMap.get(alias);
        }

        try {
            ConfigProperties loader = new ConfigProperties(filePath);
            loadedConfigMap.put(alias, loader);
            logger.info("Properties file '{}' loaded successfully.", alias);
            return loader;
        } catch (IOException error) {
            ErrorHandler.logError(error, "loadAndCacheConfigFile", "Failed to load config file: " + filePath);
            throw error;
        }
    }

    /**
     * Retrieves a property value for a specified key, checking system properties first.
     *
     * @param key The property key to retrieve.
     * @return The value of the specified property.
     * @throws IllegalArgumentException If the key does not exist or its value is empty.
     */
    public String getPropertyKey(String key) {
        try {
            // Check for system property first
            String systemPropertyValue = System.getProperty(key);
            if (systemPropertyValue != null) {
                logger.info("Retrieved system property '{}' with value '{}'", key, systemPropertyValue);
                return systemPropertyValue;
            }

            // Fallback to properties file
            String value = properties.getProperty(key);
            if (value == null || value.isEmpty()) {
                logger.warn("Property '{}' is not set or empty", key);
                throw new IllegalArgumentException("Property '" + key + "' is not set or empty");
            }
            return value;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getPropertyKey", "Failed to retrieve property '" + key + "'");
            throw error;
        }
    }

    /**
     * Retrieves a property value for a specified key, returning a default value if the key does not exist.
     *
     * @param key          The property key to retrieve.
     * @param defaultValue The default value to return if the key is not found.
     * @return The value of the property or the default value if the property does not exist.
     */
    public String getPropertyKey(String key, String defaultValue) {
        // Check for system property first
        String systemPropertyValue = System.getProperty(key);
        if (systemPropertyValue != null) {
            logger.info("Retrieved system property '{}' with value '{}'", key, systemPropertyValue);
            return systemPropertyValue;
        }

        // Fallback to properties file
        String value = properties.getProperty(key, defaultValue);
        if (value.equals(defaultValue)) {
            logger.warn("Property '{}' is not set. Returning default value '{}'", key, defaultValue);
        } else {
            logger.info("Successfully retrieved property '{}' with value '{}'", key, value);
        }
        return value;
    }

    /**
     * Retrieves a cached {@code ConfigProperties} instance by its alias.
     *
     * @param alias The alias used when loading the properties file.
     * @return The cached {@code ConfigProperties} instance.
     * @throws IllegalStateException If no properties file is associated with the specified alias.
     */
    public static ConfigProperties getLoadedFileByAliasName(String alias) {
        if (!loadedConfigMap.containsKey(alias)) {
            throw new IllegalStateException("Properties file with alias '" + alias + "' is not loaded.");
        }
        return loadedConfigMap.get(alias);
    }

    /**
     * Loads the properties file content into memory.
     *
     * @throws IOException If the file cannot be found or read.
     */
    private void loadProperties() throws IOException {
        if (!Files.exists(Path.of(configFilePath))) {
            logger.error("Properties file '{}' does not exist", configFilePath);
            throw new FileNotFoundException("Properties file not found: " + configFilePath);
        }

        try (FileInputStream inputStream = new FileInputStream(configFilePath)) {
            properties.load(inputStream);
        } catch (IOException error) {
            ErrorHandler.logError(error, "loadProperties", "Failed to load properties file");
            throw error;
        }
    }
}
