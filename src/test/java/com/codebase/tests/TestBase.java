package com.codebase.tests;

import com.codebase.ConfigPaths.DotEnvFilePaths;
import com.codebase.config.DotenvConfig;
import com.codebase.config.DotenvConfigManager;
import com.codebase.config.TestBaseConfig;
import com.codebase.helpers.ErrorHandler;
import com.codebase.parameters.EnvironmentAliases;
import com.codebase.parameters.EnvironmentTypes;
import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.util.concurrent.CompletionException;

import static com.codebase.config.TestBaseConfig.initializeDotenvConfigs;

public class TestBase {

    private static final Logger logger = LoggerUtil.getLogger(TestBase.class);

    protected DotenvConfig baseDotenvConfig;
    protected DotenvConfig specificDotEnvConfig;


    @BeforeClass
    public void setup() {
        try {
            // Load all configuration properties
            TestBaseConfig.loadAllPropertiesConfigFilesAsync().join();

            // Load environment variables from dotenv files
            baseDotenvConfig = TestBaseConfig.loadBaseEnvironment(DotEnvFilePaths.BASE_ENV_FILE.getFileName());
            specificDotEnvConfig = TestBaseConfig.loadEnvironment(EnvironmentTypes.UAT.getEnvironmentType());

            // Initialize dotenv configurations
            initializeDotenvConfigs(
                    EnvironmentAliases.BASE.getAliasName(), baseDotenvConfig,
                    EnvironmentAliases.UAT.getAliasName(), specificDotEnvConfig);

            logger.info("Setup complete");

        } catch (CompletionException error) {
            ErrorHandler.logError(error, "setup", "Failed to load configuration properties or environment variables");
            throw new RuntimeException("Failed to set up the unit test environment: " + error.getMessage(), error);
        } catch (IOException error) {
            ErrorHandler.logError(error, "setup", "I/O error occurred while loading configuration properties or environment variables");
            throw new RuntimeException("I/O error during setup: " + error.getMessage(), error);
        }
    }

    @AfterClass
    public void tearDown() {
        try {
            // Perform cleanup actions, such as quitting the browser
            System.out.println("Executing tearDown method");
        } catch (Exception error) {
            ErrorHandler.logError(error, "tearDown", "Failed to quit and close the browser");
            throw new RuntimeException("Failed to quit and close the browser: " + error.getMessage(), error);
        }
    }
}
