package com.codebase.ConfigPaths;

public enum DotEnvFilePaths {
    BASE_ENV_FILE(".env"),
    DEVELOPMENT_ENV_FILE(".env.dev"),
    UAT_ENV_FILE(".env.uat"),
    PRODUCTION_ENV_FILE(".env.prod");

    // Directory Path
    public static final String ENV_DIRECTORY = "envs";

    private final String fileName;

    DotEnvFilePaths(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getRelativePath() {
        return ENV_DIRECTORY + "/" + fileName;
    }

    public static String getEnvDirectoryPath() {
        return ENV_DIRECTORY;
    }
}