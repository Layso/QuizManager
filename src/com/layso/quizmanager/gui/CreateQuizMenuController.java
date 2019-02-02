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
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;



public class CreateQuizMenuController extends Controller implements Initializable {
	private List<Question> questions;
	
	
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
	private TextField questionText, topicsText, resourcePath, mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer,
		mcqCorrectAnswer, associativeLeft1, associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5,
		associativeRight1, associativeRight2, associativeRight3, associativeRight4, associativeRight5, openTipsText,
		quizTitle;
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		questions = new ArrayList<>();
		customDifficultySlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				customDifficulty.fire();
			}
		});
		Logger.Log("Quiz Creation Menu initialized", Logger.LogType.INFO);
	}
	
	
	
	/**
	 * Method to change menu tabs
	 * @param event ActionEvent produced by GUI
	 */
	public void ChangeNavigation(ActionEvent event) {
		for (Tab tab : quizTypeTabs.getTabs()) {
			if (tab.getId().equals(((Node) event.getSource()).getId())) {
				quizTypeTabs.getSelectionModel().select(tab);
				Clear(event);
			}
		}
	}
	
	
	
	/**
	 * Method to return back to main menu
	 * @param event ActionEvent produced by GUI
	 */
	public void QuitButton(ActionEvent event) {
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
			int customDifficultyLevel = -1;
			double avarageDifficultyLevel = 0;
			
			if (customDifficulty.isSelected()) {
				customDifficultyLevel = ((int) customDifficultySlider.getValue());
			}
			
			for (Question q : questions) {
				avarageDifficultyLevel += q.GetDifficulty();
			}
			
			// Create quiz, save to database, return to main menu
			Quiz quiz = new Quiz(QuizManager.getInstance().GetUser().GetID(), quizTitle.getText(), questions,
				customDifficultyLevel, 0, avarageDifficultyLevel/questions.size(),
				publicButton.isSelected());
			DatabaseManager.getInstance().CreateQuiz(quiz);
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
		String tab = quizTypeTabs.getSelectionModel().getSelectedItem().getId();
		List<String> topics = GetTopics();
		Question newQuestion;
		
		
		if (IsQuestionValid()) {
			// Identify question type to get Question object
			switch (Question.QuestionType.valueOf(quizTypeTabs.getSelectionModel().getSelectedItem().getId())) {
				case MultipleChoice: newQuestion = CreateMCQ(); break;
				case Associative: newQuestion = CreateAssociative(); break;
				case Open: newQuestion = new OpenQuestion(-1, questionText.getText(), topics,
					resourcePath.getText(), Question.QuestionType.Open, ((int) difficultySlider.getValue()),
					0, 0,	QuizManager.getInstance().GetUser().GetID(),
					openTipsText.getText()); break;
				default: newQuestion = null;
			}
			
			// Save question and clear screen for new one
			questions.add(newQuestion);
			Clear(event);
		} else {
			noQuestionError.setVisible(false);
			questionInvalidError.setVisible(true);
		}
	}
	
	
	
	/**
	 * Helper function to create an associative question. Creates the left and right side lists given by user with
	 * interface and calls constructor with proper parameters
	 * @return  Returns newly created Associative Question
	 */
	public AssociativeQuestion CreateAssociative() {
		List<String> leftColumn = new ArrayList<>();
		List<String> rightColumn = new ArrayList<>();
		
		
		// At this point it is sure the question is valid. Directly add first 2 rows
		leftColumn.add(associativeLeft1.getText());
		rightColumn.add(associativeRight1.getText());
		leftColumn.add(associativeLeft2.getText());
		rightColumn.add(associativeRight2.getText());
		
		
		// Check if there is any other row that is filled. Add the filled rows too
		if (!associativeLeft3.getText().equals("")) {
			leftColumn.add(associativeLeft3.getText());
			rightColumn.add(associativeRight3.getText());
		}
		
		if (!associativeLeft4.getText().equals("")) {
			leftColumn.add(associativeLeft4.getText());
			rightColumn.add(associativeRight4.getText());
		}
		
		if (!associativeLeft5.getText().equals("")) {
			leftColumn.add(associativeLeft5.getText());
			rightColumn.add(associativeRight5.getText());
		}
		
		return new AssociativeQuestion(-1, questionText.getText(), GetTopics(), resourcePath.getText(),
			Question.QuestionType.Associative, ((int) difficultySlider.getValue()), 0, 0,
			QuizManager.getInstance().GetUser().GetID(), leftColumn, rightColumn);
	}
	
	
	
	/**
	 * Helper function to create a multiple choice question. Creates the answer list given by user with interface and
	 * calls constructor with proper parameters
	 * @return  Returns newly created Multiple Choice Question
	 */
	public MultipleChoiceQuestion CreateMCQ() {
		List<String> answers = new ArrayList<>();
		answers.add(mcqFirstAnswer.getText());
		answers.add(mcqSecondAnswer.getText());
		answers.add(mcqThirdAnswer.getText());
		
		return new MultipleChoiceQuestion(-1, questionText.getText(), GetTopics(), resourcePath.getText(),
			Question.QuestionType.MultipleChoice, ((int) difficultySlider.getValue()), 0, 0,
			QuizManager.getInstance().GetUser().GetID(), mcqCorrectAnswer.getText(), answers);
	}
	
	
	
	/**
	 * Gets the plain text from topic text field and then splits according to the delimiters. Creates a List out of
	 * the found topics
	 * @return  A list including all topics
	 */
	public List<String> GetTopics() {
		List<String> topics = new ArrayList<>();
		
		
		// If there is anything filled, split it with predefined delimiters and add each topic to list
		for (String topic : topicsText.getText().split("[ ]|[;]|[-]|[,]|[.]")) {
			if (!topic.equals(""))
				topics.add(topic);
		}
		
		return topics;
	}
	
	
	
	/**
	 * Checks the user input fields to determine if given information is enough to create a question. Required fields
	 * are different for different type of questions. It checks according to the question type
	 * @return  Validness of the question
	 */
	public boolean IsQuestionValid() {
		String tab = quizTypeTabs.getSelectionModel().getSelectedItem().getId();
		boolean valid;
		
		
		// If it is an MCQ, all 4 answer text fields must be filled
		if (tab.equals(Question.QuestionType.MultipleChoice.name())) {
			valid = !(questionText.getText().equals("") || mcqFirstAnswer.getText().equals("") ||
				mcqSecondAnswer.getText().equals("") || mcqThirdAnswer.getText().equals("") ||
				mcqCorrectAnswer.getText().equals(""));
		}
		
		// If it is an associative question first two rows must be filled on both sides. Other rows can be filled but
		// it can't be filled only on 1 side
		else if (tab.equals(Question.QuestionType.Associative.name())) {
			boolean firstRow = associativeLeft1.getText().equals("") || associativeRight1.getText().equals("");
			boolean secondRow = associativeLeft2.getText().equals("") || associativeRight2.getText().equals("");
			boolean thirdRow = associativeLeft3.getText().equals("") ^ associativeRight3.getText().equals("");
			boolean fourthRow = associativeLeft4.getText().equals("") ^ associativeRight4.getText().equals("");
			boolean fifthRow = associativeLeft5.getText().equals("") ^ associativeRight5.getText().equals("");
			
			valid = !(questionText.getText().equals("") || firstRow || secondRow || thirdRow || fourthRow || fifthRow);
		}
		
		// For an open question, question text is the only thing required
		else {
			valid = !questionText.getText().equals("");
		}
		
		return valid;
	}
	
	
	
	/**
	 * Opens a file chooser to let user select an image (only with extensions .png .jpg or .gif) to support question
	 * @param event ActionEvent produced by GUI
	 */
	public void SelectResourcePath(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose image to support the question");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
		);
		File file = fileChooser.showOpenDialog(null);
		resourcePath.setText(file.getAbsolutePath());
	}
	
	
	
	/**
	 * Clears all text fields and sets display property of error messages to false
	 * @param event ActionEvent produced by GUI
	 */
	public void Clear(ActionEvent event) {
		questionText.setText("");
		topicsText.setText("");
		resourcePath.setText("");
		mcqFirstAnswer.setText("");
		mcqSecondAnswer.setText("");
		mcqThirdAnswer.setText("");
		mcqCorrectAnswer.setText("");
		associativeLeft1.setText("");
		associativeLeft2.setText("");
		associativeLeft3.setText("");
		associativeLeft4.setText("");
		associativeLeft5.setText("");
		associativeRight1.setText("");
		associativeRight2.setText("");
		associativeRight3.setText("");
		associativeRight4.setText("");
		associativeRight5.setText("");
		openTipsText.setText("");
		questionInvalidError.setVisible(false);
		noQuestionError.setVisible(false);
	}
}
