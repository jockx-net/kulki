package net.jockx.controller;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by JockX on 2014-08-10.
 *
 */
public class PropertiesReader {
	private final Properties properties;
	private static PropertiesReader instance;

	public static PropertiesReader getInstance() {
		if(instance == null){
			try{
				instance = new PropertiesReader();
			} catch (IOException e){
				e.printStackTrace();
			}
		}

		return instance;
	}

	private PropertiesReader() throws IOException {
		properties = new Properties();
		properties.load((getClass().getClassLoader().getResourceAsStream("size.properties")));
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
	}


}
