package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.NotCorrectedOpenQuestion;
import com.layso.quizmanager.services.DatabaseManager;
import com.layso.quizmanager.services.QuizManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;



public class CheckAnswersMenuController extends Controller implements Initializable {
	// GUI elements
	@FXML
	TabPane tabs;
	
	@FXML
	TextField searchCriteriaText;
	
	@FXML
	ChoiceBox searchCriteriaChoice;
	
	@FXML
	TableView questionTable;
	
	@FXML
	TableColumn questionColumn, answerColumn, answererColumn;
	
	@FXML
	Label questionTextLabel, openTipLabel;
	
	@FXML
	TextArea openUserInput;
	
	private static NotCorrectedOpenQuestion currentQuestion;
	private static List<NotCorrectedOpenQuestion> questions;
	
	
	
	/**
	 * Overriding initialize method to setup stage
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Associate search criteria with table columns and table with class
		AssociateSearchCriteriaWithTable(searchCriteriaChoice, questionTable);
		AssociateTableWithClass(NotCorrectedOpenQuestion.GetPropertyValueFactory(), questionColumn, answerColumn, answererColumn);
		QuestionSearchButton(null);
		
		Logger.Log("Check Answers Menu initialized", Logger.LogType.INFO);
	}
	
	
	
	/**
	 * Method to start SolveQuizMenu on console
	 */
	public static void CheckAnswersMenu() {
		boolean correctInput;
		boolean run = true;
		int selection;
		questions =  DatabaseManager.getInstance().GetAllUncorrectedQuestions();
		
		
		// Run until user wants to go back to main menu
		while (run) {
			do {
				// Print ready to evaluate questions
				System.out.println(NotCorrectedOpenQuestion.ConsoleTableTitle());
				PrintArrayAsTable(questions);
				
				// Print menu, get input, process input
				PrintMenu("Select Answer", "Back");
				switch (GetMenuInput()) {
					case 1: correctInput = true;
						// Get input to select answer and call helper to evaluate answer
						if (questions.size() > 0) {
							do {selection = GetMenuInput();} while(selection < 1 || selection > questions.size());
							currentQuestion = questions.get(selection-1);
							CorrectAnswer();
						}
						else {
							System.out.println("No answer to correct");
						}
						break;
					case 2: correctInput = true; run = false; QuizManager.getInstance().SetCurrentStage(WindowStage.MainMenu); break;
					default: correctInput = false;
				}
			} while (!correctInput);
		}
	}
	
	
	
	/**
	 * Helper to evaluate answer
	 */
	public static void CorrectAnswer() {
		boolean result;
		int selection;
		
		
		// Print menu and get input
		PrintMenu("Correct", "False");
		do {selection = GetMenuInput();} while(selection < 1 || selection > 2);
		result = selection == 1;
		
		// Update database according to the input
		DatabaseManager.getInstance().UpdateNotCorrected(currentQuestion, result);
		questions.remove(currentQuestion);
		DatabaseManager.getInstance().UpdateAllQuizzes();
	}
	
	
	
	/**
	 * GUI select question button
	 * @param event ActionEvent created by GUI
	 */
	public void SelectQuestionButton(ActionEvent event) {
		// If there is a selection
		if (questionTable.getSelectionModel().getSelectedItem() != null) {
			// Build scene according to selected question
			currentQuestion = ((NotCorrectedOpenQuestion) questionTable.getSelectionModel().getSelectedItem());
			BuildQuestionScene();
			ChangeNavigation(event, tabs);
		}
	}
	
	
	
	/**
	 * GUI main menu button action
	 * @param event  ActionEvent created by GUI
	 */
	public void BackToMenuButton(ActionEvent event) {
		ChangeScene(event, WindowStage.MainMenu);
	}
	
	
	
	/**
	 * GUI correct button action
	 * @param event ActionEvent created by GUI
	 */
	public void CorrectButton(ActionEvent event) {
		CorrectedQuestion(true, event);
	}
	
	
	
	/**
	 * GUI false button action
	 * @param event ActionEvent created by GUI
	 */
	public void FalseButton(ActionEvent event) {
		CorrectedQuestion(false, event);
	}
	
	
	
	/**
	 * GUI back to question selection button
	 * @param event ActionEvent created by GUI
	 */
	public void BackButton(ActionEvent event) {
		QuestionSearchButton(event);
		ChangeNavigation(event, tabs);
	}
	
	
	
	/**
	 * GUI question search button
	 * @param event ActionEvent created by GUI
	 */
	public void QuestionSearchButton(ActionEvent event) {
		questions =  DatabaseManager.getInstance().GetAllUncorrectedQuestions();
		questionTable.setItems(SearchHelper(questions, searchCriteriaText.getText(), searchCriteriaChoice.getSelectionModel().getSelectedItem().toString()));
	}
	
	
	
	/**
	 * Helper method to build the scene according to selected answer
	 */
	private void BuildQuestionScene() {
		questionTextLabel.setText(currentQuestion.getQuestion());
		openTipLabel.setText(currentQuestion.GetQuestion().GetTips());
		openUserInput.setText(currentQuestion.GetAnswer());
	}
	
	
	
	/**
	 * Post scene build method to get another answer or go back to answer selection menu
	 * @param result    Result of current question
	 * @param event     ActionEvent created by GUI
	 */
	private void CorrectedQuestion(boolean result, ActionEvent event) {
		DatabaseManager.getInstance().UpdateNotCorrected(currentQuestion, result);
		questions.remove(currentQuestion);
		
		// If there is no answer to evaluate, go back to selection menu
		if (questions.isEmpty()) {
			BackButton(event);
		}
		
		// Else build next answer
		else {
			currentQuestion = questions.get(0);
			BuildQuestionScene();
		}
	}
}
