package com.codebase.unitTests.configTests;

import com.codebase.config.TestBaseConfig;
import com.codebase.helpers.ErrorHandler;
import com.codebase.tests.TestBase;
import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class LoadDotenvTest extends TestBase {

    private static final Logger logger = LoggerUtil.getLogger(LoadDotenvTest.class);
    private static final String getTokenUsername = "TOKEN_USERNAME";
    private static final String getTokenPassword = "TOKEN_PASSWORD";

    @Test
    public void testDotenvLoading(){
        getUatDotenvVariables();
    }

    private void getUatDotenvVariables() {
        try{

            String tokenUsername = TestBaseConfig.getEnvironmentKey(getTokenUsername);
            String tokenPassword = TestBaseConfig.getEnvironmentKey(getTokenPassword);

            logger.info("Token username: {}", tokenUsername);
            logger.info("Token password: {}", tokenPassword);

        } catch (Exception error){
            ErrorHandler.logError(error, "getDotenvVariables", "Failed to get dotenv variables");
            throw error;
        }
    }
}
