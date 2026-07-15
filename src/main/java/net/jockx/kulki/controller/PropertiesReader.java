package net.jockx.kulki.controller;

import net.jockx.kulki.util.PlatformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PropertiesReader {
    private static final Logger log = LoggerFactory.getLogger(PropertiesReader.class);
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
        try (InputStream is = openResource("size.properties")) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e) {
            log.warn("Failed to load size.properties, using empty defaults", e);
        }

        Path configFile = PlatformUtil.getAppConfigDir().resolve(CONFIG_FILE_NAME);
        if (Files.exists(configFile)) {
            try (InputStream in = Files.newInputStream(configFile)) {
                properties.load(in);
            } catch (IOException e) {
                log.warn("Failed to read user config, using defaults", e);
            }
        }
    }

    public void save() {
        Path configFile = PlatformUtil.getAppConfigDir().resolve(CONFIG_FILE_NAME);
        try {
            Files.createDirectories(configFile.getParent());
            try (var os = Files.newOutputStream(configFile)) {
                properties.store(os, "Kulki Game Configuration");
            }
        } catch (IOException e) {
            log.warn("Failed to save user config", e);
        }
    }

    public static int getInt(String propertyName) {
        return Integer.parseInt(getInstance().properties.getProperty(propertyName));
    }

    private static InputStream openResource(String name) {
        InputStream is = PropertiesReader.class.getResourceAsStream("/" + name);
        if (is == null) {
            is = PropertiesReader.class.getClassLoader().getResourceAsStream(name);
        }
        if (is == null) {
            is = ClassLoader.getSystemResourceAsStream(name);
        }
        if (is == null) {
            try {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
            } catch (SecurityException e) {
                // ignore
            }
        }
        return is;
    }

    public static String getProperty(String propertyName) {
        return getInstance().properties.getProperty(propertyName);
    }

    public static void setProperty(String propertyName, String propertyValue) {
        getInstance().properties.setProperty(propertyName, propertyValue);
        getInstance().save();
    }
}
