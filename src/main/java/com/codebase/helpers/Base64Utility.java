package com.codebase.helpers;

import com.codebase.crypto.CryptoAlgorithms;
import com.codebase.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.codebase.helpers.ErrorHandler.logError;


public class Base64Utility {

    /**
     * The {@code Base64Utility} class provides utility methods for encoding and decoding data
     * to and from Base64 format. This class supports encoding byte arrays, strings, and secret keys,
     * as well as decoding Base64 encoded data back to its original form.
     * <p>
     * The class is designed as a utility class, with static methods that can be accessed directly
     * without instantiating the class.
     * </p>
     * <p>
     * The methods include validation for null values to ensure data integrity before encoding or decoding.
     * </p>
     *
     * <p><b>Usage Example:</b></p>
     * <pre>
     * String encodedString = Base64Utility.encodeString("Test data");
     * byte[] decodedBytes = Base64Utility.decodeToArray(encodedString);
     * </pre>
     */

    private static final Logger logger = LoggerUtil.getLogger(Base64Utility.class);

    private Base64Utility() {
        logError(
                new UnsupportedOperationException("Utility class cannot be instantiated"),
                "Base64Util Constructor",
                "Attempted to instantiate a utility class.");
    }

    public static String encodeArray(byte[] data) {
        try {
            validateNotNull(data, "Byte input array cannot be null");
            return Base64.getEncoder().encodeToString(data);
        } catch (Exception error) {
            logError(error, "encodeArray", "Failed to encode byte data to base64");
            throw error;
        }
    }

    public static byte[] decodeToArray(String base64String) {
        try {
            validateNotNull(base64String, "Byte input cannot be null");
            return Base64.getDecoder().decode(base64String);
        } catch (Exception error) {
            logError(error, "decodeToArray", "Failed to decode byte data from base64");
            throw error;
        }
    }

    public static String encodeString(String data) {
        try {
            validateNotNull(data, "String input cannot be null");
            return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception error) {
            logError(error, "encodeString", "Failed to encode string data to base64");
            throw error;
        }
    }

    public static String decodeToString(String base64String) {
        try {
            validateNotNull(base64String, "String input cannot be null");
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception error) {
            logError(error, "decodeToString", "Failed to decode string data from base64");
            throw error;
        }
    }

    public static String encodeSecretKey(SecretKey secretKey) {
        try {
            validateNotNull(secretKey, "Secret input key cannot be null");
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception error) {
            logError(error, "encodeSecretKey", "Failed to encode secretKey");
            throw error;
        }
    }

    public static SecretKey decodeSecretKey(String encodedKey) {
        try {
            validateNotNull(encodedKey, "Input encoded key cannot be null");

            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
            return new SecretKeySpec(decodedKey, CryptoAlgorithms.AES.getValue());
        } catch (Exception error) {
            logError(error, "decodeSecretKey", "Failed to decode secretKey");
            throw new RuntimeException("Failed to decode secretKey", error);
        }
    }

    public static void validateNotNull(Object obj, String errorMessage) {
        if (obj == null) {
            logger.error("Validation failed: {} | Error: {}", "Object is null", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
