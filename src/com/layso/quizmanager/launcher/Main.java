package com.layso.quizmanager.launcher;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.User;
import com.layso.quizmanager.services.DatabaseManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class Main extends Application {
	private static final int REQUIRED_ARGUMENT_COUNT = 1;
	private static final int REQUIRED_CONFIG_ELEMENT = 4;
	private static final int CFG_DB_URL_INDEX = 0;
	private static final int CFG_DB_USR_INDEX = 1;
	private static final int CFG_DB_PASS_INDEX = 2;
	private static final int CFG_LOG_FILENAME_INDEX = 3;
	
	
	public enum SceneTypes {Login, Main, CreateQuiz}
	public enum LoginMenuNavigation {Login, Register, Guest}
	//public enum QuestionTypes{MultipleChoice, Open, Associative}
	
	
	private String inputFieldColorCSS = "-fx-background-color: rgb(220,220,220); -fx-prompt-text-fill: rgb(100,100,100)";
	private String menuBackgroundColorCSS = "-fx-background-color: rgb(40,40,40)";
	private String buttonHoverColorCSS = "-fx-background-color: rgb(85, 85, 85)";
	private String activeTabColorCSS = "-fx-background-color: rgb(125,125,125)";
	private String inactiveTabColorCSS = "-fx-background-color: rgb(75,75,75)";
	private Color buttonTextColor = Color.rgb(255,255,255);
	private Color textColor = Color.rgb(200,200,200);
	
	int width, height;
	
	private User user;
	private DatabaseManager dbManager;
	private Stage stage;
	
	
	/* Login Menu UI elements */
	private Scene loginMenuScene;
	private LoginMenuNavigation currentNavigation;
	private Button bt_clear, bt_submit, bt_nav_login, bt_nav_register, bt_nav_guest;
	private TextField tf_username;
	private PasswordField pf_password;
	private Text txt_menuName, txt_infoMessage;
	private Image img_gameLogo;
	
	/* Main Menu UI elements */
	private Scene mainMenuScene;
	private Button bt_playQuiz, bt_createQuiz, bt_quit, bt_promoteUsers, bt_createSession, bt_joinSession, bt_logOut, bt_history;
	private Text txt_username;
	
	/* Quiz Creation Menu UI elements */
	private Scene quizCreationMenu;
	//private QuestionTypes createQuestionType;
	
	
	public static void main(String[] args) {
		launch(args);
		/*List<String> configElements;
		DatabaseManager dbManager;
		
		
		if (!CheckArguments(args)) {
			System.out.println("Usage: ./progName [configFile]");
			System.out.println("[configFile] -> File that contains required data to run program");
		}
		
		else {
			configElements = ReadConfigFile(args);
			dbManager = new DatabaseManager();
			Logger.Setup(configElements.get(CFG_LOG_FILENAME_INDEX), true, true);
			dbManager.Connect(configElements.get(CFG_DB_URL_INDEX), configElements.get(CFG_DB_USR_INDEX), configElements.get(CFG_DB_PASS_INDEX));
			launch();
		}*/
	}
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		
		/*
		primaryStage.setScene(new Scene(PrepareQuizCreationMenu(), 1024, 768));
		primaryStage.show();
		*/
		/*
		width = 1024;
		height = 768;
		stage = primaryStage;
		loginMenuScene = new Scene(PrepareLoginMenuLayout(), width, height);
		quizCreationMenu = new Scene(PrepareQuizCreationMenu(), width, height);
		
		primaryStage.setTitle("Quizmania - Learn, practice, achieve");
		primaryStage.setScene(quizCreationMenu);
		primaryStage.show();
		
		
		ChangeLoginMenuNavigation(LoginMenuNavigation.Login);*/
	}
	
	
	
	public void ChangeScene(SceneTypes type) {
		switch (type) {
			case Login: stage.setScene(loginMenuScene); break;
			case Main: stage.setScene(mainMenuScene); break;
			case CreateQuiz: stage.setScene(quizCreationMenu); break;
		}
	}
	
	
	/* SCENE BUILDERS */
	/*
	public StackPane PrepareQuizCreationMenu() {
		StackPane layout = new StackPane();
		VBox vb_menu = new VBox(55);
		VBox vb_mcqQuestionMenu = new VBox(5);
		VBox vb_openQuestionMenu = new VBox(5);
		VBox vb_associativeQuestionMenu = new VBox(5);
		VBox vb_typeAssociative = new VBox(5);
		VBox vb_typeOpen = new VBox(5);
		VBox vb_typeMCQ = new VBox(5);
		VBox vb_mcqChoices = new VBox(5);
		VBox vb_associativeLeftColumn = new VBox(5);
		VBox vb_associativeRightColumn = new VBox(5);
		VBox vb_questionCommonPart = new VBox(5);
		
		HBox hb_associativeChoices = new HBox(5);
		HBox hb_typeSelection = new HBox(5);
		HBox hb_questionText = new HBox(5);
		HBox hb_questionTopics = new HBox(5);
		HBox hb_buttonRow = new HBox(5);
		HBox hb_openQuestionHint = new HBox(5);
		HBox hb_mcqFirstChoice = new HBox(5);
		HBox hb_mcqSecondChoice = new HBox(5);
		HBox hb_mcqThirdChoice = new HBox(5);
		HBox hb_mcqCorrectChoice = new HBox(5);
		
		
		Button bt_saveQuestion = CreateButton("Next Question", 150, 20, 10, true, "");
		Button bt_saveQuiz = CreateButton("Save Quiz", 150, 20, 10, true, "");
		Button bt_back = CreateButton("Back", 150, 20, 10, true, "");
		
		Text txt_associativeLeftInfo = new Text("Questions/Left part");
		TextField tf_associativeLeft1 = new TextField();
		TextField tf_associativeLeft2 = new TextField();
		TextField tf_associativeLeft3 = new TextField();
		TextField tf_associativeLeft4 = new TextField();
		TextField tf_associativeLeft5 = new TextField();
		
		Text txt_associativeRightInfo = new Text("Answers/Right part");
		TextField tf_associativeRight1 = new TextField();
		TextField tf_associativeRight2 = new TextField();
		TextField tf_associativeRight3 = new TextField();
		TextField tf_associativeRight4 = new TextField();
		TextField tf_associativeRight5 = new TextField();
		
		
		
		RadioButton rb_associativeQuestion = new RadioButton();
		RadioButton rb_openQuestion = new RadioButton();
		RadioButton rb_mcqQuestion = new RadioButton();
		Text txt_questionTextInfo = new Text("Question");
		TextField tf_questionText = new TextField();
		Text txt_questionTopicsInfo = new Text("Topics");
		TextField tf_questionTopics = new TextField();
		
		
		// Open Question
		Text txt_openQuestionHint = new Text("Hint");
		TextField tf_openQuestionHint = new TextField();
		
		// MCQ Question
		TextField tf_mcqFirstChoice = new TextField();
		TextField tf_mcqSecondChoice = new TextField();
		TextField tf_mcqThirdChoice = new TextField();
		TextField tf_mcqCorrectChoice = new TextField();
		Text txt_mcqFirstChoice = new Text("First choice");
		Text txt_mcqSecondChoice = new Text("Second choice");
		Text txt_mcqThirdChoice = new Text("Third choice");
		Text txt_mcqCorrectChoice = new Text("Correct choice");
		
		hb_mcqFirstChoice.getChildren().addAll(txt_mcqFirstChoice, tf_mcqFirstChoice);
		hb_mcqSecondChoice.getChildren().addAll(txt_mcqSecondChoice, tf_mcqSecondChoice);
		hb_mcqThirdChoice.getChildren().addAll(txt_mcqThirdChoice, tf_mcqThirdChoice);
		hb_mcqCorrectChoice.getChildren().addAll(txt_mcqCorrectChoice, tf_mcqCorrectChoice);
		vb_mcqChoices.getChildren().addAll(hb_mcqFirstChoice, hb_mcqSecondChoice, hb_mcqThirdChoice, hb_mcqCorrectChoice);
		
		
		// Associative Question
		vb_associativeLeftColumn.getChildren().addAll(txt_associativeLeftInfo, tf_associativeLeft1, tf_associativeLeft2, tf_associativeLeft3, tf_associativeLeft4, tf_associativeLeft5);
		vb_associativeRightColumn.getChildren().addAll(txt_associativeRightInfo, tf_associativeRight1, tf_associativeRight2, tf_associativeRight3, tf_associativeRight4, tf_associativeRight5);
		hb_associativeChoices.getChildren().addAll(vb_associativeLeftColumn, vb_associativeRightColumn);
		
		
		
		ImageView img_associativeQuestion = CreateImage("file:associative.png", 150, 150, false, "");
		img_associativeQuestion.onMouseClickedProperty();
		vb_typeAssociative.getChildren().addAll(rb_associativeQuestion, img_associativeQuestion);
		rb_associativeQuestion.setText("Associative");
		rb_associativeQuestion.setOnAction(e -> {
			vb_associativeQuestionMenu.setVisible(true);
			vb_openQuestionMenu.setVisible(false);
			vb_mcqQuestionMenu.setVisible(false);
			
			
			
			hb_questionText.setDisable(true);
			tf_questionText.setText("Match the following columns");
		});
		
		ImageView img_openQuestion = CreateImage("file:open.png", 150, 150, false, "");
		vb_typeOpen.getChildren().addAll(rb_openQuestion, img_openQuestion);
		rb_openQuestion.setText("Open");
		rb_openQuestion.setOnAction(e -> {
			vb_associativeQuestionMenu.setVisible(false);
			vb_openQuestionMenu.setVisible(true);
			vb_mcqQuestionMenu.setVisible(false);
			
		});
		
		ImageView img_mcqQuestion = CreateImage("file:mcq.png", 150, 150, false, "");
		vb_typeMCQ.getChildren().addAll(rb_mcqQuestion, img_mcqQuestion);
		rb_mcqQuestion.setText("Multiple Choice");
		rb_mcqQuestion.setOnAction(e -> {
			vb_associativeQuestionMenu.setVisible(false);
			vb_openQuestionMenu.setVisible(false);
			vb_mcqQuestionMenu.setVisible(true);
			
			
		});
		
		
		
		
		tf_questionText.setMinWidth(250);
		tf_openQuestionHint.minWidth(250);
		
		
		// Common parts
		hb_typeSelection.getChildren().addAll(vb_typeMCQ, vb_typeAssociative, vb_typeOpen);
		hb_questionText.getChildren().addAll(txt_questionTextInfo, tf_questionText);
		hb_questionTopics.getChildren().addAll(txt_questionTopicsInfo, tf_questionTopics);
		vb_questionCommonPart.getChildren().addAll(hb_questionText, hb_questionTopics);
		hb_buttonRow.getChildren().addAll(bt_back, bt_saveQuestion, bt_saveQuiz);
		
		
		// Question specific parts
		hb_openQuestionHint.getChildren().addAll(txt_openQuestionHint, tf_openQuestionHint);
		
		
		
		vb_associativeQuestionMenu.getChildren().addAll(hb_associativeChoices);
		vb_openQuestionMenu.getChildren().addAll(hb_openQuestionHint);
		vb_mcqQuestionMenu.getChildren().addAll(vb_mcqChoices);
		
		vb_menu.getChildren().addAll(hb_typeSelection, vb_questionCommonPart, vb_associativeQuestionMenu, hb_buttonRow);
		layout.getChildren().addAll(vb_menu);
		return layout;
	}
	
	public StackPane PrepareMainMenuLayout() {
		StackPane layout = new StackPane();
		HBox profileRow = new HBox(5);
		VBox buttonRow = new VBox(5), menu = new VBox(5);
		
		
		
		txt_username = new Text();
		txt_username.setText(user.getUsername() + " | " + (user.isAuthoritative() ? "Advanced User" : "Normal User"));
		txt_username.setFont(Font.font(20));
		txt_username.setTextAlignment(TextAlignment.CENTER);
		txt_username.fillProperty().setValue(textColor);
		
		
		bt_createQuiz = CreateButton("Create Quiz", 300, 30, 20, true,"");
		bt_createQuiz.setOnAction(e -> ChangeScene(SceneTypes.CreateQuiz));
		
		bt_playQuiz = CreateButton("Play Quiz", 300, 30, 20, true,"");
		
		bt_createSession = CreateButton("Create Session", 300, 30, 20, true,"");
		
		bt_joinSession = CreateButton("Join Session", 300, 30, 20, true,"");
		
		bt_promoteUsers = CreateButton("Promote User", 300, 30, 20, true,"");
		
		bt_history = CreateButton("My History", 300, 30, 20, true, "");
		
		bt_logOut = CreateButton("Log Out", 300, 30, 20, true,"");
		bt_logOut.setOnAction(e -> LogOutButtonAction());
		
		bt_quit = CreateButton("Quit", 300, 30, 20, true,"");
		bt_quit.setOnAction(e -> QuitButtonAction());
		
		
		if (user.isAuthoritative()) {
			buttonRow.getChildren().addAll(bt_createQuiz, bt_createSession, bt_history, bt_promoteUsers,bt_logOut,  bt_quit);
		}
		
		else {
			buttonRow.getChildren().addAll(bt_playQuiz, bt_joinSession, bt_history, bt_logOut, bt_quit);
		}
		
		
		buttonRow.alignmentProperty().setValue(Pos.CENTER);
		menu.getChildren().addAll(txt_username, buttonRow);
		menu.alignmentProperty().setValue(Pos.TOP_CENTER);
		menu.setStyle(menuBackgroundColorCSS);
		layout.getChildren().addAll(menu);
		
		
		
		return layout;
	}
	
	public StackPane PrepareLoginMenuLayout() {
		StackPane layout = new StackPane();
		ImageView imgView = new ImageView();
		VBox navigation = new VBox(5), menu = new VBox(50);
		HBox navigationButtonRow = new HBox(5), inputButtonRow = new HBox(5);
		
		
		
		tf_username = new TextField(); pf_password = new PasswordField();
		txt_menuName = new Text(); txt_infoMessage = new Text();
		
		
		img_gameLogo = new Image("file:Quizman.png", 1000, 0, false, false);
		imgView.setImage(img_gameLogo);
		
		txt_menuName.setFont(Font.font(55));
		txt_menuName.setFill(textColor);
		
		tf_username.setPromptText("Enter your username");
		tf_username.setMaxSize(250, 25);
		tf_username.setMinHeight(35);
		tf_username.setStyle(inputFieldColorCSS);
		pf_password.setPromptText("Enter your password");
		pf_password.setMinHeight(35);
		pf_password.setMaxSize(250, 25);
		pf_password.setStyle(inputFieldColorCSS);
		
		
		bt_clear = CreateButton("Clear", 122.5, 30, 15, true,"");
		bt_clear.setOnAction(event -> ClearButtonAction());
		
		bt_submit = CreateButton("Submit", 122.5, 30, 15, true,inactiveTabColorCSS);
		bt_submit.setOnAction(event -> SubmitButtonAction());
		bt_submit.setDefaultButton(true);
		
		bt_nav_login = CreateButton("Login", 80, 30, 15, false,"");
		bt_nav_login.setOnAction(event -> ChangeLoginMenuNavigation(LoginMenuNavigation.Login));
		
		bt_nav_register = CreateButton("Register", 80, 30, 15, false, "");
		bt_nav_register.setOnAction(event -> ChangeLoginMenuNavigation(LoginMenuNavigation.Register));
		
		bt_nav_guest = CreateButton("Guest", 80, 30, 15, false, "");
		bt_nav_guest.setOnAction(event -> ChangeLoginMenuNavigation(LoginMenuNavigation.Guest));
		
		bt_quit = CreateButton("Quit", 250, 30, 15, true, "");
		bt_quit.setOnAction(e -> QuitButtonAction());
		
		navigationButtonRow.getChildren().addAll(bt_nav_login, bt_nav_register, bt_nav_guest);
		navigationButtonRow.alignmentProperty().setValue(Pos.CENTER);
		inputButtonRow.getChildren().addAll(bt_clear, bt_submit);
		inputButtonRow.alignmentProperty().setValue(Pos.CENTER);
		navigation.getChildren().addAll(txt_menuName, tf_username, pf_password, inputButtonRow, navigationButtonRow, bt_quit);
		navigation.alignmentProperty().setValue(Pos.CENTER);
		menu.getChildren().addAll(imgView, txt_menuName, navigation, txt_infoMessage);
		menu.alignmentProperty().setValue(Pos.CENTER);
		menu.setStyle(menuBackgroundColorCSS);
		layout.getChildren().addAll(menu);
		
		return layout;
	}
	*/
	
	
	
	/* HELPER FUNCTIONS */
	public void ChangeLoginMenuNavigation(LoginMenuNavigation navigation) {
		currentNavigation = navigation;
		ResetLoginMenuNavigationButtons();
		txt_infoMessage.setText("");
		switch (navigation) {
			case Login: bt_nav_login.setStyle(activeTabColorCSS); txt_menuName.setText("Login"); break;
			case Register: bt_nav_register.setStyle(activeTabColorCSS); txt_menuName.setText("Register"); break;
			case Guest:
				bt_nav_guest.setStyle(activeTabColorCSS);
				txt_menuName.setText("Guest Login");
				pf_password.setVisible(false);
				tf_username.setVisible(false);
				txt_infoMessage.setText("Guest login option is under maintenance");
				txt_infoMessage.setFill(Color.rgb(255,0,0));
				break;
		}
	}
	
	public void ResetLoginMenuNavigationButtons() {
		pf_password.setVisible(true);
		tf_username.setVisible(true);
		
		bt_nav_login.setStyle(inactiveTabColorCSS);
		bt_nav_register.setStyle(inactiveTabColorCSS);
		bt_nav_guest.setStyle(inactiveTabColorCSS);
	}
	
	
	
	
	/* BUTTON ACTIONS
	public void QuitButtonAction() {
		Logger.Log("Program terminated safely by user", Logger.LogType.INFO);
		System.exit(0);
	}
	
	public void LogOutButtonAction() {
		user = null;
		ClearButtonAction();
		ChangeScene(SceneTypes.Login);
	}
	
	public void SubmitButtonAction() {
		String username = tf_username.getText();
		String password = pf_password.getText();
		
		
		if (currentNavigation == LoginMenuNavigation.Register) {
			if (DatabaseManager.instance.UserExists(username)) {
				txt_infoMessage.setText("This username already exists");
				txt_infoMessage.setFill(Color.rgb(255,0,0));
			}
			
			else if (username.equals("") || password.equals("")) {
				txt_infoMessage.setText("Please fill both username and password field");
				txt_infoMessage.setFill(Color.rgb(255,0,0));
			}
			
			else {
				DatabaseManager.instance.UserRegister(username, password);
				txt_infoMessage.setText("New user has successfully created");
				txt_infoMessage.setFill(Color.rgb(0,255,0));
			}
		}
		
		else if (currentNavigation == LoginMenuNavigation.Login) {
			txt_infoMessage.setText("Logging in...");
			txt_infoMessage.setFill(Color.rgb(0,255,0));
			user = DatabaseManager.instance.UserLogin(username, password);
			
			
			if (user == null) {
				txt_infoMessage.setText("Incorrect username or password");
				txt_infoMessage.setFill(Color.rgb(255,0,0));
			}
			
			else {
				mainMenuScene = new Scene(PrepareMainMenuLayout(), width, height);
				ChangeScene(SceneTypes.Main);
			}
		}
	}
	
	public void ClearButtonAction() {
		tf_username.setText("");
		pf_password.setText("");
		txt_infoMessage.setText("");
	}
	
	public void QuizTypeRButtonAction(QuestionTypes newType) {
		createQuestionType = newType;
	}
	*/
	
	
	
	public ImageView CreateImage(String imagePath, int width, int height, boolean keepRatio, String style) {
		ImageView img = new ImageView();
		
		
		img.setImage(new Image(imagePath, width, height, keepRatio, false));
		img.setStyle(style);
		
		return img;
	}
	
	public Button CreateButton(String buttonText, double width, double height, int font, boolean hoverEffect, String style) {
		Button newButton = new Button();
		
		
		newButton.setText(buttonText);
		newButton.setPrefWidth(width);
		newButton.setPrefHeight(height);
		newButton.setFont(Font.font(font));
		newButton.setTextFill(buttonTextColor);
		newButton.setStyle("".equals(style) || style == null ? inactiveTabColorCSS : style);
		
		if (hoverEffect) {
			newButton.setOnMouseEntered(event -> newButton.setStyle(buttonHoverColorCSS));
			newButton.setOnMouseExited(event -> newButton.setStyle(inactiveTabColorCSS));
		}
		
		return newButton;
	}
	
	
	
	/**
	 * Reads the config file and creates an array of config elements
	 * @param args  Arguments given to program
	 * @return      List of config file elements
	 */
	public static List<String> ReadConfigFile(String[] args) {
		List <String> elements = new ArrayList<>();
		
		
		try(Scanner scanner = new Scanner(new File(args[0]))) {
			while (scanner.hasNextLine()) {
				elements.add(scanner.nextLine());
			}
			
			if (elements.size() != REQUIRED_CONFIG_ELEMENT) {
				throw new IndexOutOfBoundsException("There isn't enough configuration options for given file: " + args[0]);
			}
		} catch (Exception e) {
			System.out.println("Fatal Error: " + e.getMessage());
			System.exit(1);
		}
		
		return elements;
	}
	
	
	
	/**
	 * Checks if there is a valid filename given as a program argument
	 * @param args  Argument list given to program
	 * @return      Indicator flag for program to continue or not
	 */
	public static boolean CheckArguments(String[] args) {
		boolean result;
		
		
		if (result = (args.length == REQUIRED_ARGUMENT_COUNT)) {
			try (Scanner scanner = new Scanner(new File(args[0]))) {
				int lineCount = 0;
				while (scanner.hasNextLine()) {
					scanner.nextLine();
					++lineCount;
				}
				
				result = (lineCount == REQUIRED_CONFIG_ELEMENT);
			} catch (FileNotFoundException e) {
				System.out.println("Error: " + e.getMessage());
				result = false;
			}
		}
		
		return result;
	}
}
