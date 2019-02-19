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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CheckAnswersMenuController extends Controller implements Initializable {
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
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		AssociateSearchCriteriaWithTable(searchCriteriaChoice, questionTable);
		AssociateTableWithClass(NotCorrectedOpenQuestion.GetPropertyValueFactory(), questionColumn, answerColumn, answererColumn);
		QuestionSearchButton(null);
		Logger.Log("Check Answers Menu initialized", Logger.LogType.INFO);
	}
	
	
	public static void CheckAnswersMenu() {
		boolean correctInput;
		boolean run = true;
		int selection;
		questions =  DatabaseManager.getInstance().GetAllUncorrectedQuestions();
		
		
		while (run) {
			do {
				PrintArrayAsTable(questions);
				PrintMenu("Select Answer", "Back");
				switch (GetMenuInput()) {
					case 1: correctInput = true;
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
	
	
	public static void CorrectAnswer() {
		boolean result;
		int selection;
		
		
		PrintMenu("Correct", "False");
		do {selection = GetMenuInput();} while(selection < 1 || selection > 2);
		result = selection == 1;
		DatabaseManager.getInstance().UpdateNotCorrected(currentQuestion, result);
		questions.remove(currentQuestion);
		DatabaseManager.getInstance().UpdateAllQuizzes();
	}
	
	public void SelectQuestionButton(ActionEvent event) {
		if (questionTable.getSelectionModel().getSelectedItem() != null) {
			currentQuestion = ((NotCorrectedOpenQuestion) questionTable.getSelectionModel().getSelectedItem());
			BuildQuestionScene();
			ChangeNavigation(event, tabs);
		}
	}
	
	public void BackToMenuButton(ActionEvent event) {
		ChangeScene(event, WindowStage.MainMenu);
	}
	
	public void CorrectButton(ActionEvent event) {
		CorrectedQuestion(true, event);
	}
	
	public void FalseButton(ActionEvent event) {
		CorrectedQuestion(false, event);
	}
	
	public void BackButton(ActionEvent event) {
		QuestionSearchButton(event);
		ChangeNavigation(event, tabs);
	}
	
	public void QuestionSearchButton(ActionEvent event) {
		questions =  DatabaseManager.getInstance().GetAllUncorrectedQuestions();
		questionTable.setItems(SearchHelper(questions, searchCriteriaText.getText(), searchCriteriaChoice.getSelectionModel().getSelectedItem().toString()));
	}
	
	private void BuildQuestionScene() {
		questionTextLabel.setText(currentQuestion.getQuestion());
		openTipLabel.setText(currentQuestion.GetQuestion().GetTips());
		openUserInput.setText(currentQuestion.GetAnswer());
	}
	
	private void CorrectedQuestion(boolean result, ActionEvent event) {
		DatabaseManager.getInstance().UpdateNotCorrected(currentQuestion, result);
		questions.remove(currentQuestion);
		
		if (questions.isEmpty()) {
			BackButton(event);
		}
		
		else {
			currentQuestion = questions.get(0);
			BuildQuestionScene();
		}
	}
}
