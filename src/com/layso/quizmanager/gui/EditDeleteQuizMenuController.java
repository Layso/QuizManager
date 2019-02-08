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
	
	@FXML
	RadioButton publicButton, privateButton;
	
	int selectedQuizID;
	int selectedQuestionID;
	List<TextField> mcqAllChoices, associativeLeft, associativeRight;
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Constructing the TableView elements by associating with classes
		AssociateSearchCriteriaWithTable(searchCriteriaChoiceQuiz, quizTable);
		AssociateTableWithClass(Quiz.GetPropertyValueFactory(), nameColumnQuiz, questionCountColumnQuiz, difficultyColumnQuiz);
		
		AssociateSearchCriteriaWithTable(searchCriteriaChoiceQuestion, questionTable);
		AssociateTableWithClass(Question.GetPropertyValueFactory(), questionColumnQuestion, topicsColumnQuestion,
			typeColumnQuestion, difficultyColumnQuestion, trueDifficultyColumnQuestion, ownerColumnQuestion);
		QuizSearchButton(null);
		
		
		mcqAllChoices = CreateQuizMenuController.CreateTextFieldArray(mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer, mcqCorrectAnswer);
		associativeLeft = CreateQuizMenuController.CreateTextFieldArray(associativeLeft1, associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5);
		associativeRight = CreateQuizMenuController.CreateTextFieldArray(associativeRight1, associativeRight2, associativeRight3, associativeRight4, associativeRight5);
		Logger.Log("Quiz Edit/Delete Menu initialized", Logger.LogType.INFO);
	}
	
	
	
	public void SaveButton(ActionEvent event) {
		if (CreateQuizMenuController.IsQuestionValid(GetQuestionTypeByTab(), questionText, mcqAllChoices, associativeLeft, associativeRight)) {
			Question newQuestion = CreateQuestion(event);
			DatabaseManager.getInstance().ChangeQuestion(((Question) questionTable.getSelectionModel().getSelectedItem()), newQuestion);
			SelectQuizButton(event);
		}
		
		// TODO: UPDATE QUIZ DIFFICULTY AMK
	}
	
	public void SaveOnQuizButton(ActionEvent event) {
		if (CreateQuizMenuController.IsQuestionValid(GetQuestionTypeByTab(), questionText, mcqAllChoices, associativeLeft, associativeRight)) {
			Question newQuestion = CreateQuestion(event);
			int newID = DatabaseManager.getInstance().SaveQuestion(newQuestion);
			DatabaseManager.getInstance().ChangeQuizQuestion(selectedQuestionID, newID, selectedQuizID);
			SelectQuizButton(event);
		}
		
		// TODO: UPDATE QUIZ DIFFICULTY AMK
	}
	
	public void RemoveButton(ActionEvent event) {
		if (questionTable.getSelectionModel().getSelectedItem() != null) {
			selectedQuestionID = ((Question) questionTable.getSelectionModel().getSelectedItem()).GetID();
			DatabaseManager.getInstance().DeleteQuestionByID(selectedQuestionID);
			QuestionSearchButton(event);
		}
		
		// TODO: UPDATE QUIZ DIFFICULTY AMK
	}
	
	
	public void RemoveFromQuizButton(ActionEvent event) {
		if (questionTable.getSelectionModel().getSelectedItem() != null) {
			selectedQuestionID = ((Question) questionTable.getSelectionModel().getSelectedItem()).GetID();
			DatabaseManager.getInstance().DeleteQuestionFromQuiz(selectedQuizID, selectedQuestionID);
			QuestionSearchButton(event);
		}
		
		// TODO: UPDATE QUIZ DIFFICULTY AMK
	}
	
	
	
	/**
	 * Method to get resource path from file selector
	 * @param event
	 */
	public void SelectResourcePath(ActionEvent event) {
		resourcePath.setText(FileSelector());
	}
	
	
	
	/**
	 * Method to build the scene with a question object
	 * @param event
	 */
	private void BuildQuestionEditScreen(ActionEvent event) {
		Question question = DatabaseManager.getInstance().GetQuestionByID(selectedQuestionID);
		
		
		Clear();
		ChangeQuestionNavigationByQuestion(question);
		questionText.setText(question.GetQuestion());
		topicsText.setText(question.getTopicsTable());
		resourcePath.setText("".equals(question.GetResource()) ? "" : DatabaseManager.getInstance().GetResourceNameByQuestionID(question.GetID()));
		difficultySlider.setValue(question.GetDifficulty());
		
		
		if (question.GetPublicity())
			publicButton.fire();
		else
			privateButton.fire();
		
		
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
	
	
	
	/**
	 * If there is a selected quiz, changes scene to quiz edit menu
	 * @param event ActionEvent created by GUI
	 */
	public void SelectQuizButton(ActionEvent event) {
		if (quizTable.getSelectionModel().getSelectedItem() != null) {
			selectedQuizID = ((Quiz) quizTable.getSelectionModel().getSelectedItem()).GetQuizID();
			QuestionSearchButton(event);
			ChangeNavigation(event);
		}
	}
	
	
	
	/**
	 * If there is a selected question, changes scene to question edit menu
	 * @param event
	 */
	public void SelectQuestionButton(ActionEvent event) {
		if (questionTable.getSelectionModel().getSelectedItem() != null) {
			selectedQuestionID = ((Question) questionTable.getSelectionModel().getSelectedItem()).GetID();
			BuildQuestionEditScreen(event);
			ChangeNavigation(event);
		}
	}
	
	
	
	/**
	 * Changes question type tab selection according to the type of given Question
	 * @param question  Question to get type
	 */
	private void ChangeQuestionNavigationByQuestion(Question question) {
		switch (question.GetType()) {
			case MultipleChoice:
				((RadioButton) multipleChoiceQuestionParent.getChildren().get(0)).fire(); break;
			case Associative:
				((RadioButton) associativeQuestionParent.getChildren().get(0)).fire(); break;
			case Open:
				((RadioButton) openQuestionParent.getChildren().get(0)).fire(); break;
		}
	}
	
	
	
	/**
	 * Changes question type tab selection according to the GUI tab clicked by user
	 * @param event ActionEvent created by GUI
	 */
	public void ChangeQuestionNavigation(ActionEvent event) {
		for (Tab tab : questionTypeTabs.getTabs()) {
			if (tab.getId().equals(((Node) event.getSource()).getId())) {
				questionTypeTabs.getSelectionModel().select(tab);
				QuizSearchButton(event);
			}
		}
	}
	
	
	
	/**
	 * Changes menu navigation according to user GUI input
	 * @param event ActionEvent created by GUI
	 */
	public void ChangeNavigation(ActionEvent event) {
		for (Tab tab : tabs.getTabs()) {
			if (tab.getId().equals(((Node) event.getSource()).getId())) {
				tabs.getSelectionModel().select(tab);
			}
		}
	}
	
	
	
	/**
	 * Button action to change scene
	 * @param event ActionEvent created by GUI
	 */
	public void QuizBackButton(ActionEvent event) {
		ChangeScene(event, WindowStage.MainMenu);
	}
	
	
	
	/**
	 * Button action to search quiz
	 * @param event ActionEvent created by GUI
	 */
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
	
	
	
	/**
	 * Button action to search question
	 * @param event ActionEvent created by GUI
	 */
	public void QuestionSearchButton(ActionEvent event) {
		List<Question> questions = DatabaseManager.getInstance().GetQuestionsByQuizID(selectedQuizID);
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
	
	
	
	/**
	 * Helper method to clear fields when question changes
	 */
	public void Clear() {
		ClearTextFields(questionText, topicsText, resourcePath, mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer,
			mcqCorrectAnswer, associativeLeft1, associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5,
			associativeRight1, associativeRight2, associativeRight3, associativeRight4, associativeRight5, openTipsText);
	}
	
	
	
	/**
	 * Checks if the question is valid. If it is, shows the question type by the id of current tab (question type).
	 * Then calls appropriate helper method to create the Question object. Saves new object to questions array, clears
	 * the screen for new question. If it is not valid then shows an error message
	 * @param event ActionEvent produced by GUI
	 */
	public Question CreateQuestion(ActionEvent event) {
		Question newQuestion = null;
		
		
		switch (GetQuestionTypeByTab()) {
			case MultipleChoice: newQuestion = CreateQuizMenuController.CreateMCQ(questionText.getText(), topicsText,
				resourcePath.getText(), publicButton.isSelected(), ((int) difficultySlider.getValue()), mcqAllChoices,
				mcqCorrectAnswer.getText()); break;
			
			case Associative: newQuestion = CreateQuizMenuController.CreateAssociative(questionText.getText(), topicsText,
				resourcePath.getText(), publicButton.isSelected(), ((int) difficultySlider.getValue()), associativeLeft,
				associativeRight); break;
				
			case Open: newQuestion = CreateQuizMenuController.CreateOpenQuestion(questionText.getText(), topicsText,
				resourcePath.getText(), publicButton.isSelected(), ((int) difficultySlider.getValue()),
				openTipsText.getText()); break;
		}
		
		return newQuestion;
	}
	
	
	
	/**
	 * Helper method to get question type by the currently open tab
	 * @return  QuestionType of question
	 */
	private Question.QuestionType GetQuestionTypeByTab() {
		String tabName = questionTypeTabs.getSelectionModel().getSelectedItem().getId().toLowerCase();
		String mcqName = Question.QuestionType.MultipleChoice.name().toLowerCase();
		String associativeName = Question.QuestionType.Associative.name().toLowerCase();
		return tabName.contains(mcqName) ? Question.QuestionType.MultipleChoice :
			(tabName.contains(associativeName) ? Question.QuestionType.Associative : Question.QuestionType.Open);
	}
}
