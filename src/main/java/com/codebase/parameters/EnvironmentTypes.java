package com.codebase.parameters;

public enum EnvironmentTypes {
    BASE("base"),
    DEV("dev"),
    UAT("uat"),
    PROD("prod");

    private final String environment;

    EnvironmentTypes(String environment) {
        this.environment = environment;
    }

    public String getEnvironmentType() {
        return environment;
    }
}
