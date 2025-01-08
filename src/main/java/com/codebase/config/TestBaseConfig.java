package com.codebase.config;

import com.codebase.ConfigPaths.DotEnvFilePaths;
import com.codebase.ConfigPaths.PropertiesFilePaths;
import com.codebase.helpers.Base64Utility;
import com.codebase.helpers.ErrorHandler;
import com.codebase.parameters.AppSettings;
import com.codebase.parameters.ConfigFileAliases;
import com.codebase.parameters.EnvironmentAliases;
import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestBaseConfig {

    private static final Logger logger = LoggerUtil.getLogger(TestBaseConfig.class);

    // Adjust thread count based on expected concurrency
    private static final ExecutorService CONFIG_FILE_EXECUTOR = Executors.newFixedThreadPool(AppSettings.EXECUTOR_THREAD_COUNT.getValue());


    /**
     * Initializes two DotenvConfig instances, one for the base configuration and one for a specific
     * configuration, under the specified aliases.
     *
     * @param baseAlias        The alias for the base configuration.
     * @param baseDotenvConfig The DotenvConfig instance for the base configuration.
     * @param specificAlias    The alias for the specific configuration.
     * @param specificDotEnvConfig The DotenvConfig instance for the specific configuration.
     *
     * @throws IllegalArgumentException if either of the DotenvConfig instances are null.
     * @throws RuntimeException if an error occurs while initializing the configurations.
     */
    public static void initializeDotenvConfigs(
            String baseAlias,
            DotenvConfig baseDotenvConfig,
            String specificAlias,
            DotenvConfig specificDotEnvConfig) {
        try {
            if (baseDotenvConfig == null || specificDotEnvConfig == null) {
                throw new IllegalArgumentException("Dotenv configurations cannot be null");
            }

            DotenvConfigManager.initialize(baseAlias, baseDotenvConfig);
            DotenvConfigManager.initialize(specificAlias, specificDotEnvConfig);

        } catch (Exception error) {
            ErrorHandler.logError(error, "initializeDotenvConfigs",
                    String.format("Failed to initialize configs for aliases: %s and %s", baseAlias, specificAlias));
            throw error;
        }
    }

    /**
     * Loads the environment properties from the specified environment type.
     * <p>
     * The base environment is always loaded first, and then the specific environment is loaded.
     *
     * @param envType The environment type to load (e.g. "dev", "uat", "prod")
     * @return The loaded DotenvConfig instance
     * @throws IOException If an error occurs while loading the environment
     */
    public static DotenvConfig loadEnvironment(String envType) throws IOException {
        try {
            return DotenvConfig.loadEnvironment(getFilePathForEnvType(envType));
        } catch (Exception error) {
            ErrorHandler.logError(error, "loadEnvironment", "Failed to load environment file");
            throw error;
        }
    }



    /**
     * Loads the base environment properties file containing secret keys.
     *
     * @param baseFileEnvPath The path to the base environment properties file.
     * @return
     */
    public static DotenvConfig loadBaseEnvironment(String baseFileEnvPath) {
        try {
            logger.info("Loaded Base environment.");
            return DotenvConfig.loadEnvironment(baseFileEnvPath);
        } catch (Exception error) {
            ErrorHandler.logError(error, "loadBaseEnvironment", "Failed to load environment file");
            throw new RuntimeException("Failed to load environment file", error);
        }
    }

    /**
     * Retrieves the value of the specified environment key from the DotenvConfig instance.
     *
     * @param key The key of the environment variable to retrieve.
     * @return The value of the environment variable or null if not found.
     * @throws IllegalStateException If the DotenvConfig instance is not initialized.
     */
    public static String getEnvironmentKey(String key) {
        try {
            // Access the DotenvConfig instance from DotenvConfigManager
            DotenvConfig dotenvConfig = DotenvConfigManager.getInstance(EnvironmentAliases.UAT.getAliasName());

            return dotenvConfig.getEnvironmentKey(key);
        } catch (Exception error) {
            ErrorHandler.logError(error, "getEnvironmentKey", "Failed to get environment key");
            throw error;
        }
    }


    public static SecretKey getSecretKey(String secretKeyEnvType) {
        try {
            DotenvConfig dotenvConfig = DotenvConfigManager.getInstance(EnvironmentAliases.BASE.getAliasName());
            String secretKeyString = dotenvConfig.getEnvironmentKey(secretKeyEnvType);

            if (secretKeyString == null || secretKeyString.isBlank()) {
                throw new IllegalArgumentException("Secret key for '" + secretKeyEnvType + "' is not defined in the environment.");
            }

            return Base64Utility.decodeSecretKey(secretKeyString);
        } catch (Exception error) {
            ErrorHandler.logError(error, "getSecretKey", "Failed to retrieve secret key for " + secretKeyEnvType);
            throw new RuntimeException("Failed to retrieve secret key for " + secretKeyEnvType, error);
        }
    }


    /**
     * Asynchronously loads all properties configuration files into memory.
     * <p>
     * This method loads configuration files identified by their aliases using
     * {@link ConfigFileAliases} and their respective paths from {@link PropertiesFilePaths}.
     * Both the base and UAT configuration files are loaded concurrently.
     * <p>
     * Logs a success message upon completion or logs an error if any file fails to load.
     *
     * @return A CompletableFuture that completes when all configuration files are loaded
     * successfully, or fails with a CompletionException if an error occurs.
     */
    public static CompletableFuture<Void> loadAllPropertiesConfigFilesAsync() {
        return CompletableFuture.allOf(
                        loadConfigFileAsync(ConfigFileAliases.BASE_FILE.getAliasName(), PropertiesFilePaths.BASE_FILE_PATH.getPath()),
                        loadConfigFileAsync(ConfigFileAliases.UAT_FILE.getAliasName(), PropertiesFilePaths.UAT_FILE_PATH.getPath())
                ).thenRun(() -> logger.info("All properties config files loaded successfully"))
                .exceptionally(error -> {
                    ErrorHandler.logError(error, "loadAllPropertiesConfigFilesAsync", "Failed to load all properties config files");
                    throw new CompletionException(error);
                });
    }

    /**
     * Asynchronously loads a properties file into memory with the given alias name.
     *
     * @param aliasName The alias name for the loaded properties file.
     * @param filePath  The path to the properties file.
     * @return A CompletableFuture that completes if the properties file loads successfully, or fails with an IOException if an error occurs.
     */
    private static CompletableFuture<Void> loadConfigFileAsync(String aliasName, String filePath) {
        try {
            return CompletableFuture.runAsync(() -> {
                try {
                    logger.info("Loading config file: {} for alias: {}", filePath, aliasName);
                    ConfigPropertiesLoader.loadConfigurationFile(aliasName, filePath);
                    logger.info("Successfully loaded config file: {} for alias: {}", filePath, aliasName);
                } catch (IOException error) {
                    logger.error("Failed to load config file: {} for alias: {}", filePath, aliasName, error);
                    throw new CompletionException(error); // Consider rethrowing the original exception if needed
                }
            }, CONFIG_FILE_EXECUTOR);
        } catch (Exception error) {
            ErrorHandler.logError(error, "loadConfigFileAsync", "Failed to load config file");
            throw error;
        }
    }

    /**
     * Retrieves the file path for the specified environment type.
     *
     * @param envType The environment type (e.g. "dev", "uat", "prod").
     * @return The file path for the specified environment type.
     * @throws IllegalArgumentException If the environment type is not supported.
     */
    private static String getFilePathForEnvType(String envType) {
        try {
            return switch (envType.toLowerCase()) {
                case "dev" -> DotEnvFilePaths.DEVELOPMENT_ENV_FILE.getFileName();
                case "uat" -> DotEnvFilePaths.UAT_ENV_FILE.getFileName();
                case "prod" -> DotEnvFilePaths.PRODUCTION_ENV_FILE.getFileName();
                default -> throw new IllegalArgumentException("Unsupported environment type: " + envType);
            };
        } catch (Exception error) {
            ErrorHandler.logError(error, "getFilePathForEnvType", "Failed to get file path for environment type");
            throw error;
        }
    }
}
