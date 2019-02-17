package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.*;
import com.layso.quizmanager.services.DatabaseManager;
import com.layso.quizmanager.services.QuizManager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;



public class CreateQuizMenuController extends Controller implements Initializable {
	@FXML
	private TabPane quizTypeTabs;
	
	@FXML
	private Label questionInvalidError, quizTitleError, questionCountText, averageDifficultyText, noQuestionError;
	
	@FXML
	private RadioButton customDifficulty, publicButton;
	
	@FXML
	private VBox menuButtons;
	
	@FXML
	private Pane finalDialog;
	
	@FXML
	private Slider difficultySlider, customDifficultySlider;
	
	@FXML
	private ChoiceBox searchCriteriaChoice;
	
	@FXML
	private TextField searchCriteriaText;
	
	@FXML
	private TableView table;
	
	@FXML
	private TableColumn questionColumn, topicsColumn, typeColumn, difficultyColumn, trueDifficultyColumn, ownerColumn;
	
	@FXML
	private TabPane tabs;
	
	@FXML
	private TextField questionText, topicsText, resourcePath, mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer,
		mcqCorrectAnswer, associativeLeft1, associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5,
		associativeRight1, associativeRight2, associativeRight3, associativeRight4, associativeRight5, openTipsText,
		quizTitle;
	
