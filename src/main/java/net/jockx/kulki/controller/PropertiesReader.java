package net.jockx.kulki.controller;

import net.jockx.kulki.util.AppConfigDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PropertiesReader {
    private final Properties properties;
    private static PropertiesReader instance;
    private static final String CONFIG_FILE_NAME = "kulki.properties";

    public static PropertiesReader getInstance() {
        if (instance == null) {
            instance = new PropertiesReader();
        }
        return instance;
    }

    private PropertiesReader() {
        properties = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("size.properties")) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path configFile = AppConfigDir.get().resolve(CONFIG_FILE_NAME);
        if (Files.exists(configFile)) {
            try (InputStream is = Files.newInputStream(configFile)) {
                properties.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        Path configFile = AppConfigDir.get().resolve(CONFIG_FILE_NAME);
        try {
            Files.createDirectories(configFile.getParent());
            try (var os = Files.newOutputStream(configFile)) {
                properties.store(os, "Kulki Game Configuration");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getInt(String propertyName) {
        return Integer.parseInt(getInstance().properties.getProperty(propertyName));
    }

    public static double getDouble(String propertyName) {
        return Double.parseDouble(getInstance().properties.getProperty(propertyName));
    }

    public static String getProperty(String propertyName) {
        return getInstance().properties.getProperty(propertyName);
    }

    public static void setProperty(String propertyName, String propertyValue) {
        getInstance().properties.setProperty(propertyName, propertyValue);
        getInstance().save();
    }
}
