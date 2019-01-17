package com.layso.quizmanager.datamodel;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.DatabaseManager;

import java.util.Scanner;


public class QuizManager {
	private User user;
	
	
	
	public QuizManager() {
		user = null;
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
	 * @return  Returns true if login successful, else returns false
	 *//*
	private boolean UserLogin() {
		String username = InputPrompt("Username: ", false);
		String password = InputPrompt("Password: ", false);
		Logger.Log("User login attempt for username: " + username, Logger.LogType.INFO);
		
		
		user = DatabaseManager.instance.UserLogin(username, password);
		if (user == null) {
			Logger.Log("Failed login attempt for username: " + username, Logger.LogType.WARNING);
		}
		
		return !(user == null);
	}
	*/
	
	
	/**
	 * Gets input from user to create a new user. Transfers data to database manager to try saving to database
	 *//*
	private void UserRegister() {
		String username = InputPrompt("Username: ", false);
		String password = InputPrompt("Password: ", false);
		Logger.Log("User register attempt for username: " + username, Logger.LogType.INFO);
		
		
		if (DatabaseManager.instance.UserExists(username)) {
			Logger.Log("Username \"" + username + "\" already exists in database", Logger.LogType.WARNING);
			System.out.println("User \"" + username + "\" already exists, please try another username");
		}
		
		else {
			DatabaseManager.instance.UserRegister(username, password);
			Logger.Log("New user \"" + username + "\" successfully inserted to database", Logger.LogType.INFO);
			System.out.println("User \"" + username + "\" has successfully created");
		}
	}
	*/
	
	
	/**
	 * This function makes printing a message and getting an input for it easier and tidier
	 * @param message   Message to print before asking for input
	 * @param newLine   Boolean flag to indicate a new line at the end of message
	 * @return          Input of user after the message
	 *//*
	private String InputPrompt(String message, boolean newLine) {
		Scanner scanner = new Scanner(System.in);
		System.out.print(message + (newLine ? "\n" : ""));
		
		return scanner.nextLine();
	}
	*/
	
	
	/**
	 * Prints a welcoming message to make user feel good
	 */
	private void PrintWelcomeMessage() {
		System.out.println("---------------------------------------");
		System.out.println("-----                             -----");
		System.out.println("-----      QUIZ MANAGER 2019      -----");
		System.out.println("-----                             -----");
		System.out.println("---------------------------------------");
	}
	
	
	
	/**
	 * Prints a predefined menu structure to let user know how to navigate through program
	 */
	private void PrintMainMenu() {
		System.out.println();
		System.out.println("[1] User login");
		System.out.println("[2] User registry");
		System.out.println("[3] Guest login");
		System.out.println("[4] Quit");
	}
}
