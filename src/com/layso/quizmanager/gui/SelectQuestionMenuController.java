package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.Question;
import com.layso.quizmanager.services.DatabaseManager;
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
		ObservableList<String> availableChoices = FXCollections.observableArrayList();
		for (int i=0; i<table.getColumns().size(); ++i) {
			availableChoices.add(((TableColumn) table.getColumns().get(i)).getText());
		}
		
		searchCriteriaChoice.setItems(availableChoices);
		searchCriteriaChoice.getSelectionModel().selectFirst();
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		
		questionColumn.setCellValueFactory(new PropertyValueFactory<Question,String>("questionTextTable"));
		topicsColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("topicsTable"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("typeTable"));
		difficultyColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("difficultyTable"));
		trueDifficultyColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("trueDifficultyTable"));
		ownerColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("ownerTable"));
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
		
		// TODO:
		// Store the selected list in.. somewhere? (QuizManager maybe?)
		
		BackButton(event);
	}
	
	
	
	/**
	 * Button action method to filter the questions by given criteria. Calls helper methods according to search criteria
	 * @param event ActionEvent created by UI
	 */
	public void SearchButton(ActionEvent event) {
		List<Question> questions = DatabaseManager.getInstance().GetAllPublicQuestions();
		ObservableList<Question> data = FXCollections.observableArrayList();
		
		
		// Using if else instead of switch to filter questions since switch needs constant cases
		for (Question q : questions) {
			if (searchCriteriaText.getText().equals("")) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(questionColumn.getText())
				&& FilterQuestionText(q, searchCriteriaText.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(topicsColumn.getText())
				&& FilterQuestionTopics(q, searchCriteriaText.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(typeColumn.getText())
				&& FilterQuestionType(q, searchCriteriaText.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(difficultyColumn.getText())
				&& FilterQuestionDifficulty(q, searchCriteriaText.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(trueDifficultyColumn.getText())
				&& FilterQuestionTrueDifficulty(q, searchCriteriaText.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoice.getSelectionModel().getSelectedItem().toString().equals(ownerColumn.getText())
				&& FilterQuestionOwner(q, searchCriteriaText.getText())) {
				data.add(q);
			}
		}
		
		table.setItems(data);
	}
	
	
	
	/**
	 * Helper method to see if question text is equals or contains given criteria
	 * @param question  Question to compare with criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	private boolean FilterQuestionText(Question question, String criteria) {
		return question.GetQuestion().toLowerCase().equals(criteria.toLowerCase()) || question.GetQuestion().toLowerCase().contains(criteria.toLowerCase());
	}
	
	
	
	/**
	 * Helper method to see if question topics includes given criteria
	 * @param question  Question to compare with criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	private boolean FilterQuestionTopics(Question question, String criteria) {
		return question.getTopicsTable().toLowerCase().equals(criteria.toLowerCase()) || question.getTopicsTable().toLowerCase().contains(criteria.toLowerCase());
	}
	
	
	
	/**
	 * Helper method to see if question type is equals or contains given criteria
	 * @param question  Question to compare with criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	private boolean FilterQuestionType(Question question, String criteria) {
		return question.getTypeTable().toLowerCase().equals(criteria.toLowerCase()) || question.getTypeTable().toLowerCase().contains(criteria.toLowerCase());
	}
	
	
	
	/**
	 * Helper method to see if difficulty of question is higher than given criteria
	 * @param question  Question to compare with criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	private boolean FilterQuestionDifficulty(Question question, String criteria) {
		int criteriaDifficulty;
		
		
		try {
			criteriaDifficulty = Integer.parseInt(criteria);
		} catch (NumberFormatException e) {
			criteriaDifficulty = 0;
		}
		
		return question.GetDifficulty() > criteriaDifficulty;
	}
	
	
	
	/**
	 * Helper method to see if true difficulty of question is higher than given criteria
	 * @param question  Question to compare with criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	private boolean FilterQuestionTrueDifficulty(Question question, String criteria) {
		double actualNumber = Double.parseDouble(question.getTrueDifficultyTable());
		double criteriaNumber;
		
		
		try {
			criteriaNumber = Double.parseDouble(criteria);
		} catch (NumberFormatException e) {
			criteriaNumber = 0;
		}
		
		return actualNumber > criteriaNumber;
	}
	
	
	
	/**
	 * Helper method to see if question owner includes or equals to given criteria
	 * @param question  Question to compare with criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	private boolean FilterQuestionOwner(Question question, String criteria) {
		return question.getOwnerTable().toLowerCase().equals(criteria.toLowerCase()) || question.getOwnerTable().toLowerCase().contains(criteria.toLowerCase());
	}
}
