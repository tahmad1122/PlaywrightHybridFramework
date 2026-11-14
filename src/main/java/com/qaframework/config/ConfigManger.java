package com.qaframework.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManger {
	
	/*
	 * static Properties properties;
	 * 
	 * static { try { properties=new Properties(); // FileInputStream file=new
	 * FileInputStream(System.getProperty("user.dir") +
	 * "/src/test/resources/config/qa.properties"); InputStream file =
	 * ConfigManger.class.getClassLoader().getResourceAsStream(
	 * "config/qa.properties"); properties.load(file); } catch (Exception e) {
	 * e.printStackTrace();
	 * System.err.println("Unable to load config.properties file at: "); } }
	 * 
	 * public static String getProperty(String path) { String value =
	 * properties.getProperty(path); if (value == null || value .trim().isEmpty()) {
	 * System.out.println( "Property '" + path +
	 * "' is missing or empty in config.properties"); throw new RuntimeException(
	 * "Property <b>'" + path + "'</b> is missing or empty in config.properties"); }
	 * return value; }
	 */
	
	private static final String CONFIG_FILE = "config/qa.properties";

	static Properties properties;

	static {
		properties = new Properties();
		try (InputStream file = ConfigManger.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
			if (file == null) {
				throw new IllegalStateException("Config file not found on classpath: " + CONFIG_FILE);
			}
			properties.load(file);
		} catch (IOException | IllegalStateException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to load " + CONFIG_FILE + " from classpath", e);
		}
	}
	
	public static String getProperty(String path) {
		String value = properties.getProperty(path);
		if (value  == null || value .trim().isEmpty()) {
	        System.out.println( "Property '" + path + "' is missing or empty in config.properties");
	        throw new RuntimeException( "Property <b>'" + path + "'</b> is missing or empty in config.properties");
	    }
		return value;
	}

	/**
	 * Non-throwing variant for optional config (e.g. groq.apiKey, which is
	 * intentionally left blank in qa.properties and supplied via an
	 * environment variable instead). Returns defaultValue if the key is
	 * missing or blank, instead of failing the whole suite.
	 */
	public static String getPropertyOrDefault(String path, String defaultValue) {
		String value = properties.getProperty(path);
		return (value == null || value.trim().isEmpty()) ? defaultValue : value;
	}
}

