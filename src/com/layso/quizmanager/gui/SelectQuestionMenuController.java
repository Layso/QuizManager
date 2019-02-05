package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.Question;
import com.layso.quizmanager.services.DatabaseManager;
import com.layso.quizmanager.services.QuizManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;



public class SelectQuestionMenuController extends Controller implements Initializable {
	@FXML
	ChoiceBox searchCriteriaChoice;
	
	@FXML
	TextField searchCriteriaText;
	
	@FXML
	TableView table;
	
	@FXML
	TableColumn questionColumn, topicsColumn, typeColumn, difficultyColumn, trueDifficultyColumn, ownerColumn;
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		AssociateSearchCriteriaWithTable(searchCriteriaChoice, table);
		AssociateTableWithClass(Question.GetPropertyValueFactory(), questionColumn, topicsColumn, typeColumn,
			difficultyColumn, trueDifficultyColumn, ownerColumn);
		SearchButton(null);
		Logger.Log("Question Selection Menu initialized", Logger.LogType.INFO);
	}
	
	
	
	/**
	 * Button action method to cancel action and go back to quiz creation menu
	 * @param event ActionEvent created by UI
	 */
	public void BackButton(ActionEvent event) {
		ChangeScene(event, WindowStage.CreateQuizMenu);
	}
	
	
	
	/**
	 * Button action method to get selected questions and save them to use for quiz creation
	 * @param event ActionEvent created by UI
	 */
	public void SelectButton(ActionEvent event) {
		List<Question> selectedQuestions = new ArrayList<>();
		
		
		for (int i = 0; i<table.getSelectionModel().getSelectedItems().size(); ++i) {
			Question q = ((Question) table.getSelectionModel().getSelectedItems().get(i));
			selectedQuestions.add(q);
		}
		
		QuizManager.getInstance().SaveQuestion(selectedQuestions);
		BackButton(event);
	}
	
	
	
	/**
	 * Button action method to filter the questions by given criteria. Calls helper methods according to search criteria
	 * @param event ActionEvent created by UI
	 */
	public void SearchButton(ActionEvent event) {
		List<Question> questions = DatabaseManager.getInstance().GetAllPublicQuestions();
		ObservableList<Question> data = FXCollections.observableArrayList();
		
		
		for (Question q : questions) {
			if (searchCriteriaText.getText().equals("")) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(questionColumn.getText())
				&& q.FilterQuestionText(searchCriteriaText.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(topicsColumn.getText())
				&& q.FilterQuestionTopics(searchCriteriaText.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(typeColumn.getText())
				&& q.FilterQuestionType(searchCriteriaText.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(difficultyColumn.getText())
				&& q.FilterQuestionDifficulty(searchCriteriaText.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(trueDifficultyColumn.getText())
				&& q.FilterQuestionTrueDifficulty(searchCriteriaText.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(ownerColumn.getText())
				&& q.FilterQuestionOwner(searchCriteriaText.getText())) {
				data.add(q);
			}
		}
		
		table.setItems(data);
	}
}
