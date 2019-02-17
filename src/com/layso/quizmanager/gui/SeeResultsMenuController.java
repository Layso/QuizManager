package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.AnswerTable;
import com.layso.quizmanager.datamodel.Question;
import com.layso.quizmanager.datamodel.Quiz;
import com.layso.quizmanager.services.DatabaseManager;
import com.layso.quizmanager.services.QuizManager;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SeeResultsMenuController extends Controller implements Initializable {
	@FXML
	TableColumn nameColumn, questionCountColumn, trueAnswersColumn, falseAnswersColumn, uncheckedAnswersColumn, percentageColumn;
	
	@FXML
	TableView resultsTable;
	
	@FXML
	ChoiceBox searchCriteriaChoice;
	
	@FXML
	TextField searchCriteriaText;
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		AssociateSearchCriteriaWithTable(searchCriteriaChoice, resultsTable);
		AssociateTableWithClass(AnswerTable.GetPropertyValueFactory(), nameColumn, questionCountColumn, trueAnswersColumn, falseAnswersColumn, uncheckedAnswersColumn, percentageColumn);
		SearchButton(null);
		Logger.Log("See Results Menu initialized", Logger.LogType.INFO);
	}
	
	public void SearchButton(ActionEvent event) {
		List<AnswerTable> answers = DatabaseManager.getInstance().GetAllAnswersByUserID(QuizManager.getInstance().GetUser().GetID());
		resultsTable.setItems(SearchHelper(answers, searchCriteriaText.getText(), searchCriteriaChoice.getSelectionModel().getSelectedItem().toString()));
	}
	
	public void BackButton(ActionEvent event) {
		ChangeScene(event, WindowStage.MainMenu);
	}
}
