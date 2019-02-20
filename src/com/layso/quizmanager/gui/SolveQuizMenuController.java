package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.*;
import com.layso.quizmanager.services.DatabaseManager;
import com.layso.quizmanager.services.QuizManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.*;

public class SolveQuizMenuController extends Controller implements Initializable {
	@FXML
	ChoiceBox searchCriteriaChoiceQuiz;
	
	@FXML
	ToggleGroup mcqSelectionGroup;
	
	@FXML
	TextField searchCriteriaTextQuiz;
	
	@FXML
	TableView quizTable;
	
	@FXML
	TableColumn nameColumnQuiz, questionCountColumnQuiz, difficultyColumnQuiz, trueDifficultyColumnQuiz;
	
	@FXML
	TabPane tabs, questionTabs;
	
	@FXML
	Label totalQuestionCount, currentQuestionCount, questionTextLabel, openTipLabel, mcqChoice1, mcqChoice2, mcqChoice3, mcqChoice4, errorMessage;
	
	@FXML
	VBox leftVBox, rightVBox;
	
	@FXML
	TextArea openUserInput;
	
	@FXML
	AnchorPane background;
	
	@FXML
	Pane endPrompt;
	
	@FXML
	Button associativeLeftSelection1, associativeLeftSelection2, associativeLeftSelection3, associativeLeftSelection4, associativeLeftSelection5, associativeRightSelection1,
		associativeRightSelection2, associativeRightSelection3, associativeRightSelection4, associativeRightSelection5;
	
	private static final int STROKE_WIDTH = 5;
	private static final String STROKE_COLOR = "white";
	
	private static List<Answer> answers;
	private static Quiz quiz;
	private static List<Question> questions;
	private static int currentQuestion;
	List<Label> mcqChoices;
	List<Button> leftSelections;
	List<Button> rightSelections;
	List<Line> lines;
	Button lastClickedAssociative;
	List<Button> unusedLeftSelections;
	List<Button> unusedRightSelections;
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Constructing the TableView elements by associating with classes
		AssociateSearchCriteriaWithTable(searchCriteriaChoiceQuiz, quizTable);
		AssociateTableWithClass(Quiz.GetPropertyValueFactory(), nameColumnQuiz, questionCountColumnQuiz, difficultyColumnQuiz, trueDifficultyColumnQuiz);
		QuizSearchButton(null);
		
		lines = new ArrayList<>();
		answers = new ArrayList<>();
		mcqChoices = new ArrayList<>();
		leftSelections = new ArrayList<>();
		rightSelections = new ArrayList<>();
		unusedLeftSelections = new ArrayList<>();
		unusedRightSelections = new ArrayList<>();
		mcqChoices.addAll(Arrays.asList(mcqChoice1, mcqChoice2, mcqChoice3, mcqChoice4));
		leftSelections.addAll(Arrays.asList(associativeLeftSelection1, associativeLeftSelection2,
			associativeLeftSelection3, associativeLeftSelection4, associativeLeftSelection5));
		rightSelections.addAll(Arrays.asList(associativeRightSelection1, associativeRightSelection2,
			associativeRightSelection3, associativeRightSelection4, associativeRightSelection5));
		
