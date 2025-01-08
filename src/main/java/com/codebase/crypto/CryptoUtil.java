package com.codebase.crypto;

import com.codebase.helpers.Base64Utility;
import com.codebase.helpers.ErrorHandler;
import com.codebase.parameters.CryptoParameters;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.KeySpec;

public class CryptoUtil {


    public static byte[] generateIvKey(int size) {
        return generateRandomBytes(size);
    }


    public static byte[] generateIvKey() {
        return generateRandomBytes(CryptoParameters.IV_KEY_SIZE.getValue());
    }

    /**
     * Generates a random salt with the specified size.
     *
     * @param size the size of the salt in bytes.
     * @return a byte array representing the salt.
     */
    public static byte[] generateSaltKey(int size) {
        return generateRandomBytes(size);
    }


    public static byte[] generateSaltKey() {
        return generateRandomBytes(CryptoParameters.SALT_KEY_SIZE.getValue());
    }

    /**
     * Generates a random secret key with the specified size.
     *
     * @param size the size of the key in bytes.
     * @return a {@link SecretKey} object.
     */
    public static SecretKey generateSecretKey(int size) {
        byte[] keyBytes = generateRandomBytes(size);
        return new SecretKeySpec(keyBytes, CryptoAlgorithms.AES.getValue());
    }

    public static SecretKey generateSecretKey() {
        return generateSecretKey(CryptoParameters.AES_SECRET_KEY_SIZE.getValue());
    }

    /**
     * Generates random bytes of the specified size.
     *
     * @param size the size of the byte array.
     * @return a byte array of random bytes.
     */
    private static byte[] generateRandomBytes(int size) {
        try {
            byte[] randomBytes = new byte[size];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(randomBytes);
            return randomBytes;
        } catch (Exception error) {
            ErrorHandler.logError(error, "generateRandomBytes", "Failed to generate random bytes");
            throw new RuntimeException("Error generating random bytes", error);
        }
    }

    /**
     * Derives a key from a given secret and salt using PBKDF2.
     *
     * @param secretKey the secret key as a string.
     * @param salt      the salt as a byte array.
     * @return a {@link SecretKeySpec} object.
     * @throws GeneralSecurityException if key derivation fails.
     */
    public static SecretKeySpec deriveKey(String secretKey, byte[] salt) throws GeneralSecurityException {
        if (secretKey.length() < 32) {
            throw new IllegalArgumentException("Secret key must be at least 32 characters long.");
        }

        KeySpec spec = new PBEKeySpec(
                secretKey.toCharArray(),
                salt,
                CryptoParameters.KEY_DERIVATION_ITERATIONS.getValue(),
                CryptoParameters.AES_SECRET_KEY_SIZE.getValue()
        );

        SecretKeyFactory factory = SecretKeyFactory.getInstance(CryptoAlgorithms.PBKDF2.getValue());
        byte[] key = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, CryptoAlgorithms.AES.getValue());
    }

    private static byte[] generateMac(byte[] salt, byte[] iv, byte[] cipherText, byte[] key) throws Exception {
        try {
            String hmacAlgorithm = CryptoAlgorithms.HMAC.getValue();
            Mac mac = Mac.getInstance(hmacAlgorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, hmacAlgorithm);
            mac.init(secretKeySpec);

            // Concatenate salt, iv, and cipherText
            byte[] data = new byte[salt.length + iv.length + cipherText.length];
            System.arraycopy(salt, 0, data, 0, salt.length);
            System.arraycopy(iv, 0, data, salt.length, iv.length);
            System.arraycopy(cipherText, 0, data, salt.length + iv.length, cipherText.length);

            // Generate the MAC
            return mac.doFinal(data); // Return the raw MAC bytes
        } catch (Exception error) {
            ErrorHandler.logError(error, "generateMac", "An error occurred while generating MAC.");
            throw error;
        }
    }

    private static byte[] combineComponents(byte[] salt, byte[] iv, byte[] encryptedBytes, byte[] mac) {
        try {
            byte[] combined = new byte[salt.length + iv.length + encryptedBytes.length + mac.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(iv, 0, combined, salt.length, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, salt.length + iv.length, encryptedBytes.length);
            System.arraycopy(mac, 0, combined, salt.length + iv.length + encryptedBytes.length, mac.length);
            return combined;
        } catch (Exception error) {
            ErrorHandler.logError(error, "combineComponents", "An error occurred while combining components");
            throw error;
        }
    }

    private static void verifyMac(byte[] salt, byte[] iv, byte[] cipherText, byte[] mac, SecretKeySpec keySpec) throws Exception {
        try {
            byte[] computedMac = CryptoUtil.generateMac(salt, iv, cipherText, keySpec.getEncoded());
            if (!MessageDigest.isEqual(mac, computedMac)) throw new SecurityException("MAC validation failed. Data may have been tampered with.");
        } catch (SecurityException error) {
            ErrorHandler.logError(error, "decrypt", "Failed to decrypt data");
            throw error;
        }
    }


    public static Cipher initializeCipher(byte[] iv, SecretKeySpec keySpec, int mode) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(CryptoAlgorithms.CIPHER_TRANSFORMATION.getValue());
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(mode, keySpec, ivSpec);
            return cipher;
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeyException |
                 NoSuchAlgorithmException error) {
            ErrorHandler.logError(error, "initializeCipher", "Failed to initialize Cipher");
            throw error;
        }
    }

