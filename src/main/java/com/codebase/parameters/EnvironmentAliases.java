package com.codebase.parameters;

public enum EnvironmentAliases {

    BASE("base_config"),
    DEV("dev_config"),
    UAT("uat_config"),
    PROD("prod_config");

    private final String aliasName;

    EnvironmentAliases(String aliasName) {
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
