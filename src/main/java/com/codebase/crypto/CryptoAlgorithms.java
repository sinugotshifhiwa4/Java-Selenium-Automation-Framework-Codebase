package com.codebase.crypto;

public enum CryptoAlgorithms {

    AES("AES"),
    PBKDF2("PBKDF2WithHmacSHA256"),
    HMAC("HmacSHA256"),
    CIPHER_TRANSFORMATION("AES/CBC/PKCS5Padding");

    private final String value;

    CryptoAlgorithms(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
