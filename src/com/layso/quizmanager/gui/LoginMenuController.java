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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginMenuController implements Initializable {
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		System.out.println("Login Menu Init");
	}
	
	public void ChangeNavigation(ActionEvent event) {
		final String tabPaneId = "#navigationTabs";
		ToggleButton toggleButton = ((ToggleButton) event.getSource());
		TabPane tp_tabs = ((TabPane) toggleButton.getScene().lookup(tabPaneId));
		
		
		for (Tab tab : tp_tabs.getTabs()) {
			if (tab.getId().equals(((Node) event.getSource()).getId())) {
				ClearButton(event);
				tp_tabs.getSelectionModel().select(tab);
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
		final String loginUsernameId = "#loginUsername";
		final String loginPasswordId = "#loginPassword";
		final String loginInfoId = "#loginInfo";
		
		
		Button bt_login = ((Button) event.getSource());
		TextField tf_login = ((TextField) bt_login.getScene().lookup(loginUsernameId));
		PasswordField pf_login = ((PasswordField) bt_login.getScene().lookup(loginPasswordId));
		Label txt_loginInfo = ((Label) bt_login.getScene().lookup(loginInfoId));
		
		if (QuizManager.getInstance().UserLogin(tf_login.getText(), pf_login.getText())) {
			txt_loginInfo.setText("Logged in successfully!");
			txt_loginInfo.setTextFill(Paint.valueOf("yellow"));
			ChangeScene(event);
		}
		
		else {
			txt_loginInfo.setText("Please check your login information");
			txt_loginInfo.setTextFill(Paint.valueOf("red"));
		}
	}
	
	public void RegisterButton(ActionEvent event) {
		final String registerUsernameId = "#registerUsername";
		final String registerPasswordId = "#registerPassword";
		final String registerInfoId = "#registerInfo";
		
		
		Button bt_register = ((Button) event.getSource());
		TextField tf_register = ((TextField) bt_register.getScene().lookup(registerUsernameId));
		PasswordField pf_register = ((PasswordField) bt_register.getScene().lookup(registerPasswordId));
		Label txt_registerInfo = ((Label) bt_register.getScene().lookup(registerInfoId));
		
		if (QuizManager.getInstance().UserRegister(tf_register.getText(), pf_register.getText())) {
			txt_registerInfo.setText("New user successfully registered!");
			txt_registerInfo.setTextFill(Paint.valueOf("yellow"));
		}
		
		else {
			txt_registerInfo.setText("This user already exists");
			txt_registerInfo.setTextFill(Paint.valueOf("red"));
		}
	}
	
	public void ClearButton(ActionEvent event) {
		final String registerUsernameId = "#registerUsername";
		final String registerPasswordId = "#registerPassword";
		final String registerInfoId = "#registerInfo";
		final String loginUsernameId = "#loginUsername";
		final String loginPasswordId = "#loginPassword";
		final String loginInfoId = "#loginInfo";
		
		
		Node node = ((Node) event.getSource());
		TextField tf_register = ((TextField) node.getScene().lookup(registerUsernameId));
		TextField tf_login = ((TextField) node.getScene().lookup(loginUsernameId));
		
		PasswordField pf_register = ((PasswordField) node.getScene().lookup(registerPasswordId));
		PasswordField pf_login = ((PasswordField) node.getScene().lookup(loginPasswordId));
		
		Label txt_register = ((Label) node.getScene().lookup(registerInfoId));
		Label txt_login = ((Label) node.getScene().lookup(loginInfoId));
		
		tf_register.setText("");
		tf_login.setText("");
		pf_register.setText("");
		pf_login.setText("");
		txt_register.setText("");
		txt_login.setText("");
	}
	
	public void QuitButton(ActionEvent event) {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
	}
}
