package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.Question;
import com.layso.quizmanager.datamodel.Quiz;
import com.layso.quizmanager.services.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SolveQuizMenuController extends Controller implements Initializable {
	@FXML
	ChoiceBox searchCriteriaChoiceQuiz;
	
	@FXML
	TextField searchCriteriaTextQuiz;
	
	@FXML
	TableView quizTable;
	
	@FXML
	TableColumn nameColumnQuiz, questionCountColumnQuiz, difficultyColumnQuiz, trueDifficultyColumnQuiz;
	
	@FXML
	TabPane tabs, questionTypeTabs;
	
	@FXML
	TextField questionText, topicsText, resourcePath, mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer,
		mcqCorrectAnswer, associativeLeft1, associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5,
		associativeRight1, associativeRight2, associativeRight3, associativeRight4, associativeRight5, openTipsText;
	
	@FXML
	VBox multipleChoiceQuestionParent, associativeQuestionParent, openQuestionParent;
	
	@FXML
	RadioButton publicButton, privateButton;
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Constructing the TableView elements by associating with classes
		AssociateSearchCriteriaWithTable(searchCriteriaChoiceQuiz, quizTable);
		AssociateTableWithClass(Quiz.GetPropertyValueFactory(), nameColumnQuiz, questionCountColumnQuiz, difficultyColumnQuiz, trueDifficultyColumnQuiz);
		QuizSearchButton(null);
		
		Logger.Log("Solve Quiz Menu initialized", Logger.LogType.INFO);
	}
	
	
	public void QuizSearchButton(ActionEvent event) {
		List<Quiz> quizzes = DatabaseManager.getInstance().GetAllPublicQuizzes();
		ObservableList<Quiz> data = FXCollections.observableArrayList();
		
		if (searchCriteriaTextQuiz.getText().equals("")) {
			for (Quiz q : quizzes) {
				data.add(q);
			}
		} else {
			data = SearchHelper(quizzes, searchCriteriaTextQuiz.getText(), searchCriteriaChoiceQuiz.getSelectionModel().getSelectedItem().toString());
		}
		
		quizTable.setItems(data);
	}
	
	
	public void SelectQuizButton(ActionEvent event) {
		if (quizTable.getSelectionModel().getSelectedItem() != null) {
			ChangeNavigation(event, tabs);
		}
	}
	
	public void QuizBackButton(ActionEvent event) {
		ChangeScene(event, WindowStage.MainMenu);
	}
}
