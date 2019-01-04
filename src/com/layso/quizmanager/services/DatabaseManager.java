package com.layso.quizmanager.services;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.Student;
import com.layso.quizmanager.datamodel.Teacher;
import com.layso.quizmanager.datamodel.User;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;



public class DatabaseManager {
	public static DatabaseManager instance;
	
	private Connection connection;
	
	
	/**
	 * No parameter constructor that prepares the singleton instance. If this is first object to be created instance
	 * will be assigned as this object, else nothing will happen
	 */
	public DatabaseManager() {
		if (instance == null) {
			instance = this;
		}
	}
	
	
	
	/**
	 * Connects to database using given credentials and database URL
	 * @param databaseURL       URL to the database for connection
	 * @param databaseUser      Username for database
	 * @param databasePassword  Password for database
	 */
	public void Connect(String databaseURL, String databaseUser, String databasePassword) {
		try {
			connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
			Logger.Log("Database connection established", Logger.LogType.INFO);
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Can not connect to database", Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	/**
	 * Predefined database insert method for user register. Gets credentials and tries to insert to database
	 * @param username  Username for new user
	 * @param password  Password for new user
	 */
	public void UserRegister(String username, String password) {
		String sqlQuery = "insert into USER(USERNAME, PASSWORD, AUTHORITY) values(?, ?, ?)";
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, Boolean.toString(false));
			statement.execute();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to insert new user " + username, Logger.LogType.WARNING);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Predefined database search method for user login. Gets all user information from database and then
	 * checks if given credentials exist on the table
	 * @param username  Username to check
	 * @param password  Password to check
	 * @return          If doesn't exists returns null, else returns a User object according to authority level
	 */
	public User UserLogin(String username, String password) {
		String sqlQuery = "select ID, USERNAME, PASSWORD, AUTHORITY from USER";
		User user = null;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			ResultSet results = statement.executeQuery();
			
			while(results.next()) {
				if (results.getString("USERNAME").equals(username) && results.getString("PASSWORD").equals(password)) {
					int id = Integer.parseInt(results.getString("ID"));
					boolean auth = Boolean.valueOf(results.getString("AUTHORITY"));
					user =  auth ? new Teacher(id, username) : new Student(id, username);
				}
			}
			
		} catch (SQLException e) {
			Logger.Log("Fatal Error: SQL exception caught while trying user login", Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return user;
	}
	
	
	
	/**
	 * Checks if given username exists in database
	 * @param username  Username to check
	 * @return          True if username exists, else false
	 */
	public boolean UserExists(String username) {
		String sqlQuery = "select * from USER where USERNAME=(?)";
		boolean result = false;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, username);
			ResultSet results = statement.executeQuery();
			result = results.next();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: SQL exception caught while trying to search user", Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return result;
	}
}
