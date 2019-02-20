package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.CfgManager;
import com.layso.quizmanager.services.DatabaseManager;
import com.layso.quizmanager.services.QuizManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ResourceBundle;



public class MainMenuController extends Controller implements Initializable {
	@FXML
	Tab authoratitiveTab, normalTab;
	
	@FXML
	TabPane tabs;
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		if (QuizManager.getInstance().GetUser().isAuthoritative())
			tabs.getSelectionModel().select(authoratitiveTab);
		else
			tabs.getSelectionModel().select(normalTab);
		
		Logger.Log("Main Menu initialized", Logger.LogType.INFO);
	}
	
	public void CreateQuizButton(ActionEvent event) {
		ChangeScene(event, WindowStage.CreateQuizMenu);
	}
	
	public void EditDeleteButton(ActionEvent event) {
		ChangeScene(event, WindowStage.EditDeleteQuizMenu);
	}
	
	public void SolveQuizButton(ActionEvent event) {
		ChangeScene(event, WindowStage.SolveQuizMenu);
	}
	
	public void SeeResultsButton(ActionEvent event) {
		ChangeScene(event, WindowStage.SeeResultsMenu);
	}
	
	public void CorrectAnswer(ActionEvent event) {
		ChangeScene(event, WindowStage.CheckAnswersMenu);
	}
	
	public void UserPromotion(ActionEvent event) {
		ChangeScene(event, WindowStage.UserPromoteMenu);
	}
	
	public void QuitButton(ActionEvent event) {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
	}
	
	
	public static void MainMenu() {
		boolean run = true;
		boolean correctInput;
		
		
		while (run) {
			PrintMainMenu();
			
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
	
	
	public static void PrintMainMenu() {
		String[] nonAuthorativeMenu = {"Solve Quiz", "See Results", "Quit"};
		String[] authorativeMenu = {"Create Quiz", "Edit Quiz", "Correct Answers", "See Results", "Promote Users", "Quit"};
		
		PrintMenu(QuizManager.getInstance().GetUser().isAuthoritative() ? authorativeMenu : nonAuthorativeMenu);
	}
}
