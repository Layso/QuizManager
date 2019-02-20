package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.*;
import com.layso.quizmanager.services.DatabaseManager;
import com.layso.quizmanager.services.QuizManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;



public class EditDeleteQuizMenuController extends Controller implements Initializable {
	// GUI elements
	@FXML
	ChoiceBox searchCriteriaChoiceQuiz, searchCriteriaChoiceQuestion;
	
	@FXML
	TextField searchCriteriaTextQuiz, searchCriteriaTextQuestion;
	
	@FXML
	TableView quizTable, questionTable;
	
	@FXML
	TableColumn nameColumnQuiz, questionCountColumnQuiz, difficultyColumnQuiz, trueDifficultyColumnQuiz,
		questionColumnQuestion, topicsColumnQuestion, typeColumnQuestion, difficultyColumnQuestion,
		trueDifficultyColumnQuestion, ownerColumnQuestion;
	
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
	
	private int selectedQuizID;
	private int selectedQuestionID;
	private List<TextField> mcqAllChoices, associativeLeft, associativeRight;
	
	
	
	/**
	 * Overriding initialize method to setup stage
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Constructing the TableView elements by associating with classes
		AssociateSearchCriteriaWithTable(searchCriteriaChoiceQuiz, quizTable);
		AssociateTableWithClass(Quiz.GetPropertyValueFactory(), nameColumnQuiz, questionCountColumnQuiz, difficultyColumnQuiz, trueDifficultyColumnQuiz);
		
		AssociateSearchCriteriaWithTable(searchCriteriaChoiceQuestion, questionTable);
		AssociateTableWithClass(Question.GetPropertyValueFactory(), questionColumnQuestion, topicsColumnQuestion,
			typeColumnQuestion, difficultyColumnQuestion, trueDifficultyColumnQuestion, ownerColumnQuestion);
		QuizSearchButton(null);
		
		
		mcqAllChoices = CreateQuizMenuController.CreateTextFieldArray(mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer, mcqCorrectAnswer);
		associativeLeft = CreateQuizMenuController.CreateTextFieldArray(associativeLeft1, associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5);
		associativeRight = CreateQuizMenuController.CreateTextFieldArray(associativeRight1, associativeRight2, associativeRight3, associativeRight4, associativeRight5);
		Logger.Log("Quiz Edit/Delete Menu initialized", Logger.LogType.INFO);
	}
	
	
	
	/**
	 * Method to start EditDeleteQuizMenu on console
	 */
	public static void EditDeleteQuizMenu() {
		boolean run = true;
		boolean correctInput;
		
		
		// Run until user wants to go back to main menu
		while (run) {
			do {
				// Print menu, get input and process input
				System.out.println();
				PrintMenu("Select Quiz", "Back");
				switch (GetMenuInput()) {
					case 1: correctInput = true; SelectQuizQuestion(); break;
					case 2: correctInput = true; run = false; QuizManager.getInstance().SetCurrentStage(WindowStage.MainMenu); break;
					default: correctInput = false;
				}
			} while (!correctInput);
		}
	}
	
	
	
	/**
	 * Helper menu to seşect a quiz and question to edit or delete
	 */
	public static void SelectQuizQuestion() {
		List<Quiz> quizzes = DatabaseManager.getInstance().GetOwningQuizzes();
		List<Question> questions;
		Quiz quiz;
		Question question;
		int selection;
		boolean run = true;
		
		
		// Print quiz list to let user pick quiz
		System.out.println(Quiz.ConsoleTableTitle());
		PrintArrayAsTable(quizzes);
		do { selection = GetMenuInput(); } while (selection < 1 || selection > quizzes.size());
		quiz = quizzes.get(selection-1);
		questions = quiz.GetQuestions();
		
		// Print question list of selected quiz
		System.out.println(Question.ConsoleTableTitle());
		PrintArrayAsTable(questions);
		do { selection = GetMenuInput(); } while (selection < 1 || selection > questions.size());
		question = questions.get(selection-1);
		
		// Print menu, get input, process input
		PrintMenu("Edit Question", "Remove Question", "Remove Question From Quiz");
		do { selection = GetMenuInput(); } while (selection < 1 || selection > 3);
		switch (selection) {
			case 1: EditQuestion(question); DatabaseManager.getInstance().UpdateAllQuizzes(); break;
			case 2: DatabaseManager.getInstance().DeleteQuestionByID(question.GetID(), true); DatabaseManager.getInstance().UpdateAllQuizzes(); break;
			case 3: DatabaseManager.getInstance().DeleteQuestionFromQuiz(quiz.GetID(), question.GetID()); DatabaseManager.getInstance().UpdateAllQuizzes(); break;
		}
	}
	
	
	
