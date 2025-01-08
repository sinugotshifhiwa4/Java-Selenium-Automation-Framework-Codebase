package com.codebase.parameters;

public enum CryptoParameters {

    KEY_DERIVATION_ITERATIONS(100_000),
    AES_SECRET_KEY_SIZE(256),
    IV_KEY_SIZE(16),
    SALT_KEY_SIZE(32),
    HMAC_KEY_SIZE(32);

    private final int value;

    CryptoParameters(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
