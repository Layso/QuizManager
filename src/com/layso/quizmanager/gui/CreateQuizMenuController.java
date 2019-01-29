package com.layso.quizmanager.gui;

import com.layso.quizmanager.datamodel.AssociativeQuestion;
import com.layso.quizmanager.datamodel.MultipleChoiceQuestion;
import com.layso.quizmanager.datamodel.OpenQuestion;
import com.layso.quizmanager.datamodel.Question;

import com.layso.quizmanager.services.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateQuizMenuController {
	@FXML
	TabPane quizTypeTabs;
	
	@FXML
	TextField questionText, topicsText, resourcePath;
	
	@FXML
	TextField mcqFirstAnswer, mcqSecondAnswer, mcqThirdAnswer, mcqCorrectAnswer;
	
	@FXML
	TextField associativeLeft1, associativeLeft2, associativeLeft3, associativeLeft4, associativeLeft5;
	@FXML
	TextField associativeRight1, associativeRight2, associativeRight3, associativeRight4, associativeRight5;
	
	@FXML
	TextField openTipsText;
	
	List<Question> quiz;
	
	public void ChangeNavigation(ActionEvent event) {
		for (Tab tab : quizTypeTabs.getTabs()) {
			if (tab.getId().equals(((Node) event.getSource()).getId())) {
				quizTypeTabs.getSelectionModel().select(tab);
				Clear(event);
			}
		}
	}
	
	
	public void SaveQuiz(ActionEvent event) {
		// TODO
		// Show a config window (public, question count, etc)
		// Write function on database manager to save quiz, call it (don't forget resource)
		// Return to main menu
	}
	
	
	public void SaveQuestion(ActionEvent event) {
		String tab = quizTypeTabs.getSelectionModel().getSelectedItem().getId();
		List<String> topics = GetTopics();
		Question newQuestion;
		
		if (IsQuestionValid()) {
			if (tab.equals(Question.QuestionType.MultipleChoice.name())) {
				newQuestion = CreateMCQ();
			}
			
			else if (tab.equals(Question.QuestionType.Associative.name())) {
				newQuestion = CreateAssociative();
			}
			
			else {
				newQuestion = new OpenQuestion(-1, questionText.getText(), topics, !resourcePath.getText().equals(""), Question.QuestionType.Open, openTipsText.getText());
			}
			
			
			if (quiz == null)
				quiz = new ArrayList<>();
			
			quiz.add(newQuestion);
			Clear(event);
		}
		
		else {
			// TODO: show error message
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
		
		leftColumn.add(associativeLeft1.getText());
		leftColumn.add(associativeLeft2.getText());
		leftColumn.add(associativeLeft3.getText());
		leftColumn.add(associativeLeft4.getText());
		leftColumn.add(associativeLeft5.getText());
		
		rightColumn.add(associativeRight1.getText());
		rightColumn.add(associativeRight2.getText());
		rightColumn.add(associativeRight3.getText());
		rightColumn.add(associativeRight4.getText());
		rightColumn.add(associativeRight5.getText());
		
		return new AssociativeQuestion(-1, questionText.getText(), GetTopics(), !resourcePath.getText().equals(""), Question.QuestionType.Associative, leftColumn, rightColumn);
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
		
		return new MultipleChoiceQuestion(-1, questionText.getText(), GetTopics(), !resourcePath.getText().equals(""), Question.QuestionType.MultipleChoice, mcqCorrectAnswer.getText(), answers);
	}
	
	
	/**
	 * Gets the plain text from topic text field and then splits according to the delimiters. Creates a List out of
	 * the found topics
	 * @return  A list including all topics
	 */
	public List<String> GetTopics() {
		List<String> topics = new ArrayList<>();
		
		
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
		
		
		if (tab.equals(Question.QuestionType.MultipleChoice.name())) {
			valid = !(questionText.getText().equals("") || mcqFirstAnswer.getText().equals("") || mcqSecondAnswer.getText().equals("") || mcqThirdAnswer.getText().equals("") || mcqCorrectAnswer.getText().equals(""));
		}
		
		else if (tab.equals(Question.QuestionType.Associative.name())) {
			boolean firstRow = associativeLeft1.getText().equals("") || associativeRight1.getText().equals("");
			boolean secondRow = associativeLeft2.getText().equals("") ^ associativeRight2.getText().equals("");
			boolean thirdRow = associativeLeft3.getText().equals("") ^ associativeRight3.getText().equals("");
			boolean fourthRow = associativeLeft4.getText().equals("") ^ associativeRight4.getText().equals("");
			boolean fifthRow = associativeLeft5.getText().equals("") ^ associativeRight5.getText().equals("");
			
			valid = !(questionText.getText().equals("") || firstRow || secondRow || thirdRow || fourthRow || fifthRow);
		}
		
		else {
			valid = !questionText.getText().equals("");
		}
		
		
		return valid;
	}
	
	
	
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
	}
}