	/**
	 * Helper menu to edit given question
	 * @param question  Question to edit
	 */
	public static void EditQuestion (Question question) {
		Question newQuestion;
		int selection;
		
		
		// Build menu by printing question first
		System.out.println("[1] Question:   " + question.GetQuestion());
		System.out.println("    Type:       " + question.GetType());
		System.out.println("[2] Topics:     " + question.getTopicsTable());
		System.out.println("[3] Resource:   " + question.GetResource());
		System.out.println("[4] Publicity:  " + question.GetPublicity());
		System.out.println("[5] Difficulty: " + question.GetDifficulty());
		
		// Print Multiple Choice specific parts
		if (question.GetType() == Question.QuestionType.MultipleChoice) {
			MultipleChoiceQuestion multipleChoice = ((MultipleChoiceQuestion) question);
			
			System.out.println("[6] Answers:   ");
			System.out.println("    Correct:   " + multipleChoice.GetCorrectAnswer());
			System.out.println("    Others:    " + multipleChoice.GetAnswers());
		}
		
		// Print Associative specific parts
		else if (question.GetType() == Question.QuestionType.Associative) {
			AssociativeQuestion associative = ((AssociativeQuestion) question);
			List<String> left = associative.GetLeftColumn();
			List<String> right = associative.GetRightColumn();
			
			System.out.println("[6] Rows:   ");
			for (int i=0; i<left.size(); ++i)
				System.out.println("    Row :   " + left.get(i) + " - " + right.get(i));
		}
		
		// Print Open specific parts
		else {
			OpenQuestion open = ((OpenQuestion) question);
			System.out.println("[6] Tip:       " + open.GetTips());
		}
		
		
		// Get an attribute to edit, construct new question, save to database
		do {selection = GetMenuInput();} while (selection < 1 || selection > 6 );
		newQuestion = CreateEditedQuestion(question, selection);
		DatabaseManager.getInstance().ChangeQuestion(question, newQuestion);
	}
	
	
	
	/**
	 * Console helper method to edit selected question
	 * @param oldQuestion   Old question
	 * @param change        Index of changed attribute
	 * @return              New question
	 */
	public static Question CreateEditedQuestion(Question oldQuestion, int change) {
		Question newQuestion = null;
		Object changedValue = null;
		
		
		// Ask new value of selected change attribute
		switch (change) {
			case 1: changedValue = GetInput("New question", false); break;
			case 2: changedValue = GetInput("New topics", false); break;
			case 3: changedValue = GetInput("New resource", false); break;
			case 4: changedValue = Boolean.toString(true).toLowerCase().contains(GetInput("New publicity", false).toLowerCase()); break;
			case 5:
				try {
					changedValue = Integer.parseInt(GetInput("New difficulty", false));
				} catch (Exception e) {
					System.out.println("Bad input"); changedValue = oldQuestion.GetDifficulty();
				} break;
			case 6:
				// If change is question specific, call question creation helpers from CreateQuizMenuController
				switch (oldQuestion.GetType()) {
					case MultipleChoice: newQuestion = CreateQuizMenuController.CreateMultipleChoiceQuestion(oldQuestion.GetQuestion(),
						oldQuestion.getTopicsTable(), oldQuestion.GetResource(), oldQuestion.GetDifficulty()); break;
					case Associative: newQuestion = CreateQuizMenuController.CreateAssociativeQuestion(oldQuestion.GetQuestion(),
						oldQuestion.getTopicsTable(), oldQuestion.GetResource(), oldQuestion.GetDifficulty()); break;
					case Open: newQuestion = CreateQuizMenuController.CreateOpenQuestion(oldQuestion.GetQuestion(),
						oldQuestion.getTopicsTable(), oldQuestion.GetResource(), oldQuestion.GetDifficulty()); break;
				}
		}
		
		// Construct new question if it wasn't constructed by CreateQuizMenuController helpers
		if (newQuestion == null) {
			newQuestion = CreateNewQuestion(oldQuestion, change, changedValue);
		}
		
		
		return newQuestion;
	}
	
	
	
