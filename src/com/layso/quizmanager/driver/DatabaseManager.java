package com.layso.quizmanager.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseManager {
	private String databaseURL;
	private String databaseUser;
	private String databasePassword;
	private Connection connection;
	
	
	
	/**
	 * Gets required variables to connect to database and sets member variables
	 * @param url       Database URL to connect
	 * @param user      Username for database
	 * @param password  Password of given username
	 */
	public DatabaseManager(String url, String user, String password) {
		this.databaseURL = url;
		this.databaseUser = user;
		this.databasePassword = password;
	}
	
	
	
	/**
	 * Tries to connect the database. Returns connection status and prints error message to console if occurs
	 * @return  Connection status, true if connected, false if there is an error
	 */
	public boolean Connect() {
		boolean status = true;
		
		
		try {
			connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
			connection = null;
			status = false;
		}
	
		return status;
	}
}
