package com.codebase.ConfigPaths;

public enum PropertiesFilePaths {

    BASE_FILE_PATH("src/main/resources/base-config.properties"),
    DEV_FILE_PATH("src/main/resources/dev-config.properties"),
    UAT_FILE_PATH("src/main/resources/uat-config.properties");

    private final String path;

    PropertiesFilePaths(String path) {
        this.path = path;
    }

    /**
     * Gets the file path associated with the enum constant.
     *
     * @return the file path as a string
     */
    public String getPath() {
        return path;
    }
}
