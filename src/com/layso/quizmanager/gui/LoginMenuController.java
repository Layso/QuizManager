package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.CfgManager;
import com.layso.quizmanager.services.QuizManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class LoginMenuController implements Initializable {
	@FXML
	Label loginInfo, registerInfo;
	
	@FXML
	PasswordField loginPassword, registerPassword;
	
	@FXML
	TextField loginUsername, registerUsername;
	
	@FXML
	TabPane navigationTabs;
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Logger.Log("Login Menu initialized", Logger.LogType.INFO);
	}
	
	public void ChangeNavigation(ActionEvent event) {
		for (Tab tab : navigationTabs.getTabs()) {
			if (tab.getId().equals(((Node) event.getSource()).getId())) {
				ClearButton(event);
				navigationTabs.getSelectionModel().select(tab);
			}
		}
	}
	
	public void ChangeScene(ActionEvent event) {
		Stage window = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		double width = window.getWidth(), height = window.getHeight();
		
		
		try {
			Parent mainMenuParent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.main.fxml")));
			Scene mainMenuScene = new Scene(mainMenuParent, width, height);
			window.setScene(mainMenuScene);
		} catch (NullPointerException e) {
			Logger.Log("Fatal Error: FXML filename fetch for main menu failed: " + e.getMessage() + ": Terminating program", Logger.LogType.ERROR);
			System.exit(-1);
		} catch (IOException e) {
			Logger.Log("Fatal Error: GUI replacement for main menu failed: " + e.getMessage() + ": Terminating program", Logger.LogType.ERROR);
			System.exit(-1);
		}
	}
	
	public void LoginButton(ActionEvent event) {
		if (QuizManager.getInstance().UserLogin(loginUsername.getText(), loginPassword.getText())) {
			loginInfo.setText("Logged in successfully!");
			loginInfo.setTextFill(Paint.valueOf("yellow"));
			ChangeScene(event);
		}
		
		else {
			loginInfo.setText("Please check your login information");
			loginInfo.setTextFill(Paint.valueOf("red"));
		}
	}
	
	public void RegisterButton(ActionEvent event) {
		if (QuizManager.getInstance().UserRegister(registerUsername.getText(), registerPassword.getText())) {
			registerInfo.setText("New user successfully registered!");
			registerInfo.setTextFill(Paint.valueOf("yellow"));
		}
		
		else {
			registerInfo.setText("This user already exists");
			registerInfo.setTextFill(Paint.valueOf("red"));
		}
	}
	
	public void ClearButton(ActionEvent event) {
		registerUsername.setText("");
		loginUsername.setText("");
		registerPassword.setText("");
		loginPassword.setText("");
		registerInfo.setText("");
		loginInfo.setText("");
	}
	
	public void QuitButton(ActionEvent event) {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
	}
}
