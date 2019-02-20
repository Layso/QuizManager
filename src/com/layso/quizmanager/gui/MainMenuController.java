package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.QuizManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;



public class MainMenuController extends Controller implements Initializable {
	// GUI elements
	@FXML
	Tab authoratitiveTab, normalTab;
	
	@FXML
	TabPane tabs;
	
	
	
	/**
	 * Overriding initialize method to setup stage
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Build the main menu buttons according to the user authority
		if (QuizManager.getInstance().GetUser().isAuthoritative())
			tabs.getSelectionModel().select(authoratitiveTab);
		else
			tabs.getSelectionModel().select(normalTab);
		
		Logger.Log("Main Menu initialized", Logger.LogType.INFO);
	}
	
	
	
	/**
	 * Method to start MainMenu on console
	 */
	public static void MainMenu() {
		boolean run = true;
		boolean correctInput;
		
		
		// Run until user specifies to log out
		while (run) {
			// Print menu
			PrintMainMenu();
			
			// Get input, process input according to user Authority
			if (QuizManager.getInstance().GetUser().isAuthoritative()) {
				do {
					switch (Controller.GetMenuInput()) {
						case 1: correctInput = true; CreateQuizMenuController.CreateQuizMenu(); break;
						case 2: correctInput = true; EditDeleteQuizMenuController.EditDeleteQuizMenu(); break;
						case 3: correctInput = true; CheckAnswersMenuController.CheckAnswersMenu(); break;
						case 4: correctInput = true; SeeResultsMenuController.SeeResultsMenu(); break;
						case 5: correctInput = true; UserPromotionMenuController.UserPromotionMenu(); break;
						case 6: correctInput = true; run = false; QuizManager.getInstance().SetCurrentStage(WindowStage.LoginMenu); break;
						default: correctInput = false;
					}
				} while (!correctInput);
			}
			
			else {
				do {
					switch (Controller.GetMenuInput()) {
						case 1: correctInput = true; SolveQuizMenuController.SolveQuizMenu(); break;
						case 2: correctInput = true; SeeResultsMenuController.SeeResultsMenu(); break;
						case 3: correctInput = true; run = false; QuizManager.getInstance().SetCurrentStage(WindowStage.LoginMenu); break;
						default: correctInput = false;
					}
				} while (!correctInput);
			}
		}
	}
	
	
	
	/**
	 * Helper method to print Menu options according to user
	 */
	public static void PrintMainMenu() {
		String[] nonAuthorativeMenu = {"Solve Quiz", "See Results", "Quit"};
		String[] authorativeMenu = {"Create Quiz", "Edit Quiz", "Correct Answers", "See Results", "Promote Users", "Quit"};
		
		PrintMenu(QuizManager.getInstance().GetUser().isAuthoritative() ? authorativeMenu : nonAuthorativeMenu);
	}
	
	
	
	/**
	 * Button action method to change scene to CreateQuizMenu
	 * @param event ActionEvent created by GUI
	 */
	public void CreateQuizButton(ActionEvent event) {
		ChangeScene(event, WindowStage.CreateQuizMenu);
	}
	
	/**
	 * Button action method to change scene to EditDeleteQuizMenu
	 * @param event ActionEvent created by GUI
	 */
	public void EditDeleteButton(ActionEvent event) {
		ChangeScene(event, WindowStage.EditDeleteQuizMenu);
	}
	
	/**
	 * Button action method to change scene to SolveQuizMenu
	 * @param event ActionEvent created by GUI
	 */
	public void SolveQuizButton(ActionEvent event) {
		ChangeScene(event, WindowStage.SolveQuizMenu);
	}
	
	/**
	 * Button action method to change scene to SeeResultsMenu
	 * @param event ActionEvent created by GUI
	 */
	public void SeeResultsButton(ActionEvent event) {
		ChangeScene(event, WindowStage.SeeResultsMenu);
	}
	
	/**
	 * Button action method to change scene to CheckAnswersMenu
	 * @param event ActionEvent created by GUI
	 */
	public void CorrectAnswer(ActionEvent event) {
		ChangeScene(event, WindowStage.CheckAnswersMenu);
	}
	
	/**
	 * Button action method to change scene to UserPromoteMenu
	 * @param event ActionEvent created by GUI
	 */
	public void UserPromotion(ActionEvent event) {
		ChangeScene(event, WindowStage.UserPromoteMenu);
	}
	
	/**
	 * Button action method to quit the application
	 * @param event ActionEvent created by GUI
	 */
	public void QuitButton(ActionEvent event) {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
	}
}