		Logger.Log("Solve Quiz Menu initialized", Logger.LogType.INFO);
	}
	
	
	public static void SolveQuizMenu() {
		List<Quiz> quizzes = DatabaseManager.getInstance().GetAllPublicQuizzes();
		answers = new ArrayList<>();
		boolean correctInput;
		boolean run = true;
		int selection;
		
		
		while (run) {
			do {
				System.out.println("Quizzes: Quiz Title - Question Count - Difficulty - True Difficulty");
				PrintArrayAsTable(quizzes);
				System.out.println();
				PrintMenu("Select Quiz", "Search Quiz", "Back");
				switch (GetMenuInput()) {
					case 1: correctInput = true;
						if (quizzes.size() > 0) {
							do {selection = GetMenuInput();} while(selection < 1 || selection > quizzes.size());
							quiz = quizzes.get(selection-1);
							questions = quiz.GetQuestions();
							currentQuestion = 0;
							SolveQuiz();
						}
						else {
							System.out.println("No quiz to solve");
						}
						break;
					case 2: correctInput = true; quizzes = Search(); break;
					case 3: correctInput = true; run = false; QuizManager.getInstance().SetCurrentStage(WindowStage.MainMenu); break;
					default: correctInput = false;
				}
			} while (!correctInput);
		}
	}
	
	public static void SolveQuiz() {
		for (int i=0; i<questions.size(); ++i) {
			currentQuestion = i+1;
			System.out.println();
			System.out.println(QuestionToString(questions.get(i)));
			answers.add(GetAnswerForQuestion(questions.get(i)));
		}
		
		System.out.println("Congratulations, you finished the quiz!");
		if (!DatabaseManager.getInstance().DidUserSolveQuiz(quiz.GetID(), QuizManager.getInstance().GetUser().GetID())) {
			SaveAllAnswers();
		}
	}
	
	public static Answer GetAnswerForQuestion(Question question) {
		Answer answer = null;
		
		
		System.out.println(currentQuestion + ") " + question.GetQuestion());
		if (question.GetType() == Question.QuestionType.MultipleChoice) {
			List<String> allAnswers = new ArrayList<>(((MultipleChoiceQuestion) question).GetAnswers());
			allAnswers.add(((MultipleChoiceQuestion) question).GetCorrectAnswer());
			
			Collections.shuffle(allAnswers);
			System.out.println();
			for (int i=0; i<allAnswers.size(); ++i) {
				System.out.println("[" + (i+1) + "] " + allAnswers.get(i));
			}
			
			int selection;
			do { selection = GetMenuInput(); } while(selection < 1 || selection > allAnswers.size());
			
			answer = new MultipleChoiceAnswer(quiz.GetID(), question.GetID(), QuizManager.getInstance().GetUser(), allAnswers.get(selection-1));
		}
		
		else if (question.GetType() == Question.QuestionType.Associative) {
			List<String> left = new ArrayList<>(((AssociativeQuestion) question).GetLeftColumn());
			List<String> right = new ArrayList<>(((AssociativeQuestion) question).GetRightColumn());
			
			Collections.shuffle(left);
			Collections.shuffle(right);
			
			System.out.println();
			for (int i=0; i<left.size(); ++i) {
				System.out.println("[" + (i+1) + "] " + left.get(i) + "\t\t\t[" + (char)('a' + i) + "] " + right.get(i));
			}
			
			String answers;
			boolean inputCorrect = false;
			List<String> leftAnswers = new ArrayList<>();
			List<String> rightAnswers = new ArrayList<>();
			
			do {
				answers = GetInput("Associate", true);
				String[] rows = answers.split("[ ]");
				if (rows.length == left.size()) {
					for (String row : rows) {
						System.out.println("Row - *" + row + "*");
						String[] doubles = row.split("[-]");
						
						if (doubles.length == 2) {
							try {
								
								leftAnswers.add(left.get(Integer.parseInt(doubles[0]) - 1));
								rightAnswers.add(right.get(doubles[1].charAt(0) - 'a'));
								inputCorrect = true;
							} catch (Exception e) {
								System.out.println(e);
								System.out.println("Input format not correct");
								inputCorrect = false;
							}
						}
					}
				}
			} while (!inputCorrect);
			
			answer = new AssociativeAnswer(quiz.GetID(), question.GetID(), QuizManager.getInstance().GetUser(), leftAnswers, rightAnswers);
		}
		
		else {
			System.out.println();
			System.out.println("(TIP: " + ((OpenQuestion) question).GetTips() + ")");
			System.out.println();
			String input;
			do {input = GetInput("Answer", true);} while (input.equals(""));
			
			answer = new OpenAnswer(quiz.GetID(), question.GetID(), QuizManager.getInstance().GetUser(), input);
		}
		
		return answer;
	}
	
	public static String QuestionToString(Question questionToConvert) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(questionToConvert.getQuestionTextTable());
		switch (questionToConvert.GetType()) {
			case MultipleChoice:
		}
		
		
		return builder.toString();
	}
	
	
	public static List<Quiz> Search() {
		String searchCriteria = GetInput("Search criteria", true);
		Quiz.QuizSearchTerms termEnum;
		int searchTerm;
		
		do {
			System.out.println("[1] Name\n[2] QuestionCount\n[3] Difficulty\n[4] TrueDifficulty");
			searchTerm = GetMenuInput();
		} while (searchTerm < 1 || searchTerm > 4);
		
		termEnum = searchTerm == 1 ? Quiz.QuizSearchTerms.Name : (searchTerm == 2 ? Quiz.QuizSearchTerms.QuestionCount : (searchTerm == 3 ? Quiz.QuizSearchTerms.Difficulty : Quiz.QuizSearchTerms.TrueDifficulty));
		return new ArrayList(Controller.SearchHelper(DatabaseManager.getInstance().GetAllUsers(), searchCriteria, termEnum.name()));
	}
	
	public void QuizSearchButton(ActionEvent event) {
		List<Quiz> quizzes = DatabaseManager.getInstance().GetAllPublicQuizzes();
		quizTable.setItems(SearchHelper(quizzes, searchCriteriaTextQuiz.getText(), searchCriteriaChoiceQuiz.getSelectionModel().getSelectedItem().toString()));
	}
	
	
	public void SelectQuizButton(ActionEvent event) {
		if (quizTable.getSelectionModel().getSelectedItem() != null) {
			ChangeNavigation(event, tabs);
			quiz = ((Quiz) quizTable.getSelectionModel().getSelectedItem());
			questions = quiz.GetQuestions();
			currentQuestion = 0;
			BuildQuestionMenu(questions.get(currentQuestion));
			totalQuestionCount.setText(Integer.toString(questions.size()));
			Clear();
		}
	}
	
	public void QuizBackButton(ActionEvent event) {
		ChangeScene(event, WindowStage.MainMenu);
	}
	
	public void NextQuestionButton(ActionEvent event) {
		if (IsQuestionAnswered()) {
			++currentQuestion;
			SaveAnswer();
			Clear();
			
			
			if (currentQuestion >= questions.size()) {
				endPrompt.setVisible(true);
				SaveAllAnswers();
			} else {
				BuildQuestionMenu(questions.get(currentQuestion));
			}
		} else {
			errorMessage.setVisible(true);
		}
	}
	
	private static void SaveAllAnswers() {
		int trueCount = 0;
		int notCorrectedCount = 0;
		
		
		for (int i=0; i<answers.size(); ++i) {
			Question current = questions.get(i);
			
			if (current.GetType() == Question.QuestionType.MultipleChoice || current.GetType() == Question.QuestionType.Associative) {
				AutoCorrectable correctable = ((AutoCorrectable) current);
				Question newStats = current;
				
				if (correctable.CheckAnswer(answers.get(i))) {
					newStats.IncreaseCorrectAnswers();
					++trueCount;
				} else {
					newStats.IncreaseFalseAnwers();
				}
				
				DatabaseManager.getInstance().ChangeQuestion(current, newStats);
			} else  {
				++notCorrectedCount;
				DatabaseManager.getInstance().SaveNotCorrected(((OpenQuestion) current), ((OpenAnswer) answers.get(i)));
			}
		}
		
		DatabaseManager.getInstance().SaveAnswer(quiz.GetID(), QuizManager.getInstance().GetUser().GetID(),
			notCorrectedCount, trueCount, questions.size() - (trueCount + notCorrectedCount));
		DatabaseManager.getInstance().UpdateAllQuizzes();
	}
	
	public void QuitQuizButton (ActionEvent event) {
		ChangeNavigation(event, tabs);
	}
	
	public void AssociativeClick (ActionEvent event) {
		Button clickedAssociative = ((Button) event.getSource());
		
		clickedAssociative.setDisable(true);
		if (lastClickedAssociative == null) {
			lastClickedAssociative = clickedAssociative;
		}
		
		else if (!((leftSelections.contains(lastClickedAssociative) && leftSelections.contains(clickedAssociative)) ||
			(rightSelections.contains(lastClickedAssociative) && rightSelections.contains(clickedAssociative)))) {
			Bounds firstBound;
			Bounds secondBound;
			
			
			if (leftSelections.contains(clickedAssociative)) {
				firstBound = clickedAssociative.localToScene(clickedAssociative.getBoundsInLocal());
				secondBound = lastClickedAssociative.localToScene(clickedAssociative.getBoundsInLocal());
			}
			
			else {
				firstBound = lastClickedAssociative.localToScene(lastClickedAssociative.getBoundsInLocal());
				secondBound = clickedAssociative.localToScene(clickedAssociative.getBoundsInLocal());
			}
			
			Line line = new Line(firstBound.getMaxX(),(firstBound.getMaxY()+firstBound.getMinY())/2,
				secondBound.getMinX(), (secondBound.getMaxY()+secondBound.getMinY())/2);
			
			
			line.strokeProperty().set(Paint.valueOf(STROKE_COLOR));
			line.setStrokeWidth(STROKE_WIDTH);
			lines.add(line);
			background.getChildren().add(line);
			lastClickedAssociative = null;
		}
		
		else {
			clickedAssociative.setDisable(false);
		}
	}
	
	
	private void BuildQuestionMenu(Question question) {
		questionTextLabel.setText(question.GetQuestion());
		currentQuestionCount.setText(Integer.toString(currentQuestion+1));
		
		
		switch (question.GetType()) {
			case MultipleChoice: BuildMultipleChoice((MultipleChoiceQuestion) question); break;
			case Associative: BuildAssociative((AssociativeQuestion) question); break;
			case Open: openTipLabel.setText(((OpenQuestion) question).GetTips()); break;
		}
		
		for (Tab tab: questionTabs.getTabs()) {
			if (question.GetType().name().toLowerCase().contains(tab.getId().toLowerCase())) {
				questionTabs.getSelectionModel().select(tab);
			}
		}
	}
	
	private void BuildAssociative(AssociativeQuestion question) {
		AssociativeQuestion associative = question;
		List<String> left = new ArrayList<>(question.GetLeftColumn());
		List<String> right = new ArrayList<>(question.GetRightColumn());
		
		
		Collections.shuffle(left);
		Collections.shuffle(right);
		ResizeAssociativeSelections(left.size());
		
		for (int i = 0; i < left.size(); i++) {
			leftSelections.get(i).setText(left.get(i));
			rightSelections.get(i).setText(right.get(i));
		}
	}
	
	private void BuildMultipleChoice(MultipleChoiceQuestion question) {
		MultipleChoiceQuestion mcq = question;
		List<String> choices = mcq.GetAnswers();
		choices.add(mcq.GetCorrectAnswer());
		
		Collections.shuffle(choices);
		for (int i = 0; i < mcqChoices.size(); i++) {
			mcqChoices.get(i).setText(choices.get(i));
		}
	}
	
	private void ResizeAssociativeSelections(int rowCount) {
		while (rowCount > leftSelections.size()) {
			Button buttonLeft = unusedLeftSelections.get(0);
			unusedLeftSelections.remove(buttonLeft);
			leftVBox.getChildren().add(buttonLeft);
			leftSelections.add(buttonLeft);
			
			Button buttonRight = unusedRightSelections.get(0);
			unusedRightSelections.remove(buttonRight);
			rightVBox.getChildren().add(buttonRight);
			rightSelections.add(buttonRight);
		}
		
		while (rowCount < leftSelections.size()) {
			Button buttonLeft = leftSelections.get(0);
			leftSelections.remove(buttonLeft);
			leftVBox.getChildren().remove(buttonLeft);
			unusedLeftSelections.add(buttonLeft);
			
			Button buttonRight = rightSelections.get(0);
			rightSelections.remove(buttonRight);
			rightVBox.getChildren().remove(buttonRight);
			unusedRightSelections.add(buttonRight);
		}
	}
	
	
	private boolean IsQuestionAnswered() {
		return mcqSelectionGroup.getSelectedToggle() != null || lines.size() == leftVBox.getChildren().size() || !openUserInput.getText().equals("");
	}
	
	private void Clear() {
		if (mcqSelectionGroup.getSelectedToggle() != null) {
			mcqSelectionGroup.getSelectedToggle().selectedProperty().setValue(false);
		}
		
		for (Line line : lines) {
			background.getChildren().remove(line);
		}
		
		for (int i=0; i<leftSelections.size(); ++i) {
			leftSelections.get(i).setDisable(false);
			rightSelections.get(i).setDisable(false);
		}
		
		for (int i=0; i<unusedLeftSelections.size(); ++i) {
			unusedLeftSelections.get(i).setDisable(false);
			unusedRightSelections.get(i).setDisable(false);
		}
		
		errorMessage.setVisible(false);
		lines.clear();
		openUserInput.setText("");
	}
	
	private void SaveAnswer() {
		Question question = questions.get(currentQuestion-1);
		Answer answer;
		
		
		if (question.GetType() == Question.QuestionType.MultipleChoice) {
			RadioButton selected = ((RadioButton) mcqSelectionGroup.getSelectedToggle());
			String mcqAnswer = "";
			
			
			for (Label label : mcqChoices) {
				if (selected.getId().contains(label.getId())) {
					mcqAnswer = label.getText();
				}
			}
			
			answer = new MultipleChoiceAnswer(quiz.GetID(), question.GetID(), QuizManager.getInstance().GetUser(), mcqAnswer);
		}
		
		else if (question.GetType() == Question.QuestionType.Associative) {
			List<String> leftAnswer = new ArrayList<>(), rightAnswer = new ArrayList<>();
			
			for (Line line : lines) {
				Double startY = line.getStartY();
				Double endY = line.getEndY();
				
				
				for (int i=0; i<leftSelections.size(); ++i) {
					Bounds boundLeft = leftSelections.get(i).localToScene(leftSelections.get(i).getBoundsInLocal());
					Bounds boundRight = rightSelections.get(i).localToScene(rightSelections.get(i).getBoundsInLocal());
					
					
					if (boundLeft.getMinY() < startY && startY < boundLeft.getMaxY()) {
						leftAnswer.add(leftSelections.get(i).getText());
					}
					
					if (boundRight.getMinY() < endY && endY < boundRight.getMaxY()) {
						rightAnswer.add(rightSelections.get(i).getText());
					}
				}
			}
			
			answer = new AssociativeAnswer(quiz.GetID(), question.GetID(), QuizManager.getInstance().GetUser(), leftAnswer, rightAnswer);
		}
		
		else {
			answer = new OpenAnswer(quiz.GetID(), question.GetID(), QuizManager.getInstance().GetUser(), openUserInput.getText());
		}
		
		
		answers.add(answer);
	}
}
