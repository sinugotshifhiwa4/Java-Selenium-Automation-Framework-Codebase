package com.codebase.unitTests.cryptoTests;

import com.codebase.config.TestBaseConfig;
import com.codebase.crypto.CryptoManager;
import com.codebase.crypto.CryptoService;
import com.codebase.helpers.ErrorHandler;
import com.codebase.parameters.SecretKeysParameters;
import com.codebase.tests.TestBase;
import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.util.List;

import static com.codebase.config.TestBaseConfig.getSecretKey;


public class DecryptionTest extends TestBase {

    private static final Logger logger = LoggerUtil.getLogger(DecryptionTest.class);
    private static final String getTokenUsername = "TOKEN_USERNAME";
    private static final String getTokenPassword = "TOKEN_PASSWORD";

    @Test
    public void testDecryptCredentials() throws Exception {
        try {
            decryptMultipleCredentials();
            decryptSingleCredential();
        } catch (Exception error) {
            ErrorHandler.logError(error, "testDecryptCredentials", "Failed to decrypt credentials");
            throw error;
        }
    }


    private void decryptMultipleCredentials() throws Exception {
        try {
            List<String> decryptedValues =
                    CryptoManager.decryptMultipleKeys(
                            specificDotEnvConfig,
                            TestBaseConfig.getSecretKey(SecretKeysParameters.UAT_SECRET_KEY.getValue()),
                            getTokenUsername, getTokenPassword
                    );

            logger.info(String.join("\n", decryptedValues));

        } catch (Exception error) {
            ErrorHandler.logError(error, "decryptCredentials", "Failed to decrypt credentials");
            throw error;
        }
    }

    private void decryptSingleCredential() throws Exception {
        try {
            // Single key
            String password = CryptoService.decrypt(
                    getSecretKey(SecretKeysParameters.UAT_SECRET_KEY.getValue()),
                    specificDotEnvConfig.getEnvironmentKey(getTokenPassword)
            );
            logger.info("Decrypted Single password: {}", password);

        } catch (Exception error) {
            ErrorHandler.logError(error, "decryptCredentials", "Failed to decrypt credentials");
            throw error;
        }
    }
}
