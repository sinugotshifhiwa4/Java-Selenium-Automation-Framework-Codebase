package com.codebase.unitTests.cryptoTests;

import com.codebase.ConfigPaths.DotEnvFilePaths;
import com.codebase.crypto.CryptoManager;
import com.codebase.helpers.ErrorHandler;
import com.codebase.parameters.SecretKeysParameters;
import com.codebase.tests.TestBase;

import org.testng.annotations.Test;

public class EncryptionTest extends TestBase {

    private static final String getTokenUsername = "TOKEN_USERNAME";
    private static final String getTokenPassword = "TOKEN_PASSWORD";

    @Test
    private void performEncryption() {
        try {
            CryptoManager.encryptMultipleVariables(
                    DotEnvFilePaths.UAT_ENV_FILE.getFileName(), // specify to the environment file
                    SecretKeysParameters.UAT_SECRET_KEY.getValue(), // Specify the environment secret key
                    DotEnvFilePaths.UAT_ENV_FILE.getRelativePath(), // Specify the relative path to the specified environment file,
                    getTokenUsername, getTokenPassword // Specify the environment variables to encrypt
            );

        } catch (Exception error) {
            ErrorHandler.logError(error, "performEncryption", "Failed to perform encryption");
            throw error;
        }
    }
}
