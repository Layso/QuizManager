package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.DatabaseManager;
import com.layso.quizmanager.services.CfgManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class MainGUI extends Application {
	public static void main(String[] args) {
		// Check arguments, print usage if wrong
		if (args.length != 1) {
			System.out.println("Usage: ./progName [configFile]");
			System.out.println("[configFile] -> File that contains required data to run program");
			System.exit(-1);
		}
		
		// Get configuration settings, setup logger and database manager. Order is important
		CfgManager.Setup(args[0]);
		Logger.Setup(CfgManager.getInstance().Get("log.filename"), true, true);
		DatabaseManager.Setup(CfgManager.getInstance().Get("db.url"), CfgManager.getInstance().Get("db.user"), CfgManager.getInstance().Get("db.pass"));
		
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
			windowWidth = Integer.parseInt(CfgManager.getInstance().Get("gui.windowWidth"));
			windowHeight = Integer.parseInt(CfgManager.getInstance().Get("gui.windowHeight"));
		} catch (NumberFormatException e) {
			Logger.Log("Fatal Error: Can't parse window size from config file: " + e.getMessage() + ": Terminating program", Logger.LogType.ERROR);
			System.exit(-1);
		}
		
		try {
			root = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.fxml")));
		} catch (IOException e) {
			Logger.Log("Fatal Error: GUI initialization failed: " + e.getMessage() + ": Terminating program", Logger.LogType.ERROR);
			System.exit(-1);
		}
		
		
		primaryStage.setTitle("Quizmania - Learn, practice, success");
		primaryStage.setScene(new Scene(root, windowWidth, windowHeight));
		primaryStage.setResizable(false);
		primaryStage.show();
		Logger.Log("GUI has successfully launched", Logger.LogType.INFO);
	}
}
