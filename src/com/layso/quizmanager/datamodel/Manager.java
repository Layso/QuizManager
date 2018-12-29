package com.layso.quizmanager.datamodel;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.DatabaseManager;

import java.util.Scanner;


public class Manager {
	User user;
	DatabaseManager db;
	boolean keepRunning;
	
	
	public Manager(DatabaseManager db) {
		this.db = db;
	}
	
	
	
	
	public void Run() {
		Logger.Log("Program started", Logger.LogType.INFO);
		PrintWelcomeMessage();
		
		
		keepRunning = Authenticate();
		while (keepRunning) {
			do {
				user.PrintUserMenu();
			} while (user.IsRequestValid(InputPrompt("Please select menu: ", false)));
			user.ProcessUserRequest();
		}
	}
	
	
	
	private boolean Authenticate() {
		boolean breakLoop = false;
		boolean result = false;
		
		
		while(!breakLoop) {
			PrintMainMenu();
			switch (InputPrompt("Please select menu [1/2/3/4]: ", false)) {
				case "1":   result = breakLoop = UserLogin(); break;
				case "2":   UserRegister(); break;
				case "3":	System.out.println("Guest entry is not available yet"); break;
				case "4":   breakLoop = true; break;
				default:    System.out.println("Invalid command. Please try again");
			}
		}
		
		return result;
	}
	
	
	
	private boolean UserLogin() {
		String username = InputPrompt("Username: ", false);
		String password = InputPrompt("Password: ", false);
		Logger.Log("User login attempt for Username: " + username + " Password: " + password, Logger.LogType.INFO);
		
		
		// TODO: Check database, return true if credentials match, else return false
		return "username".equals(username) && "password".equals(password);
	}
	
	private void UserRegister() {
		String username = InputPrompt("Username: ", false);
		String password = InputPrompt("Password: ", false);
		boolean normalUser = "".equals(InputPrompt("Select user type [1]Student / [2]Teacher: ", false));
		
		// TODO: Check input validness and save to database if valid, else print error
	}
	
	
	
	/**
	 * This function makes printing a message and getting an input for it easier and tidier
	 * @param message   Message to print before asking for input
	 * @param newLine   Boolean flag to indicate a new line at the end of message
	 * @return          Input of user after the message
	 */
	private String InputPrompt(String message, boolean newLine) {
		Scanner scanner = new Scanner(System.in);
		System.out.print(message + (newLine ? "\n" : ""));
		String input = scanner.nextLine();
		
		return input;
	}
	
	
	
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