	/**
	 * Helper function to create edited question with old question and new attribute
	 * @param oldQuestion   Question to edit
	 * @param change        Change index (attribute)
	 * @param changedValue  New value of the attribute
	 * @return              New Question object
	 */
	public static Question CreateNewQuestion(Question oldQuestion, int change, Object changedValue) {
		Question newQuestion = null;
		
		
		// Use related constructor according to question type
		switch (oldQuestion.GetType()) {
			case MultipleChoice: newQuestion = new MultipleChoiceQuestion(
				oldQuestion.GetID(),
				change == 1 ? ((String) changedValue) : oldQuestion.GetQuestion(),
				change == 2 ? CreateQuizMenuController.GetTopics((String) changedValue) : oldQuestion.GetTopics(),
				change == 3 ? ((String) changedValue) : oldQuestion.GetResource(), oldQuestion.GetType(),
				change == 4 ? ((boolean) changedValue) : oldQuestion.GetPublicity(),
				change == 5 ? ((int) changedValue) : oldQuestion.GetDifficulty(),
				oldQuestion.GetCorrectAnswers(),
				oldQuestion.GetFalseAnswers(),
				oldQuestion.GetOwner(),
				((MultipleChoiceQuestion) oldQuestion).GetCorrectAnswer(),
				((MultipleChoiceQuestion) oldQuestion).GetAnswers());
				break;
				
			case Associative: newQuestion = new AssociativeQuestion(
				oldQuestion.GetID(),
				change == 1 ? ((String) changedValue) : oldQuestion.GetQuestion(),
				change == 2 ? CreateQuizMenuController.GetTopics((String) changedValue) : oldQuestion.GetTopics(),
				change == 3 ? ((String) changedValue) : oldQuestion.GetResource(), oldQuestion.GetType(),
				change == 4 ? ((boolean) changedValue) : oldQuestion.GetPublicity(),
				change == 5 ? ((int) changedValue) : oldQuestion.GetDifficulty(),
				oldQuestion.GetCorrectAnswers(),
				oldQuestion.GetFalseAnswers(),
				oldQuestion.GetOwner(),
				((AssociativeQuestion) oldQuestion).GetLeftColumn(),
				((AssociativeQuestion) oldQuestion).GetRightColumn());
				break;
				
			case Open: newQuestion = new OpenQuestion(
				oldQuestion.GetID(),
				change == 1 ? ((String) changedValue) : oldQuestion.GetQuestion(),
				change == 2 ? CreateQuizMenuController.GetTopics((String) changedValue) : oldQuestion.GetTopics(),
				change == 3 ? ((String) changedValue) : oldQuestion.GetResource(), oldQuestion.GetType(),
				change == 4 ? ((boolean) changedValue) : oldQuestion.GetPublicity(),
				change == 5 ? ((int) changedValue) : oldQuestion.GetDifficulty(),
				oldQuestion.GetCorrectAnswers(),
				oldQuestion.GetFalseAnswers(),
				oldQuestion.GetOwner(),
				((OpenQuestion) oldQuestion).GetTips());
				break;
		}
		
		return newQuestion;
	}
	
	
	
