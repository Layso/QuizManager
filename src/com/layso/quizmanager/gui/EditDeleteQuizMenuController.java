package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.*;
import com.layso.quizmanager.services.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;


import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EditDeleteQuizMenuController extends Controller implements Initializable {
	@FXML
	ChoiceBox searchCriteriaChoiceQuiz, searchCriteriaChoiceQuestion;
	
	@FXML
	TextField searchCriteriaTextQuiz, searchCriteriaTextQuestion;
	
	@FXML
	TableView quizTable, questionTable;
	
	@FXML
	TableColumn nameColumnQuiz, questionCountColumnQuiz, difficultyColumnQuiz, questionColumnQuestion, topicsColumnQuestion,
		typeColumnQuestion, difficultyColumnQuestion, trueDifficultyColumnQuestion, ownerColumnQuestion;
	
	@FXML
	TabPane tabs, questionTypeTabs;
	
	@FXML
	Slider difficultySlider;
	
	@FXML
	TextField questionText, topicsText, resourcePath, mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer,
		mcqCorrectAnswer, associativeLeft1, associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5,
		associativeRight1, associativeRight2, associativeRight3, associativeRight4, associativeRight5, openTipsText;
	
	@FXML
	VBox multipleChoiceQuestionParent, associativeQuestionParent, openQuestionParent;
	
	int quizID;
	int questionID;
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		AssociateSearchCriteriaWithTable(searchCriteriaChoiceQuiz, quizTable);
		AssociateTableWithClass(Quiz.GetPropertyValueFactory(), nameColumnQuiz, questionCountColumnQuiz, difficultyColumnQuiz);
		
		
		AssociateSearchCriteriaWithTable(searchCriteriaChoiceQuestion, questionTable);
		AssociateTableWithClass(Question.GetPropertyValueFactory(), questionColumnQuestion, topicsColumnQuestion,
			typeColumnQuestion, difficultyColumnQuestion, trueDifficultyColumnQuestion, ownerColumnQuestion);
		
		QuizSearchButton(null);
		Logger.Log("Quiz Edit/Delete Menu initialized", Logger.LogType.INFO);
	}
	
	
	
	public void SaveButton(ActionEvent event) {
		// TODO:
		// Check question validness
		// If type is different than old, delete question
			// Create new question
			// Change old questionID from association table with new id
		
		// Else
			// Alter question tables
		
		// Reload question selection menu
	}
	
	public void SaveOnQuizButton(ActionEvent event) {
		// TODO:
		// Check question validness
		// Create new question
		// Change questionID with new produced ID on association table where quiz id is quizID
		
		// Reload question selection menu
	}
	
	public void RemoveButton(ActionEvent event) {
		// TODO:
		// Delete question
		// Delete entries on association table where question ıd is questionID
		
		// Reload question selection menu
	}
	
	
	public void RemoveFromQuizButton(ActionEvent event) {
		// TODO:
		// Delete entries on association table where question ıd is questionID
		
		// Reload question selection menu
	}
	
	
	
	public void SelectResourcePath(ActionEvent event) {
		resourcePath.setText(FileSelector());
	}
	
	private void BuildQuestionScreen(ActionEvent event) {
		Question question = DatabaseManager.getInstance().GetQuestionByID(questionID);
		
		
		ChangeQuestionNavigationByQuestion(question);
		questionText.setText(question.GetQuestion());
		topicsText.setText(question.getTopicsTable());
		resourcePath.setText("".equals(question.GetResource()) ? "" : DatabaseManager.getInstance().GetResourceNameByQuestionID(question.GetQuestionID()));
		difficultySlider.setValue(question.GetDifficulty());
		
		
		if (question.GetType() == Question.QuestionType.MultipleChoice) {
			MultipleChoiceQuestion q = ((MultipleChoiceQuestion) question);
			List<String> answers = q.GetAnswers();
			
			mcqFirstAnswer.setText(answers.get(0));
			mcqSecondAnswer.setText(answers.get(1));
			mcqThirdAnswer.setText(answers.get(2));
			mcqCorrectAnswer.setText(q.GetCorrectAnswer());
		}
		
		else if (question.GetType() == Question.QuestionType.Associative) {
			AssociativeQuestion q = ((AssociativeQuestion) question);
			List<String> left = q.GetLeftColumn(), right = q.GetRightColumn();
			
			switch (left.size()) {
				case 5: associativeLeft5.setText(left.get(4)); associativeRight5.setText(right.get(4));
				case 4: associativeLeft4.setText(left.get(3)); associativeRight4.setText(right.get(3));
				case 3: associativeLeft3.setText(left.get(2)); associativeRight3.setText(right.get(2));
				case 2: associativeLeft2.setText(left.get(1)); associativeRight2.setText(right.get(1));
				case 1: associativeLeft1.setText(left.get(0)); associativeRight1.setText(right.get(0)); break;
			}
		}
		
		else {
			OpenQuestion q = ((OpenQuestion) question);
			openTipsText.setText(q.GetTips());
		}
	}
	
	public void SelectQuizButton(ActionEvent event) {
		if (quizTable.getSelectionModel().getSelectedItem() != null) {
			quizID = ((Quiz) quizTable.getSelectionModel().getSelectedItem()).GetQuizID();
			QuestionSearchButton(event);
			ChangeNavigation(event);
		}
	}
	
	public void SelectQuestionButton(ActionEvent event) {
		if (questionTable.getSelectionModel().getSelectedItem() != null) {
			questionID = ((Question) questionTable.getSelectionModel().getSelectedItem()).GetQuestionID();
			BuildQuestionScreen(event);
			ChangeNavigation(event);
		}
	}
	
	private void ChangeQuestionNavigationByQuestion(Question q) {
		switch (q.GetType()) {
			case MultipleChoice:
				((RadioButton) multipleChoiceQuestionParent.getChildren().get(0)).fire(); break;
			case Associative:
				((RadioButton) associativeQuestionParent.getChildren().get(0)).fire(); break;
			case Open:
				((RadioButton) openQuestionParent.getChildren().get(0)).fire(); break;
		}
	}
	
	public void ChangeQuestionNavigation(ActionEvent event) {
		for (Tab tab : questionTypeTabs.getTabs()) {
			if (tab.getId().equals(((Node) event.getSource()).getId())) {
				questionTypeTabs.getSelectionModel().select(tab);
			}
		}
	}
	
	public void ChangeNavigation(ActionEvent event) {
		for (Tab tab : tabs.getTabs()) {
			if (tab.getId().equals(((Node) event.getSource()).getId())) {
				tabs.getSelectionModel().select(tab);
			}
		}
	}
	
	public void QuizBackButton(ActionEvent event) {
		ChangeScene(event, WindowStage.MainMenu);
	}
	
	
	public void QuizSearchButton(ActionEvent event) {
		List<Quiz> quizzes = DatabaseManager.getInstance().GetOwningQuizzes();
		ObservableList<Quiz> data = FXCollections.observableArrayList();
		
		
		for (Quiz quiz : quizzes) {
			if (searchCriteriaTextQuiz.getText().equals("")) {
				data.add(quiz);
			}
			
			else if (searchCriteriaChoiceQuiz.getSelectionModel().getSelectedItem().toString().equals(nameColumnQuiz.getText())
				&& quiz.FilterQuizName(searchCriteriaTextQuiz.getText())) {
				data.add(quiz);
			}
			
			else if (searchCriteriaChoiceQuiz.getSelectionModel().getSelectedItem().toString().equals(questionCountColumnQuiz.getText())
				&& quiz.FilterQuizQuestionCount(searchCriteriaTextQuiz.getText())) {
				data.add(quiz);
			}
			
			else if (searchCriteriaChoiceQuiz.getSelectionModel().getSelectedItem().toString().equals(difficultyColumnQuiz.getText())
				&& quiz.FilterQuizDifficulty(searchCriteriaTextQuiz.getText())) {
				data.add(quiz);
			}
		}
		
		
		quizTable.setItems(data);
	}
	
	
	public void QuestionSearchButton(ActionEvent event) {
		List<Question> questions = DatabaseManager.getInstance().GetQuestionsByQuizID(quizID);
		ObservableList<Question> data = FXCollections.observableArrayList();
		
		
		for (Question q : questions) {
			if (searchCriteriaTextQuestion.getText().equals("")) {
				data.add(q);
			}
			
			else if (searchCriteriaChoiceQuestion.getSelectionModel().getSelectedItem().toString().equals(questionColumnQuestion.getText())
				&& q.FilterQuestionText(searchCriteriaTextQuestion.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoiceQuestion.getSelectionModel().getSelectedItem().toString().equals(topicsColumnQuestion.getText())
				&& q.FilterQuestionTopics(searchCriteriaTextQuestion.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoiceQuestion.getSelectionModel().getSelectedItem().toString().equals(typeColumnQuestion.getText())
				&& q.FilterQuestionType(searchCriteriaTextQuestion.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoiceQuestion.getSelectionModel().getSelectedItem().toString().equals(difficultyColumnQuestion.getText())
				&& q.FilterQuestionDifficulty(searchCriteriaTextQuestion.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoiceQuestion.getSelectionModel().getSelectedItem().toString().equals(trueDifficultyColumnQuestion.getText())
				&& q.FilterQuestionTrueDifficulty(searchCriteriaTextQuestion.getText())) {
				data.add(q);
			}
			
			else if (searchCriteriaChoiceQuestion.getSelectionModel().getSelectedItem().toString().equals(ownerColumnQuestion.getText())
				&& q.FilterQuestionOwner(searchCriteriaTextQuestion.getText())) {
				data.add(q);
			}
		}
		
		questionTable.setItems(data);
	}
	
	public void Clear() {
		ClearTextFields(questionText, topicsText, resourcePath, mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer,
			mcqCorrectAnswer, associativeLeft1, associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5,
			associativeRight1, associativeRight2, associativeRight3, associativeRight4, associativeRight5, openTipsText);
	}
}
