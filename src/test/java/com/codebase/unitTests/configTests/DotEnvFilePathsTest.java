package com.codebase.unitTests.configTests;

import com.codebase.helpers.ErrorHandler;
import com.codebase.tests.TestBase;
import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class DotEnvFilePathsTest extends TestBase {

    private static final Logger logger = LoggerUtil.getLogger(DotEnvFilePathsTest.class);

    @Test
    public void testEnvFilePaths() throws Exception {
        logEnvFilePaths(
                com.codebase.ConfigPaths.DotEnvFilePaths.BASE_ENV_FILE.getFileName(),
                com.codebase.ConfigPaths.DotEnvFilePaths.DEVELOPMENT_ENV_FILE.getFileName(),
                com.codebase.ConfigPaths.DotEnvFilePaths.UAT_ENV_FILE.getFileName(),
                com.codebase.ConfigPaths.DotEnvFilePaths.PRODUCTION_ENV_FILE.getFileName());
    }

    @Test
    public void testEnvFileRelativePaths() throws Exception {
        logEnvFileRelativePaths(com.codebase.ConfigPaths.DotEnvFilePaths.BASE_ENV_FILE.getRelativePath(),
                com.codebase.ConfigPaths.DotEnvFilePaths.DEVELOPMENT_ENV_FILE.getRelativePath(),
                com.codebase.ConfigPaths.DotEnvFilePaths.UAT_ENV_FILE.getRelativePath(),
                com.codebase.ConfigPaths.DotEnvFilePaths.PRODUCTION_ENV_FILE.getRelativePath());
    }

    private void logEnvFilePaths(String baseEnvFile, String devEnvFile, String uatEnvFile, String prodEnvFile) throws Exception {
        try {
            logger.info("Env Directory: {}", com.codebase.ConfigPaths.DotEnvFilePaths.getEnvDirectoryPath());
            logger.info("Base Env File: {}", baseEnvFile);
            logger.info("Dev Env File: {}", devEnvFile);
            logger.info("UAT Env File: {}", uatEnvFile);
            logger.info("Prod Env File: {}", prodEnvFile);
        } catch (Exception error) {
            handleError(error, "logEnvFilePaths", "Failed to log environment file paths");
        }
    }

    private void logEnvFileRelativePaths(String baseEnvPath, String devEnvPath, String uatEnvPath, String prodEnvPath) throws Exception {
        try {
            logger.info("Base Environment File Path: '{}'", baseEnvPath);
            logger.info("Development Environment File Path: '{}'", devEnvPath);
            logger.info("UAT Environment File Path: '{}'", uatEnvPath);
            logger.info("Production Environment File Path: '{}'", prodEnvPath);
        } catch (Exception error) {
            handleError(error, "logEnvFileRelativePaths", "Failed to log environment file relative paths");
        }
    }

    private void handleError(Exception error, String methodName, String message) throws Exception {
        ErrorHandler.logError(error, methodName, message);
        throw error; // Rethrow the exception to fail the test
    }
}