public static byte[] encryptData(byte[] iv, SecretKeySpec keySpec, byte[] salt, String dataToEncrypt) throws Exception {
    try {
        // Initialize the cipher for encryption
        Cipher cipher = CryptoUtil.initializeCipher(iv, keySpec, Cipher.ENCRYPT_MODE);

        // Encrypt the data
        byte[] encryptedBytes = cipher.doFinal(dataToEncrypt.getBytes(StandardCharsets.UTF_8));

        // Generate MAC
        byte[] mac = CryptoUtil.generateMac(salt, iv, encryptedBytes, keySpec.getEncoded());

        // Combine salt, IV, encrypted data, and MAC
        return CryptoUtil.combineComponents(salt, iv, encryptedBytes, mac);
    } catch (IllegalBlockSizeException | BadPaddingException error) {
        ErrorHandler.logError(error, "initializeEncryption", "Failed to initialize encryption");
        throw error;
    }
}

    public static byte[] decryptData(SecretKey secretKey, String encryptedData) throws Exception {
        try {
            byte[] combined = Base64Utility.decodeToArray(encryptedData);

            // Get key sizes
            int saltSize = CryptoParameters.SALT_KEY_SIZE.getValue();
            int ivSize = CryptoParameters.IV_KEY_SIZE.getValue();
            int macSize = CryptoParameters.HMAC_KEY_SIZE.getValue();
            int cipherTextSize = combined.length - saltSize - ivSize - macSize;

            byte[] salt = new byte[saltSize];
            byte[] iv = new byte[ivSize];
            byte[] mac = new byte[macSize];
            byte[] cipherText = new byte[cipherTextSize];

            System.arraycopy(combined, 0, salt, 0, saltSize);
            System.arraycopy(combined, saltSize, iv, 0, ivSize);
            System.arraycopy(combined, saltSize + ivSize, cipherText, 0, cipherTextSize);
            System.arraycopy(combined, saltSize + ivSize + cipherTextSize, mac, 0, macSize);

            // Derive the key using the secret key string
            SecretKeySpec keySpec = CryptoUtil.deriveKey(String.valueOf(secretKey), salt);

            // Verify the MAC
            verifyMac(salt, iv, cipherText, mac, keySpec);

            // Initialize the cipher for decryption
            Cipher cipher = CryptoUtil.initializeCipher(iv, keySpec, Cipher.DECRYPT_MODE);
            return cipher.doFinal(cipherText);
        } catch (Exception error) {
            ErrorHandler.logError(error, "decryptData", "Failed to decrypt data");
            throw error;
        }
    }
}
