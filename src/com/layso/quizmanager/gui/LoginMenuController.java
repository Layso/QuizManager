package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.QuizManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;



public class LoginMenuController extends Controller implements Initializable {
	// GUI elements
	@FXML
	Label loginErrorMessage, registerErrorMessage, loginSuccesfullMessage, registerSuccesfullMessage;
	
	@FXML
	PasswordField loginPassword, registerPassword;
	
	@FXML
	TextField loginUsername, registerUsername;
	
	@FXML
	TabPane navigationTabs;
	
	@FXML
	Button loginButton;
	
	
	
	/**
	 * Overriding initialize method to setup stage
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Logger.Log("Login Menu initialized", Logger.LogType.INFO);
		loginButton.setDefaultButton(true);
	}
	
	
	
	/**
	 * Method to start LoginMenu on console
	 */
	public static void LoginMenu() {
		boolean run = true;
		boolean correctInput;
		
		
		while (run) {
			PrintMenu("Login", "Register", "Guest Login", "Quit");
			
			do {
				switch (Controller.GetMenuInput()) {
					case 1: correctInput = true; run = Login(); break;
					case 2: correctInput = true; Register(); break;
					case 3: correctInput = true; System.out.println("Guest login currently unavailable"); break;
					case 4: correctInput = true; run = false; QuizManager.getInstance().SetKeepRunning(false); break;
					default: correctInput = false;
				}
			} while (!correctInput);
		}
	}
	
	
	
	/**
	 * Helper menu to create new user
	 */
	public static void Register() {
		// Get inputs
		String username = GetInput("Username for register", false);
		String password = GetInput("Password for register", true);
		
		// Try to save to database
		if (QuizManager.getInstance().UserRegister(username, password)) {
			System.out.println("New user " + username + " has successfully registered");
		}
		
		else {
			System.out.println("Register failed");
		}
	}
	
	
	
	/**
	 * Helper menu to login
	 * @return  Returns the success status of login
	 */
	public static boolean Login() {
		boolean success;
		
		
		// Get input
		String username = GetInput("Username for login", false);
		String password = GetInput("Password for login", false);
		success = QuizManager.getInstance().UserLogin(username, password);
		
		// Try to log in
		if (success) {
			System.out.println(username + " has successfully logged in");
			QuizManager.getInstance().SetCurrentStage(WindowStage.MainMenu);
		}
		
		else {
			System.out.println("Login failed");
		}
		
		return !success;
	}
	
	
	
	/**
	 * Method to change menu tabs
	 * @param event ActionEvent produced by GUI
	 */
	public void ChangeNavigation(ActionEvent event) {
		ChangeNavigation(event, navigationTabs);
		ClearButton(event);
	}
	
	
	
	/**
	 * Gets user credentials to try logging in to the system. Loads (user) main menu if successful, shows error message
	 * if failed
	 * @param event ActionEvent produced by GUI
	 */
	public void LoginButton(ActionEvent event) {
		if (QuizManager.getInstance().UserLogin(loginUsername.getText(), loginPassword.getText())) {
			loginSuccesfullMessage.setVisible(true);
			ChangeScene(event, WindowStage.MainMenu);
		}
		
		else {
			loginErrorMessage.setVisible(true);
		}
	}
	
	
	
	/**
	 * Gets user credentials to try saving new user to database. Shows success message if successful, shows error message
	 * if failed
	 * @param event ActionEvent produced by GUI
	 */
	public void RegisterButton(ActionEvent event) {
		if (QuizManager.getInstance().UserRegister(registerUsername.getText(), registerPassword.getText())) {
			registerSuccesfullMessage.setVisible(true);
		}
		
		else {
			registerErrorMessage.setVisible(true);
		}
	}
	
	
	
	/**
	 * Clears all text fields and sets display property of error messages to false
	 * @param event ActionEvent produced by GUI
	 */
	public void ClearButton(ActionEvent event) {
		ClearTextFields(registerUsername, loginUsername, registerPassword, loginPassword);
		
		registerSuccesfullMessage.setVisible(false);
		loginSuccesfullMessage.setVisible(false);
		registerErrorMessage.setVisible(false);
		loginErrorMessage.setVisible(false);
	}
	
	
	
	/**
	 * Quit button action to close the application
	 * @param event ActionEvent created by GUI
	 */
	public void QuitButton(ActionEvent event) {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
	}
}
