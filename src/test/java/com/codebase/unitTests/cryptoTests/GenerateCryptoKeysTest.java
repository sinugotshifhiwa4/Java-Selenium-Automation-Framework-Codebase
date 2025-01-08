package com.codebase.unitTests.cryptoTests;

import com.codebase.crypto.CryptoUtil;
import com.codebase.helpers.Base64Utility;
import com.codebase.helpers.ErrorHandler;
import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;

public class GenerateCryptoKeysTest {

    private static final Logger logger = LoggerUtil.getLogger(GenerateCryptoKeysTest.class);

    @Test
    public void testGenerateIvKey(){
        try{
            byte [] ivKey = CryptoUtil.generateIvKey();
            String encodedIvKey = Base64Utility.encodeArray(ivKey);
            Assert.assertNotNull(encodedIvKey, "ivKey cannot be null");

            logger.info("Encoded Iv Key: {}", encodedIvKey);

        } catch (Exception error){
            ErrorHandler.logError(error, "testGenerateIvKey", "Failed to generate Iv Key");
            throw error;
        }
    }

    @Test
    public void testGenerateSaltKey(){
        try {
            byte [] saltKey = CryptoUtil.generateSaltKey();
            String encodedSaltKey = Base64Utility.encodeArray(saltKey);
            Assert.assertNotNull(encodedSaltKey, "saltKey cannot be null");


            logger.info("Encoded Salt Key: {}", encodedSaltKey);

        } catch (Exception error){
            ErrorHandler.logError(error, "testGenerateSaltKey", "Failed to generate Salt Key");
            throw error;
        }
    }

    @Test
    public void testGenerateSecretKey() {
        try {
            SecretKey secretKey = CryptoUtil.generateSecretKey();
            String encodedSecretKey = Base64Utility.encodeSecretKey(secretKey);
            Assert.assertNotNull(encodedSecretKey, "secretKey cannot be null");

            logger.info("Encoded Secret Key: {}", encodedSecretKey);

        } catch (Exception error){
            ErrorHandler.logError(error, "testGenerateSecretKey", "Failed to generate Secret Key");
            throw error;
        }
    }
}
