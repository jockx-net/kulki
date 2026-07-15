package net.jockx.kulki.util;

import java.nio.file.Path;

public final class PlatformUtil {

    private static final String APP_NAME = "Kulki";

    private PlatformUtil() {
    }

    private static boolean isAndroid() {
        return System.getenv("ANDROID_ROOT") != null;
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static String getDefaultPlayerName() {
        return isAndroid()
            ? "Player"
            : System.getProperty("user.name", "Player");
    }

    public static Path getAppConfigDir() {
        String userHome = System.getProperty("user.home");
        Path homeDotDir = Path.of(userHome, ".kulki");
        Path configDir;
        if (isAndroid()) {
            configDir = homeDotDir;
        } else if (isWindows()) {
            String appData = System.getenv("APPDATA");
            configDir = appData != null
                ? Path.of(appData, APP_NAME)
                : homeDotDir;
        } else {
            String configHome = System.getenv("XDG_CONFIG_HOME");
            configDir = configHome != null
                ? Path.of(configHome, APP_NAME.toLowerCase())
                : Path.of(userHome, ".config", APP_NAME.toLowerCase());
        }
        return configDir;
    }
}
