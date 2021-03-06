package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.AnswerTable;
import com.layso.quizmanager.services.DatabaseManager;
import com.layso.quizmanager.services.QuizManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;



public class SeeResultsMenuController extends Controller implements Initializable {
	// GUI elements
	@FXML
	TableColumn nameColumn, questionCountColumn, trueAnswersColumn, falseAnswersColumn, uncheckedAnswersColumn, percentageColumn, quizSolverColumn;
	
	@FXML
	TableView resultsTable;
	
	@FXML
	ChoiceBox searchCriteriaChoice;
	
	@FXML
	TextField searchCriteriaText;
	
	
	
	/**
	 * Overriding initialize method to setup stage
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Associating search functionality with GUI elements and table with class
		AssociateSearchCriteriaWithTable(searchCriteriaChoice, resultsTable);
		AssociateTableWithClass(AnswerTable.GetPropertyValueFactory(), nameColumn, questionCountColumn, trueAnswersColumn, falseAnswersColumn, uncheckedAnswersColumn, percentageColumn, quizSolverColumn);
		SearchButton(null);
		
		Logger.Log("See Results Menu initialized", Logger.LogType.INFO);
	}
	
	
	
	/**
	 * Method to start SeeResultsMenu on console
	 */
	public static void SeeResultsMenu() {
		List<AnswerTable> answers = DatabaseManager.getInstance().GetAllAnswersByUserID(QuizManager.getInstance().GetUser().GetID());
		boolean run = true;
		boolean correctInput;
		
		
		// Run until user selects main menu
		while (run) {
			// Print results
			System.out.println(AnswerTable.ConsoleTableTitle());
			PrintArrayAsTable(answers);
			
			// Print menu, get input and process input
			System.out.println();
			PrintMenu("Search Result", "Back");
			do {
				switch (GetMenuInput()) {
					case 1: correctInput = true; answers = Search(); break;
					case 2: correctInput = true; run = false; break;
					default: correctInput = false;
				}
			} while (!correctInput);
		}
	}
	
	
	
	/**
	 * Console search helper method
	 * @return  Filtered list of AnswerTables
	 */
	public static List<AnswerTable> Search() {
		// Get Search criteria
		String searchCriteria = GetInput("Search criteria", true);
		AnswerTable.AnswerTableSearchTerms termEnum;
		int searchTerm;
		
		// Get SearchTerm
		do {
			System.out.println("[1] Quiz Title\n[2] QuestionCount\n[3] Difficulty\n[4] TrueDifficulty");
			searchTerm = GetMenuInput();
		} while (searchTerm < 1 || searchTerm > 4);
		
		// Create filtered list and return
		termEnum = searchTerm == 1 ? AnswerTable.AnswerTableSearchTerms.Name :
			(searchTerm == 2 ? AnswerTable.AnswerTableSearchTerms.QuestionCount :
				(searchTerm == 3 ? AnswerTable.AnswerTableSearchTerms.UncheckedAnswers :
					(searchTerm == 4 ? AnswerTable.AnswerTableSearchTerms.TrueAnswers :
						(searchTerm == 5 ? AnswerTable.AnswerTableSearchTerms.FalseAnswers : AnswerTable.AnswerTableSearchTerms.Percentage))));
		return new ArrayList(Controller.SearchHelper(DatabaseManager.getInstance().GetAllAnswersByUserID(QuizManager.getInstance().GetUser().GetID()), searchCriteria, termEnum.name()));
	}
	
	
	
	/**
	 * GUI search button action
	 * @param event ActionEvent created by GUI
	 */
	public void SearchButton(ActionEvent event) {
		// Get filtered list and set it in table
		List<AnswerTable> answers = DatabaseManager.getInstance().GetAllAnswersByUserID(QuizManager.getInstance().GetUser().GetID());
		resultsTable.setItems(SearchHelper(answers, searchCriteriaText.getText(), searchCriteriaChoice.getSelectionModel().getSelectedItem().toString()));
	}
	
	
	
	/**
	 * Back to main menu button action
	 * @param event ActionEvent created by GUI
	 */
	public void BackButton(ActionEvent event) {
		ChangeScene(event, WindowStage.MainMenu);
	}
}
