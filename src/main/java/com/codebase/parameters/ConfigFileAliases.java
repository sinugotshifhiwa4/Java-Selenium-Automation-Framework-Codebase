package com.codebase.parameters;

/**
 * Enum representing aliases for various properties configuration files.
 */
public enum ConfigFileAliases {

    BASE_FILE("BaseConfig"),          // Base configuration file alias
    DEV_FILE("DevConfigFile"),        // Development configuration file alias
    UAT_FILE("UatConfig"),            // User Acceptance Testing configuration file alias
    PROD_FILE("ProdConfigFile");      // Production configuration file alias

    private final String aliasName;

    ConfigFileAliases(String aliasName) {
        this.aliasName = aliasName;
    }

    /**
     * Gets the string value associated with the alias.
     *
     * @return the string value of the alias
     */
    public String getAliasName() {
        return aliasName;
    }
}