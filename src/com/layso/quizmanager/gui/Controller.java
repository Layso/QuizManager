package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.CfgManager;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;



public class Controller {
	/**
	 * Enum type to define windows
	 */
	public enum WindowStage {LoginMenu, MainMenu, CreateQuizMenu, SelectQuestionMenu}
	
	
	
	/**
	 * A method to change the window with a predefined one
	 * @param event ActionEvent produced from GUI
	 * @param stage Enumerated type that indicates the target window
	 */
	public void ChangeScene(ActionEvent event, WindowStage stage) {
		Stage currentWindow = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		double width = currentWindow.getWidth(), height = currentWindow.getHeight();
		Parent parent = null;
		
		
		try {
			// Load target GUI elements
			switch (stage) {
				case LoginMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.login.fxml"))); break;
				case MainMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.main.fxml"))); break;
				case CreateQuizMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.createQuiz.fxml"))); break;
				case SelectQuestionMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.selectQuestion.fxml"))); break;
			}
			
			// Set new scene
			Scene mainMenuScene = new Scene(parent, width, height);
			currentWindow.setScene(mainMenuScene);
			Logger.Log("Scene changed successfully to: " + stage.name(), Logger.LogType.INFO);
		} catch (NullPointerException e) {
			Logger.Log("Fatal Error: FXML filename fetch for failed: " + e.getMessage() + ": Terminating program", Logger.LogType.ERROR);
			System.exit(-1);
		} catch (IOException e) {
			Logger.Log("Fatal Error: GUI replacement failed: " + e.getMessage() + ": Terminating program", Logger.LogType.ERROR);
			System.exit(-1);
		}
	}
}
