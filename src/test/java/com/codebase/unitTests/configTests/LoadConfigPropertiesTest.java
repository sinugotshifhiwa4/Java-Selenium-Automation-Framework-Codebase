package com.codebase.unitTests.configTests;

import com.codebase.config.ConfigPropertiesLoader;
import com.codebase.helpers.ErrorHandler;
import com.codebase.parameters.ConfigFileAliases;
import com.codebase.tests.TestBase;
import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;


public class LoadConfigPropertiesTest extends TestBase {

    private static final Logger logger = LoggerUtil.getLogger(LoadConfigPropertiesTest.class);
    private static final String getProjectName = "PROJECT_NAME";
    private static final String getReportName = "REPORT_NAME";

    @Test
    public void testBaseConfigProperties() {
        try {
            String projectName = ConfigPropertiesLoader.getPropertyKey(
                    ConfigFileAliases.BASE_FILE.getAliasName(),
                    getProjectName);
            String reportName = ConfigPropertiesLoader.getPropertyKey(
                    ConfigFileAliases.BASE_FILE.getAliasName(),
                    getReportName);
            logger.info("Project Name: {}", projectName);
            logger.info("Report Name: {}", reportName);

        } catch (Exception error) {
            ErrorHandler.logError(
                    error,
                    "testBaseConfigProperties",
                    "Failed to load base configuration properties"
            );
            throw new RuntimeException(error);
        }
    }
}