	private List<TextField> mcqAllChoices, associativeLeft, associativeRight;
	private List<Question> tempQuestions;
	private List<Integer> selectedQuestions;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		tempQuestions = new ArrayList<>();
		selectedQuestions = new ArrayList<>();
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		AssociateSearchCriteriaWithTable(searchCriteriaChoice, table);
		AssociateTableWithClass(Question.GetPropertyValueFactory(), questionColumn, topicsColumn, typeColumn,
			difficultyColumn, trueDifficultyColumn, ownerColumn);
		SearchButton(null);
		customDifficultySlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				customDifficulty.fire();
			}
		});
		
		mcqAllChoices = CreateTextFieldArray(mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer, mcqCorrectAnswer);
		associativeLeft = CreateTextFieldArray(associativeLeft1, associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5);
		associativeRight = CreateTextFieldArray(associativeRight1, associativeRight2, associativeRight3, associativeRight4, associativeRight5);
		Logger.Log("Quiz Creation Menu initialized", Logger.LogType.INFO);
	}
	
	
	public void ChangeQuestionNavigation(ActionEvent event) {
		if (event == null)
			System.out.println("event null");
		
		if (tabs == null)
			System.out.println("tabs null");
		
		try {
			ChangeNavigation(event, tabs);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * Helper method to create list from TextFields
	 * @param fields    Fields to include in list
	 * @return          ArrayList created from input list
	 */
	public static List<TextField> CreateTextFieldArray(TextField ... fields) {
		List<TextField> list = new ArrayList<>();
		
		
		for (TextField field : fields) {
			list.add(field);
		}
		
		return list;
	}
	
	
	/**
	 * Method to change menu tabs
	 * @param event ActionEvent produced by GUI
	 */
	public void ChangeNavigation(ActionEvent event) {
		ChangeNavigation(event, quizTypeTabs);
		ClearSpecificFields();
	}
	
	
	
	/**
	 * Method to open question selection menu to reuse other questions
	 * @param event ActionEvent produced by GUI
	 */
	public void SelectQuestionButton(ActionEvent event) {
		ChangeScene(event, WindowStage.SelectQuestionMenu);
	}
	
	
	
	/**
	 * Method to return back to main menu
	 * @param event ActionEvent produced by GUI
	 */
	public void QuitButton(ActionEvent event) {
		tempQuestions.clear();
		selectedQuestions.clear();
		ChangeScene(event, WindowStage.MainMenu);
	}
	
	
	
	/**
	 * Closes the final dialog for quiz creation to continue adding questions
	 * @param event ActionEvent produced by GUI
	 */
	public void BackToQuiz(ActionEvent event) {
		finalDialog.setVisible(false);
		menuButtons.setVisible(true);
		Clear(event);
	}
	
	
	
	/**
	 * Checks if quiz title has entered. If it is creates a Quiz object to be saved to database and calls appropriate
	 * method for it. Returns to the main menu when everything is done. Else shows error message
	 * @param event ActionEvent produced by GUI
	 */
	public void SaveQuiz(ActionEvent event) {
		if (quizTitle.getText().equals("")) {
			quizTitleError.setVisible(true);
		}
		
		else {
			// Create quiz, save to database
			Quiz quiz = new Quiz(-1, QuizManager.getInstance().GetUser().GetID(), quizTitle.getText(), tempQuestions,
				customDifficulty.isSelected() ? (int) customDifficultySlider.getValue() : -1, 0, 0,	 publicButton.isSelected());
			
			int quizID = DatabaseManager.getInstance().CreateQuiz(quiz);
			for (int id : selectedQuestions) {
				DatabaseManager.getInstance().AssociateQuizAndQuestion(id, quizID);
			}
			
			DatabaseManager.getInstance().UpdateAllQuizzes();
			ChangeScene(event, WindowStage.MainMenu);
		}
	}
	
	
	
	/**
	 * Checks the question count. If there is any saved question or the scene includes a valid question that is not
	 * saved yet opens up a final dialog to finish quiz creation. Else shows the error message
	 * @param event ActionEvent produced by GUI
	 */
	public void FinalizeQuiz(ActionEvent event) {
		int avg = 0;
		List<Question> questions = new ArrayList(tempQuestions);
		for (int questionID : selectedQuestions) {
			questions.add(DatabaseManager.getInstance().GetQuestionByID(questionID));
		}
		
		
		SaveQuestion(event);
		if (questions.size() == 0) {
			questionInvalidError.setVisible(false);
			noQuestionError.setVisible(true);
		}
		
		else {
			for (Question q : questions) {
				avg += q.GetDifficulty();
			}
			
			finalDialog.setVisible(true);
			menuButtons.setVisible(false);
			questionCountText.setText(Integer.toString(questions.size()));
			averageDifficultyText.setText(String.format("%.2f", avg / (double)questions.size()));
		}
	}
	
	
	
	/**
	 * Checks if the question is valid. If it is, shows the question type by the id of current tab (question type).
	 * Then calls appropriate helper method to create the Question object. Saves new object to questions array, clears
	 * the screen for new question. If it is not valid then shows an error message
	 * @param event ActionEvent produced by GUI
	 */
	public void SaveQuestion(ActionEvent event) {
		Question.QuestionType type = Question.QuestionType.valueOf(quizTypeTabs.getSelectionModel().getSelectedItem().getId());
		Question newQuestion = null;
		
		
		if (IsQuestionValid(type, questionText, mcqAllChoices, associativeLeft, associativeRight)) {
			// Identify question type to get Question object
			switch (Question.QuestionType.valueOf(quizTypeTabs.getSelectionModel().getSelectedItem().getId())) {
				case MultipleChoice: newQuestion = CreateMCQ(questionText.getText(), topicsText, resourcePath.getText(),
					publicButton.isSelected(), ((int) difficultySlider.getValue()), mcqAllChoices, mcqCorrectAnswer.getText());
					break;
					
				case Associative: newQuestion = CreateAssociative(questionText.getText(), topicsText, resourcePath.getText(),
					publicButton.isSelected(), ((int) difficultySlider.getValue()), associativeLeft, associativeRight);
					break;
					
				case Open: newQuestion = CreateOpenQuestion(questionText.getText(), topicsText, resourcePath.getText(),
					publicButton.isSelected(), ((int) difficultySlider.getValue()), openTipsText.getText());
					break;
			}
			
			// Save question and clear screen for new one
			tempQuestions.add(newQuestion);
			Clear(event);
		} else {
			noQuestionError.setVisible(false);
			questionInvalidError.setVisible(true);
		}
	}
	
	
	
	/**
	 * Helper function to create an associative question. Creates the left and right side lists given by user with
	 * interface and calls constructor with proper parameters. Question must be valid to create without problems
	 * @param question      Question text
	 * @param topics        Topics TextField
	 * @param resource      Resource text
	 * @param isPublic      Publicity value
	 * @param difficulty    Difficulty value
	 * @param leftColumn    Left column of GUI with text fields, as List
	 * @param rightColumn   Right column of GUI with text fields, as List
	 * @return              Newly created Associative Question
	 */
	public static AssociativeQuestion CreateAssociative(String question, TextField topics, String resource, boolean isPublic,
	                                             int difficulty, List<TextField> leftColumn, List<TextField> rightColumn) {
		List<String> leftChoices = new ArrayList<>();
		List<String> rightChoices = new ArrayList<>();
		int i;
		
		
		// At this point it is sure the question is valid. Directly add first 2 rows
		for (i=0; i<AssociativeQuestion.MINIMUM_ROW_COUNT; ++i) {
			leftChoices.add(leftColumn.get(i).getText());
			rightChoices.add(rightColumn.get(i).getText());
		}
		
		// Add remaining rows where there is a data
		for (; i<leftColumn.size(); ++i) {
			if (!leftColumn.get(i).getText().equals("")) {
				leftChoices.add(leftColumn.get(i).getText());
				rightChoices.add(rightColumn.get(i).getText());
			}
		}
		
		return new AssociativeQuestion(-1, question, GetTopics(topics), resource, Question.QuestionType.Associative,
			isPublic, difficulty,0, 0, QuizManager.getInstance().GetUser(), leftChoices, rightChoices);
	}
	
	
	
	/**
	 * Helper function to create a multiple choice question. Creates the answer list given by user with interface and
	 * calls constructor with proper parameters
	 * @param question      Question text
	 * @param topics        Topics TextField
	 * @param resource      Resource text
	 * @param isPublic      Publicity value
	 * @param difficulty    Difficulty value
	 * @param allAnswers    List of TextField for all answer fields
	 * @param correctAnswer The answer that is considered as correct
	 * @return              Returns newly created Multiple Choice Question
	 */
	public static MultipleChoiceQuestion CreateMCQ(String question, TextField topics, String resource, boolean isPublic,
	                                         int difficulty, List<TextField> allAnswers, String correctAnswer) {
		List<String> answers = new ArrayList<>();
		
		
		for (TextField field : allAnswers) {
			if (!field.getText().equals(correctAnswer)) {
				answers.add(field.getText());
			}
		}
		
		
		return new MultipleChoiceQuestion(-1, question, GetTopics(topics), resource, Question.QuestionType.MultipleChoice,
			isPublic, difficulty,0, 0, QuizManager.getInstance().GetUser(), correctAnswer, answers);
	}
	
	
	
	/**
	 * Helper function to create an open question
	 * @param question      Question text
	 * @param topics        Topics TextField
	 * @param resource      Resource text
	 * @param isPublic      Publicity value
	 * @param difficulty    Difficulty value
	 * @param tips          Tips for question
	 * @return              Newly created open question
	 */
	public static OpenQuestion CreateOpenQuestion(String question, TextField topics, String resource, boolean isPublic,
	                                       int difficulty, String tips) {
		return new OpenQuestion(-1, question, GetTopics(topics), resource, Question.QuestionType.Open, isPublic,
			difficulty, 0, 0, QuizManager.getInstance().GetUser(), tips);
	}
	
	
	/**
	 * Gets the plain text from topic text field and then splits according to the delimiters. Creates a List out of
	 * the found topics
	 * @return  A list including all topics
	 */
	public static List<String> GetTopics(TextField topicField) {
		List<String> topics = new ArrayList<>();
		
		
		// If there is anything filled, split it with predefined delimiters and add each topic to list
		for (String topic : topicField.getText().split("[ ]|[;]|[-]|[,]|[.]")) {
			if (!topic.equals(""))
				topics.add(topic);
		}
		
		return topics;
	}
	
	
	
	/**
	 * Helper method to check the user input fields to determine if given information is enough to create a question.
	 * Required fields are different for different type of questions. It checks according to the question type
	 * @param type                      Type of the question
	 * @param question                  Question text
	 * @param mcqAnswers                List of all answers for multiple choice
	 * @param associativeLeftFields     Left column of GUI input fields for associated question as list
	 * @param associativeRightFields    Right column of GUI input fields for associated question as list
	 * @return                          Validness of question
	 */
	public static boolean IsQuestionValid(Question.QuestionType type, TextField question, List<TextField> mcqAnswers,
	                                List<TextField> associativeLeftFields, List<TextField> associativeRightFields) {
		boolean notValid = false;
		
		
		// If it is an MCQ, all 4 answer text fields must be filled
		if (type == Question.QuestionType.MultipleChoice) {
			for (TextField field : mcqAnswers) {
				notValid |= field.getText().equals("");
			}
		}
		
		// If it is an associative question first two rows must be filled on both sides. Other rows can be filled but
		// it can't be filled only on 1 side. Using XOR to test this case
		else if (type == Question.QuestionType.Associative) {
			if (associativeLeftFields.size() == associativeRightFields.size()) {
				int i;
				
				for (i=0; i<AssociativeQuestion.MINIMUM_ROW_COUNT; ++i) {
					notValid |= associativeLeftFields.get(i).getText().equals("") || associativeRightFields.get(i).getText().equals("");
				}
				
				for (; i<associativeLeftFields.size(); ++i) {
					notValid |= associativeLeftFields.get(i).getText().equals("") ^ associativeRightFields.get(i).getText().equals("");
				}
			}
			
			else {
				notValid = true;
			}
		}
		
		// No constraints for open question, question text is the only thing required for all of them
		notValid |= question.getText().equals("");
		
		return !notValid;
	}
	
	
	
	/**
	 * Button action method to cancel action and go back to quiz creation menu
	 * @param event ActionEvent created by UI
	 */
	public void BackButton(ActionEvent event) {
		ChangeNavigation(event, tabs);
	}
	
	
	
	/**
	 * Button action method to get selected questions and save them to use for quiz creation
	 * @param event ActionEvent created by UI
	 */
	public void SelectButton(ActionEvent event) {
		for (int i = 0; i<table.getSelectionModel().getSelectedItems().size(); ++i) {
			Question q = ((Question) table.getSelectionModel().getSelectedItems().get(i));
			selectedQuestions.add(q.GetID());
		}
		
		BackButton(event);
	}
	
	
	
	/**
	 * Button action method to filter the questions by given criteria. Calls helper methods according to search criteria
	 * @param event ActionEvent created by UI
	 */
	public void SearchButton(ActionEvent event) {
		List<Question> questions = DatabaseManager.getInstance().GetAllPublicQuestions();
		table.setItems(SearchHelper(questions, searchCriteriaText.getText(), searchCriteriaChoice.getSelectionModel().getSelectedItem().toString()));
	}
	
	
	
	/**
	 * Button action to get path selection
	 * @param event ActionEvent created by GUI
	 */
	public void SelectResourcePath(ActionEvent event) {
		resourcePath.setText(FileSelector());
	}
	
	
	
	/**
	 * Helper to clear the answer fields when question type is changed
	 */
	public void ClearSpecificFields() {
		ClearTextFields(mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer, mcqCorrectAnswer, associativeLeft1,
			associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5, associativeRight1,
			associativeRight2, associativeRight3, associativeRight4, associativeRight5, openTipsText);
	}
	
	
	
	/**
	 * Clears all text fields and sets display property of error messages to false
	 * @param event ActionEvent produced by GUI
	 */
	public void Clear(ActionEvent event) {
		ClearTextFields(questionText, topicsText, resourcePath, mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer,
			mcqCorrectAnswer, associativeLeft1, associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5,
			associativeRight1, associativeRight2, associativeRight3, associativeRight4, associativeRight5, openTipsText);
		questionInvalidError.setVisible(false);
		noQuestionError.setVisible(false);
	}
}
