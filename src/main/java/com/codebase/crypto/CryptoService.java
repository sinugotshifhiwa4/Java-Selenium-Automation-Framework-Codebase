package com.codebase.crypto;

import com.codebase.helpers.Base64Utility;
import com.codebase.helpers.ErrorHandler;
import com.codebase.parameters.CryptoParameters;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class CryptoService {

    public static String encrypt(SecretKey secretKey, String dataToEncrypt) throws Exception {
        try {
            // Derive the key using the provided secret key and salt
            byte[] salt = CryptoUtil.generateSaltKey(CryptoParameters.SALT_KEY_SIZE.getValue());
            SecretKeySpec keySpec = CryptoUtil.deriveKey(String.valueOf(secretKey), salt);

            // Generate IV
            byte[] iv = CryptoUtil.generateIvKey(CryptoParameters.IV_KEY_SIZE.getValue());

            byte[] combined = CryptoUtil.encryptData(iv, keySpec, salt, dataToEncrypt);

            // Return Base64-encoded result
            return Base64Utility.encodeArray(combined);

        } catch (Exception error) {
            ErrorHandler.logError(error, "encrypt", "Failed to encrypt data");
            throw error;
        }
    }

    public static String decrypt(SecretKey secretKey, String encryptedData) throws Exception {
        try {
            // decrypt data
            byte[] decryptedData = CryptoUtil.decryptData(secretKey, encryptedData);

            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception error) {
            ErrorHandler.logError(error, "decrypt", "Failed to decrypt data");
            throw error;
        }
    }
}
