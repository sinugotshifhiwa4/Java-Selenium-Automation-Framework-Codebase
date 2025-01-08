package com.codebase.crypto;

import com.codebase.ConfigPaths.DotEnvFilePaths;
import com.codebase.config.DotenvConfig;
import com.codebase.config.DotenvConfigManager;
import com.codebase.config.TestBaseConfig;
import com.codebase.helpers.Base64Utility;
import com.codebase.helpers.ErrorHandler;
import com.codebase.helpers.FileManager;
import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.codebase.config.TestBaseConfig.loadBaseEnvironment;

public class CryptoManager {

    private static final Logger logger = LoggerUtil.getLogger(CryptoManager.class);

    /**
     * Encrypts multiple environment variables in the specified file.
     *
     * @param envType         The type of environment file (e.g., "dev", "uat", "prod").
     * @param secretKeyEnvType The type of secret key environment variable.
     * @param filePath        The path to the file containing environment variables.
     * @param envVariables    The list of environment variables to encrypt.
     * @throws IOException If an error occurs while writing to the file.
     */
    public static void encryptMultipleVariables(
            String envType,
            String secretKeyEnvType,
            String filePath,
            String... envVariables) {
        try {

            SecretKey secretKey = getEncryptionSecretKey(secretKeyEnvType); // issue here

            for (String envVariable : envVariables) {
                encryptSingleVariable(envType, envVariable, secretKey, filePath);
            }

            logger.info("All specified environment variables encrypted successfully.");
        } catch (Exception error) {
            ErrorHandler.logError(error, "encryptMultipleVariables", "Failed to encrypt multiple variables");
            throw error;
        }
    }

    public static SecretKey getEncryptionSecretKey(String secretKeyEnvType) {
        try {
            DotenvConfig secretKeyEnv = loadBaseEnvironment(DotEnvFilePaths.BASE_ENV_FILE.getFileName());
            SecretKey secretKey = secretKeyEnv.getSecretKey(secretKeyEnvType);
            Base64Utility.validateNotNull(secretKey, "Secret key cannot be null");
            return secretKey;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getSecretKey", "Failed to retrieve secret key");
            throw new RuntimeException("Failed to retrieve secret key", error);
        }
    }

    /**
     * Encrypts a single environment variable in the specified file.
     *
     * @param envType         The type of environment file (e.g., "dev", "uat", "prod").
     * @param envVariable     The environment variable to encrypt.
     * @param secretKey       The secret key to use for encryption.
     * @param filePath        The path to the file containing environment variables.
     */
    private static void encryptSingleVariable(String envType, String envVariable, SecretKey secretKey, String filePath) {
        try {
            String envValue = DotenvConfig.loadEnvironment(envType).getEnvironmentKey(envVariable);

            if (envValue != null) {
                String encryptedValue = CryptoService.encrypt(secretKey, envValue);
                logger.info("Key '{}' encrypted successfully.", envVariable);

                if (encryptedValue != null) {
                    updateEnvVariable(filePath, envVariable, encryptedValue);
                } else {
                    ErrorHandler.logAndThrowError("Failed to save encrypted value for variable: {}", envVariable);
                }
            } else {
                throw new RuntimeException("Environment variable '" + envVariable + "' is null");
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "encryptSingleVariable", "Failed to encrypt variable: " + envVariable);
            throw new RuntimeException("Failed to encrypt variable: " + envVariable, error);
        }
    }

    /**
     * Updates the value of an environment variable in the specified file.
     *
     * @param filePath    The path to the file containing environment variables.
     * @param envVariable The environment variable to update.
     * @param value       The new value for the environment variable.
     * @throws IOException If an error occurs while writing to the file.
     */
    public static void updateEnvVariable(String filePath, String envVariable, String value) {
        Path path = Paths.get(filePath);

        try {
            List<String> lines = Files.readAllLines(path);
            AtomicBoolean isUpdated = new AtomicBoolean(false);

            List<String> updatedLines = lines.stream()
                    .map(line -> {
                        if (line.startsWith(envVariable + "=")) {
                            isUpdated.set(true);
                            return envVariable + "=" + value;
                        }
                        return line;
                    })
                    .collect(Collectors.toList());

            if (!isUpdated.get()) {
                updatedLines.add(envVariable + "=" + value);
            }

            if (!lines.equals(updatedLines)) {
                Files.write(path, updatedLines);
                logger.info("Environment variable '{}' updated in {}", envVariable, filePath);
            }

        } catch (IOException e) {
            ErrorHandler.logError(e, "updateEnvVariable", "Failed to update variable: " + envVariable);
            throw new RuntimeException("Failed to update variable: " + envVariable, e);
        }
    }

    /**
     * Saves the provided secret key to the base environment file specified by the {@link DotEnvFilePaths#BASE_ENV_FILE}
     * constant. The secret key is stored as a Base64-encoded string.
     *
     * @param envVariable The name of the environment variable to store the secret key in.
     * @param encodedSecretKey The Base64-encoded secret key to store.
     * @throws IOException If an error occurs while saving the secret key.
     */
    public static void saveSecretKeyToBaseEnvFile(String envVariable, String encodedSecretKey) throws IOException {
        try {
            // Check if the directory exists; create it if it doesn't
            FileManager.createDirIfNotExists(DotEnvFilePaths.getEnvDirectoryPath());

            // Check if the base environment file exists; create it if it doesn't
            FileManager.createFileIfNotExists(DotEnvFilePaths.getEnvDirectoryPath(), DotEnvFilePaths.BASE_ENV_FILE.getFileName());

            updateEnvVariable(DotEnvFilePaths.BASE_ENV_FILE.getRelativePath(), envVariable, encodedSecretKey);
            logger.info("Secret key saved successfully for variable '{}'", envVariable);
        } catch (Exception error) {
            ErrorHandler.logError(error, "saveSecretKeyToEnvFile", "Failed to save secret key");
            throw error;
        }
    }

    /**
     * Decrypts multiple encrypted environment variables using the provided secret key and returns a list of their decrypted values.
     *
     * @param loadEnvironment The DotenvConfig instance containing the encrypted environment variables.
     * @param secretKey The secret key used to decrypt the environment variables.
     * @param requiredKeys The keys of the environment variables to decrypt.
     * @return A list of decrypted environment variable values.
     * @throws Exception If an error occurs during decryption.
     */
    public static List<String> decryptMultipleKeys(DotenvConfig loadEnvironment, SecretKey secretKey, String... requiredKeys) throws Exception {
        try {
            List<String> decryptedValues = new ArrayList<>();

            for (String key : requiredKeys) {
                String encryptedValue = loadEnvironment.getEnvironmentKey(key);
                String decryptedValue = CryptoService.decrypt(secretKey, encryptedValue);
                decryptedValues.add(decryptedValue);
            }

            return decryptedValues;
        } catch (Exception error){
            ErrorHandler.logError(error, "decryptMultipleKeys", "Failed to decrypt single or multiple keys");
            throw error;
        }
    }
}
