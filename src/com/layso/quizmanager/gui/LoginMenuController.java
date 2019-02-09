package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.QuizManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;



public class LoginMenuController extends Controller implements Initializable {
	@FXML
	Label loginErrorMessage, registerErrorMessage, loginSuccesfullMessage, registerSuccesfullMessage, loginInfo, registerInfo;
	
	@FXML
	PasswordField loginPassword, registerPassword;
	
	@FXML
	TextField loginUsername, registerUsername;
	
	@FXML
	TabPane navigationTabs;
	
	@FXML
	Button loginButton;
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Logger.Log("Login Menu initialized", Logger.LogType.INFO);
		loginButton.setDefaultButton(true);
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
	
	
	
	public void QuitButton(ActionEvent event) {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
	}
}
