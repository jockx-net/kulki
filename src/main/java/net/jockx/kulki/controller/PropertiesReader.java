package net.jockx.kulki.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
	private final Properties properties;
	private static PropertiesReader instance;
	private static final String CONFIG_FILE_NAME = "kulki.properties";

	public static PropertiesReader getInstance() {
		if(instance == null){
			instance = new PropertiesReader();
		}

		return instance;
	}

	private PropertiesReader() {
		properties = new Properties();
		// Load defaults from classpath
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("size.properties")) {
			if (is != null) {
				properties.load(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Load overrides from local storage
		File configFile = getConfigFile();
		if (configFile.exists()) {
			try (FileInputStream fis = new FileInputStream(configFile)) {
				properties.load(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private File getConfigFile() {
		String userHome = System.getProperty("user.home");
		String os = System.getProperty("os.name").toLowerCase();
		File configDir;

		if (os.contains("win")) {
			String appData = System.getenv("APPDATA");
			if (appData != null) {
				configDir = new File(appData, "Kulki");
			} else {
				configDir = new File(userHome, ".kulki");
			}
		} else if (os.contains("android")) {
			// For Android, we typically use the files directory.
			// Since we don't have access to Android Context here easily without passing it,
			// we'll use a hidden folder in user.home as a fallback.
			configDir = new File(userHome, ".kulki");
		} else {
			// Linux (including Flatpak) and others
			String configHome = System.getenv("XDG_CONFIG_HOME");
			if (configHome != null) {
				configDir = new File(configHome, "kulki");
			} else {
				configDir = new File(userHome, ".config/kulki");
			}
		}

		if (!configDir.exists()) {
			configDir.mkdirs();
		}
		return new File(configDir, CONFIG_FILE_NAME);
	}

	public void save() {
		File configFile = getConfigFile();
		try (FileOutputStream fos = new FileOutputStream(configFile)) {
			properties.store(fos, "Kulki Game Configuration");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getInt(String propertyName){
		return Integer.parseInt(getInstance().properties.getProperty(propertyName));
	}

	public static double getDouble(String propertyName){
		return Double.parseDouble(getInstance().properties.getProperty(propertyName));
	}

	public static String getProperty (String propertyName){
		return getInstance().properties.getProperty(propertyName);
	}

	public static void setProperty(String propertyName, String propertyValue){
		getInstance().properties.setProperty(propertyName, propertyValue);
		getInstance().save();
	}
}
