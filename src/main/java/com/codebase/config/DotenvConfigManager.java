package com.codebase.config;

import com.codebase.helpers.ErrorHandler;
import java.util.HashMap;
import java.util.Map;

public class DotenvConfigManager {

    private static final Map<String, DotenvConfig> configMap = new HashMap<>();

    private DotenvConfigManager() {}

    /**
     * Initializes a new configuration or replaces an existing one.
     * @param name Unique name for the configuration.
     * @param config The DotenvConfig instance to associate with the name.
     */
    public static void initialize(String name, DotenvConfig config) {
        try {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Configuration name cannot be null or empty.");
            }

            if (config == null) {
                throw new IllegalArgumentException("DotenvConfig cannot be null.");
            }

            synchronized (configMap) {
                if (configMap.containsKey(name)) {
                    throw new IllegalStateException("Configuration with name '" + name + "' already exists.");
                }
                configMap.put(name, config);
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "initializeConfig", "Failed to initialize configuration: " + name);
            throw error;
        }
    }

    /**
     * Retrieves a configuration by name.
     * @param name The unique name of the configuration to retrieve.
     * @return The DotenvConfig instance associated with the name.
     */
    public static DotenvConfig getInstance(String name) {
        try {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Configuration name cannot be null or empty.");
            }

            synchronized (configMap) {
                if (!configMap.containsKey(name)) {
                    throw new IllegalStateException("Configuration with name '" + name + "' is not initialized.");
                }
                return configMap.get(name);
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "getInstance", "Failed to get configuration: " + name);
            throw error;
        }
    }

    /**
     * Removes a configuration by name.
     * @param name The unique name of the configuration to remove.
     */
    public static void remove(String name) {
        try {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Configuration name cannot be null or empty.");
            }

            synchronized (configMap) {
                if (!configMap.containsKey(name)) {
                    throw new IllegalStateException("Configuration with name '" + name + "' does not exist.");
                }
                configMap.remove(name);
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "removeConfig", "Failed to remove configuration: " + name);
            throw error;
        }
    }

    /**
     * Checks if a configuration exists by name.
     * @param name The unique name of the configuration to check.
     * @return True if the configuration exists, false otherwise.
     */
    public static boolean exists(String name) {
        synchronized (configMap) {
            return configMap.containsKey(name);
        }
    }
}


//package com.codebase.config;
//
//import com.codebase.helpers.ErrorHandler;
//
//public class DotenvConfigManager {
//
//    private static DotenvConfig dotenvConfig;
//
//    private DotenvConfigManager() {}
//
//    public static void initialize(DotenvConfig config) {
//        try {
//            if (dotenvConfig == null) {
//                dotenvConfig = config;
//            } else {
//                throw new IllegalStateException("DotenvConfig has already been initialized.");
//            }
//        } catch (Exception error){
//            ErrorHandler.logError(error, "initializeDotEnv", "Failed to initialize DotenvConfig");
//            throw error;
//        }
//    }
//
//    public static DotenvConfig getInstance() {
//        try {
//        if (dotenvConfig == null) {
//            throw new IllegalStateException("DotenvConfig is not initialized. Call initialize first.");
//        }
//        return dotenvConfig;
//        } catch (Exception error){
//            ErrorHandler.logError(error, "getInstance", "Failed to get DotenvConfig instance");
//            throw error;
//        }
//    }
//}
