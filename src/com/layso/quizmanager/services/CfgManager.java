package com.layso.quizmanager.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;



public class CfgManager {
	// One instance to rule them all, aka singleton
	private static CfgManager instance;
	
	private Properties properties;
	
	
	
	/**
	 * A private constructor to prevent calls from different parts of code
	 * @param fileName Configuration file name
	 */
	private CfgManager(String fileName) {
		this.properties = new Properties();
		
		try (InputStream stream = new FileInputStream(new File(fileName))) {
			properties.load(stream);
		} catch (IOException e) {
			System.out.println("Program configuration failed. Cause:\n" + e.getMessage() + "\nTerminating program");
			System.exit(1);
		}
	}
	
	
	
	/**
	 * An interface to force users instantiate singleton with parameters
	 * @param fileName Configuration file name
	 */
	public static void Setup(String fileName) {
		if (instance == null)
			instance = new CfgManager(fileName);
	}
	
	
	
	/**
	 * Getter for singleton
	 * @return Returns the instance
	 */
	public static CfgManager getInstance() {
		if (instance == null){
			System.out.println("No instance created. Please call Setup() before getting instance\nTerminating program");
			System.exit(1);
		}
		
		return instance;
	}
	
	
	
	/**
	 * Interface for user to reach desired value of a property key
	 * @param key                   Key to search in property file
	 * @return                      Associated value of the key parameter
	 * @throws NullPointerException Throws exception if key is not found
	 */
	public String Get(String key) throws NullPointerException {
		return properties.getProperty(key);
	}
}
