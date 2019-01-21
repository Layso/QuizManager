package com.layso.quizmanager.services;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.User;
import com.layso.quizmanager.services.DatabaseManager;

import java.util.Scanner;


public class QuizManager {
	// One instance to rule them all, AKA singleton
	private static QuizManager instance;
	private static User user;
	
	
	/**
	 * Private constructor to prevent object creations
	 */
	private QuizManager() {
		user = null;
	}
	
	
	/**
	 * Interface for user to reach manager
	 *
	 * @return Created instance of QuizManager
	 */
	public static QuizManager getInstance() {
		if (instance == null)
			instance = new QuizManager();
		
		return instance;
	}
	
	
	
	/*
	public void Run() {
		String userInput;
		boolean keepRunning;
		
		
		Logger.Log("Program started", Logger.LogType.INFO);
		PrintWelcomeMessage();
		
		keepRunning = Authenticate();
		while (keepRunning && !user.LoggedOut()) {
			do {
				user.PrintUserMenu();
				userInput = InputPrompt("Please select menu: ", false);
			} while (!user.IsRequestValid(userInput));
			user.ProcessUserRequest(userInput);
		}
	}
	*/
	
	/*
	private boolean Authenticate() {
		boolean breakLoop = false;
		boolean result = false;
		
		
		while(!breakLoop) {
			PrintMainMenu();
			switch (InputPrompt("Please select menu: ", false)) {
				case "1":   result = breakLoop = UserLogin(); if (!result) System.out.println("Invalid user credentials"); break;
				case "2":   UserRegister(); break;
				case "3":	System.out.println("Guest entry is not available yet"); break;
				case "4":   breakLoop = true; break;
				default:    System.out.println("Invalid command. Please try again");
			}
		}
		
		return result;
	}
	*/
	
	/**
	 * Gets input from user to login. Uses database manager to check if credentials are correct
	 *
	 * @return Returns true if login successful, else returns false
	 */
	public static boolean UserLogin(String username, String password) {
		Logger.Log("User login attempt for username: " + username, Logger.LogType.INFO);
		
		
		user = DatabaseManager.getInstance().UserLogin(username, password);
		if (user == null) {
			Logger.Log("Failed login attempt for username: " + username, Logger.LogType.WARNING);
		}
		
		return !(user == null);
	}
	
	
	/**
	 * Gets input of user to create a new user. Transfers data to database manager to try saving to database
	 *
	 * @param username Username for new user
	 * @param password Password for new user
	 */
	public static boolean UserRegister(String username, String password) {
		Logger.Log("User register attempt for username: " + username, Logger.LogType.INFO);
		boolean result;
		
		
		if (DatabaseManager.getInstance().UserExists(username)) {
			result = false;
			Logger.Log("Username \"" + username + "\" already exists in database", Logger.LogType.WARNING);
		} else {
			result = true;
			DatabaseManager.getInstance().UserRegister(username, password);
			Logger.Log("New user \"" + username + "\" successfully inserted to database", Logger.LogType.INFO);
		}
		
		return result;
	}
}