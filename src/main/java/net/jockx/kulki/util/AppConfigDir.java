package net.jockx.kulki.util;

import java.nio.file.Path;

public final class AppConfigDir {
    private static final String APP_NAME = "Kulki";

    private AppConfigDir() {
    }

    public static Path get() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        Path configDir;
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            configDir = appData != null ? Path.of(appData, APP_NAME) : Path.of(userHome, ".kulki");
        } else if (os.contains("android")) {
            configDir = Path.of(userHome, ".kulki");
        } else {
            String configHome = System.getenv("XDG_CONFIG_HOME");
            configDir = configHome != null
                    ? Path.of(configHome, APP_NAME.toLowerCase())
                    : Path.of(userHome, ".config", APP_NAME.toLowerCase());
        }
        return configDir;
    }
}
