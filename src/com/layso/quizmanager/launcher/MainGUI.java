package com.layso.quizmanager.launcher;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.CfgManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;



public class MainGUI extends Application {
	public static void main(String[] args) {
		// Check arguments and setup
		Main.CheckArguments(args.length);
		Main.SetupProgram(args, true, true);
		
		// Launch the gui
		Logger.Log("Program has successfully started", Logger.LogType.INFO);
		launch(args);
		Logger.Log("Program has successfully terminated by user", Logger.LogType.INFO);
		Logger.Log("", Logger.LogType.INFO);
	}
	
	
	
	@Override
	public void start(Stage primaryStage) {
		Parent root = null;
		int windowWidth = 0, windowHeight = 0;
		
		
		try {
			// Get window dimensions from configuration file
			windowWidth = Integer.parseInt(CfgManager.getInstance().Get("gui.windowWidth"));
			windowHeight = Integer.parseInt(CfgManager.getInstance().Get("gui.windowHeight"));
		} catch (NumberFormatException e) {
			Logger.Log("Fatal Error: Can't parse window size from config file: " + e.getMessage() + ": Terminating program", Logger.LogType.ERROR);
			System.exit(-1);
		}
		
		try {
			// Load first window which is login menu
			root = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.login.fxml")));
		} catch (NullPointerException e) {
			Logger.Log("Fatal Error: FXML filename fetch for login menu failed: " + e.getMessage() + ": Terminating program", Logger.LogType.ERROR);
			System.exit(-1);
		} catch (IOException e) {
			Logger.Log("Fatal Error: GUI initialization failed: " + e.getMessage() + ": Terminating program", Logger.LogType.ERROR);
			System.exit(-1);
		}
		
		// Prepare window and show
		primaryStage.setResizable(false);
		primaryStage.setTitle("Quizmania - Learn, practice, success");
		primaryStage.setScene(new Scene(root, windowWidth, windowHeight));
		primaryStage.show();
		Logger.Log("GUI has successfully launched", Logger.LogType.INFO);
	}
}
