package com.codebase.parameters;

public enum SecretKeysParameters {

    DEV_SECRET_KEY("DEVELOPMENT_SECRET_KEY"),
    UAT_SECRET_KEY("UAT_SECRET_KEY"),
    PROD_SECRET_KEY("PRODUCTION_SECRET_KEY");

    private final String parameter;

    SecretKeysParameters(String parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return parameter;
    }
}
