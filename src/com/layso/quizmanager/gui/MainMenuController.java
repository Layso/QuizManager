package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.CfgManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class MainMenuController implements Initializable {
	@FXML
	Button skipButton;
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
	
	public void Skip(ActionEvent event) {
		Stage window = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		double width = window.getWidth(), height = window.getHeight();
		
		
		try {
			Parent mainMenuParent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.createQuiz.fxml")));
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
}
