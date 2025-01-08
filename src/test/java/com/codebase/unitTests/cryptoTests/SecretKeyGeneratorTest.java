package com.codebase.unitTests.cryptoTests;

import com.codebase.crypto.CryptoManager;
import com.codebase.crypto.CryptoUtil;
import com.codebase.helpers.Base64Utility;
import com.codebase.helpers.ErrorHandler;
import com.codebase.parameters.SecretKeysParameters;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
import java.io.IOException;

public class SecretKeyGeneratorTest {


    @Test
    public void testGenerateAndStoreSecretKey() throws IOException {
        try {
            // Generate a secret key
            SecretKey secretKey = CryptoUtil.generateSecretKey();

            // Save the secret key to base environment file
            CryptoManager.saveSecretKeyToBaseEnvFile(
                    SecretKeysParameters.UAT_SECRET_KEY.getValue(),
                    Base64Utility.encodeSecretKey(secretKey)
            );

        } catch (IOException error) {
            ErrorHandler.logError(error, "testGenerateAndStoreSecretKey", "Failed to generate and store secret key");
            throw error;
        }
    }
}