	/**
	 * GUI save button action
	 * @param event ActionEvent created by GUI
	 */
	public void SaveButton(ActionEvent event) {
		// Check if new question is valid
		if (CreateQuizMenuController.IsQuestionValid(GetQuestionTypeByTab(), questionText, mcqAllChoices, associativeLeft, associativeRight)) {
			// Get new question
			Question newQuestion = CreateQuestion(event);
			
			// Save to database
			DatabaseManager.getInstance().ChangeQuestion(((Question) questionTable.getSelectionModel().getSelectedItem()), newQuestion);
			DatabaseManager.getInstance().UpdateAllQuizzes();
			
			// Update the screen
			QuestionSearchButton(event);
			QuizSearchButton(event);
			ChangeNavigation(event);
		}
	}
	
	
	
	/**
	 * GUI remove button action
	 * @param event ActionEvent created by GUI
	 */
	public void RemoveButton(ActionEvent event) {
		// Check if there is a selected question
		if (questionTable.getSelectionModel().getSelectedItem() != null) {
			// Get selected question
			selectedQuestionID = ((Question) questionTable.getSelectionModel().getSelectedItem()).GetID();
			
			// Update database
			DatabaseManager.getInstance().DeleteQuestionByID(selectedQuestionID, true);
			DatabaseManager.getInstance().UpdateAllQuizzes();
			
			// Update screen
			QuestionSearchButton(event);
			QuizSearchButton(event);
		}
	}
	
	
	
	/**
	 * GUI remove from quiz button action
	 * @param event ActionEvent created by GUI
	 */
	public void RemoveFromQuizButton(ActionEvent event) {
		// Check if there is a selected question
		if (questionTable.getSelectionModel().getSelectedItem() != null) {
			// Get selected question
			selectedQuestionID = ((Question) questionTable.getSelectionModel().getSelectedItem()).GetID();
			
			// Update database
			DatabaseManager.getInstance().DeleteQuestionFromQuiz(selectedQuizID, selectedQuestionID);
			DatabaseManager.getInstance().UpdateAllQuizzes();
			
			// Update screen
			QuestionSearchButton(event);
			QuizSearchButton(event);
		}
	}
	
	
	
	/**
	 * Method to get resource path from file selector
	 * @param event ActionEvent created by GUI
	 */
	public void SelectResourcePath(ActionEvent event) {
		resourcePath.setText(FileSelector());
	}
	
	
	
	/**
	 * Method to build the scene with a question object
	 * @param event ActionEvent created by GUI
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
			ChangeNavigation(event);
			selectedQuizID = ((Quiz) quizTable.getSelectionModel().getSelectedItem()).GetID();
			QuestionSearchButton(event);
		}
	}
	
	
	
	/**
	 * If there is a selected question, changes scene to question edit menu
	 * @param event ActionEvent created by GUI
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
		ChangeNavigation(event, questionTypeTabs);
		QuizSearchButton(event);
	}
	
	
	
	
	public void ChangeNavigation(ActionEvent event) {
		ChangeNavigation(event, tabs);
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
		quizTable.setItems(SearchHelper(quizzes, searchCriteriaTextQuiz.getText(), searchCriteriaChoiceQuiz.getSelectionModel().getSelectedItem().toString()));
	}
	
	
	
	/**
	 * Button action to search question
	 * @param event ActionEvent created by GUI
	 */
	public void QuestionSearchButton(ActionEvent event) {
		List<Question> questions = DatabaseManager.getInstance().GetQuestionsByQuizID(selectedQuizID);
		questionTable.setItems(SearchHelper(questions, searchCriteriaTextQuestion.getText(), searchCriteriaChoiceQuestion.getSelectionModel().getSelectedItem().toString()));
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
	 * @return      Created question
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
