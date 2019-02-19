package com.layso.quizmanager.services;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.Question;
import com.layso.quizmanager.datamodel.Quiz;
import com.layso.quizmanager.datamodel.User;
import com.layso.quizmanager.gui.Controller;
import com.layso.quizmanager.gui.LoginMenuController;
import com.layso.quizmanager.gui.MainMenuController;
import com.layso.quizmanager.services.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class QuizManager {
	// One instance to rule them all, AKA singleton
	private static QuizManager instance;
	
	private boolean keepRunning;
	private Controller.WindowStage currentStage;
	private User user;
	
	
	/**
	 * Private constructor to prevent object creations
	 */
	private QuizManager() {
		user = null;
		keepRunning = true;
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
	
	
	public void Run() {
		currentStage = Controller.WindowStage.LoginMenu;
		
		while (keepRunning) {
			switch (currentStage) {
				case LoginMenu: LoginMenuController.LoginMenu(); break;
				case MainMenu: MainMenuController.MainMenu(); break;
			}
		}
	}
	
	
	public void SetKeepRunning(boolean status) {
		keepRunning = status;
	}
	
	public void SetCurrentStage(Controller.WindowStage stage) {
		currentStage = stage;
	}
	
	
	
	
	public void CreateQuizMenu() {
		while (true) {
		
		}
	}
	
	
	public void EditDeleteQuizMenu() {
		while (true) {
		
		}
	}
	
	
	public void SolveQuizMenu() {
		while (true) {
		
		}
	}
	
	
	public void SeeResultsMenu() {
		while (true) {
		
		}
	}
	
	
	public void CheckAnswerMenu() {
		while (true) {
		
		}
	}
	
	
	public void UserPromotionMenu() {
		while (true) {
		
		}
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
		
		
		if (username.equals("") || password.equals("")) {
			result = false;
			Logger.Log("Username or/and password can't be blank", Logger.LogType.WARNING);
		} else if (DatabaseManager.getInstance().UserExists(username)) {
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