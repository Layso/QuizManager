package com.layso.quizmanager.services;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.Question;
import com.layso.quizmanager.datamodel.Quiz;
import com.layso.quizmanager.datamodel.User;
import com.layso.quizmanager.services.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class QuizManager {
	// One instance to rule them all, AKA singleton
	private static QuizManager instance;
	
	private User user;
	private List<Question> tempQuiz;
	
	
	/**
	 * Private constructor to prevent object creations
	 */
	private QuizManager() {
		tempQuiz = new ArrayList<>();
		user = null;
	}
	
	
	/**
	 * Interface for user to reach manager
	 *
	 * @return Created instance of QuizManager
	 */
	public static QuizManager getInstance() {
		if (instance == null) {
			instance = new QuizManager();
		}
		
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
	 * Method to save single question to the quiz which is on creation step
	 * @param question Question to be stored
	 */
	public void SaveQuestion(Question question) {
		tempQuiz.add(question);
	}
	
	
	
	/**
	 * Method to save the questions given as a list to the quiz which is on creation step
	 * @param questions List of questions to store
	 */
	public void SaveQuestion(List<Question> questions) {
		for (Question question : questions) {
			tempQuiz.add(question);
		}
	}
	
	
	
	/**
	 * Getter for all the questions saved to create a quiz
	 * @return A list which stores given questions
	 */
	public List<Question> GetTempQuiz() {
		return tempQuiz;
	}
	
	
	
	/**
	 *  Method to clear the temporary question list for next quiz creation
	 */
	public void ClearTempQuiz() {
		tempQuiz.clear();
	}
	
	
	
	/**
	 * Gets input from user to login. Uses database manager to check if credentials are correct
	 *
	 * @return Returns true if login successful, else returns false
	 */
	public boolean UserLogin(String username, String password) {
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
	public boolean UserRegister(String username, String password) {
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
	
	
	
	/**
	 * Getter for currently logged in user
	 * @return currently logged in user
	 */
	public User GetUser() {
		return user;
	}
}