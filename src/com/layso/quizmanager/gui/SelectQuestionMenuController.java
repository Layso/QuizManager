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
	
	
	public void BackButton(ActionEvent event) {
		ChangeScene(event, WindowStage.CreateQuizMenu);
	}
	
	public void SelectButton(ActionEvent event) {
		// TODO:
		// Save all selected questions in a list
		// Store the selected list in.. somewhere? (QuizManager maybe?)
		
		//table.getSelectionModel().get
		
		BackButton(event);
	}
	
	public void SearchButton(ActionEvent event) {
		List<Question> questions = DatabaseManager.getInstance().GetAllPublicQuestions();
		ObservableList<Question> data = FXCollections.observableArrayList();
		
		
		
		if (searchCriteriaText.getText().equals("")) {
			for (Question q : questions) {
				data.add(q);
			}
		}
		
		// TODO:
		// Eliminate the unwanted results according to search criteria
		
		else if (((TableColumn) searchCriteriaChoice.getSelectionModel().getSelectedItem()).getText().equals(questionColumn.getText())) {
		
		}
		
		else if (((TableColumn) searchCriteriaChoice.getSelectionModel().getSelectedItem()).getText().equals(typeColumn.getText())) {
		
		}
		
		else if (((TableColumn) searchCriteriaChoice.getSelectionModel().getSelectedItem()).getText().equals(difficultyColumn.getText())) {
		
		}
		
		else if (((TableColumn) searchCriteriaChoice.getSelectionModel().getSelectedItem()).getText().equals(trueDifficultyColumn.getText())) {
		
		}
		
		else if (((TableColumn) searchCriteriaChoice.getSelectionModel().getSelectedItem()).getText().equals(ownerColumn.getText())) {
		
		}
		
		table.setItems(data);
	}
}
