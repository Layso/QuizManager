package com.layso.quizmanager.launcher;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.User;
import com.layso.quizmanager.services.DatabaseManager;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
	
	
	public enum SceneTypes {Welcoming, Main}
	public enum WelcomingMenuNavigation {Login, Register, Guest}
	
	
	
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
	
	private Scene welcomingMenuScene;
	private WelcomingMenuNavigation currentNavigation;
	private Button bt_clear, bt_submit, bt_nav_login, bt_nav_register, bt_nav_guest;
	private TextField tf_username;
	private PasswordField pf_password;
	private Text txt_menuName, txt_infoMessage;
	private Image img_gameLogo;
	
	
	private Scene mainMenuScene;
	private Button bt_playQuiz, bt_createQuiz, bt_quit, bt_promoteUsers, bt_createSession, bt_joinSession, bt_logOut;
	private Text txt_username;
	
	
	
	public static void main(String[] args) {
		List<String> configElements;
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
		}
	}
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		width = 1024;
		height = 768;
		stage = primaryStage;
		welcomingMenuScene = new Scene(PrepareWelcomingMenuLayout(), width, height);
		
		
		primaryStage.setTitle("Quizmania - Learn, practice, achieve");
		primaryStage.setScene(welcomingMenuScene);
		primaryStage.show();
		
		
		ChangeWelcomingMenuNavigation(WelcomingMenuNavigation.Login);
	}
	
	
	
	public void ChangeScene(SceneTypes type) {
		switch (type) {
			case Welcoming: stage.setScene(welcomingMenuScene); break;
			case Main: stage.setScene(new Scene(PrepareMainMenuLayout(), width, height)); break;
		}
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
		
		bt_playQuiz = CreateButton("Play Quiz", 300, 30, 20, true,"");
		
		bt_createSession = CreateButton("Create Session", 300, 30, 20, true,"");
		
		bt_joinSession = CreateButton("Join Session", 300, 30, 20, true,"");
		
		bt_promoteUsers = CreateButton("Promote User", 300, 30, 20, true,"");
		
		bt_logOut = CreateButton("Log Out", 300, 30, 20, true,"");
		
		bt_quit = CreateButton("Quit", 300, 30, 20, true,"");
		
		
		if (user.isAuthoritative()) {
			buttonRow.getChildren().addAll(bt_playQuiz, bt_createQuiz, bt_createSession, bt_promoteUsers,bt_logOut,  bt_quit);
		}
		
		else {
			buttonRow.getChildren().addAll(bt_playQuiz, bt_createQuiz, bt_joinSession, bt_logOut, bt_quit);
		}
		
		
		buttonRow.alignmentProperty().setValue(Pos.CENTER);
		menu.getChildren().addAll(txt_username, buttonRow);
		menu.alignmentProperty().setValue(Pos.TOP_CENTER);
		menu.setStyle(menuBackgroundColorCSS);
		layout.getChildren().addAll(menu);
		
		
		
		return layout;
	}
	
	
	
	public StackPane PrepareWelcomingMenuLayout() {
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
		
		
		bt_clear = CreateButton("Clear", 122.5, 30, 15, true,inactiveTabColorCSS);
		bt_clear.setOnAction(event -> ClearButtonAction());
		
		bt_submit = CreateButton("Submit", 122.5, 30, 15, true,inactiveTabColorCSS);
		bt_submit.setOnAction(event -> SubmitButtonAction());
		bt_submit.setDefaultButton(true);
		
		bt_nav_login = CreateButton("Login", 80, 30, 15, false,"");
		bt_nav_login.setOnAction(event -> ChangeWelcomingMenuNavigation(WelcomingMenuNavigation.Login));
		
		bt_nav_register = CreateButton("Register", 80, 30, 15, false, "");
		bt_nav_register.setOnAction(event -> ChangeWelcomingMenuNavigation(WelcomingMenuNavigation.Register));
		
		bt_nav_guest = CreateButton("Guest", 80, 30, 15, false, "");
		bt_nav_guest.setOnAction(event -> ChangeWelcomingMenuNavigation(WelcomingMenuNavigation.Guest));
		
		
		navigationButtonRow.getChildren().addAll(bt_nav_login, bt_nav_register, bt_nav_guest);
		navigationButtonRow.alignmentProperty().setValue(Pos.CENTER);
		inputButtonRow.getChildren().addAll(bt_clear, bt_submit);
		inputButtonRow.alignmentProperty().setValue(Pos.CENTER);
		navigation.getChildren().addAll(txt_menuName, tf_username, pf_password, inputButtonRow, navigationButtonRow);
		navigation.alignmentProperty().setValue(Pos.CENTER);
		menu.getChildren().addAll(imgView, txt_menuName, navigation, txt_infoMessage);
		menu.alignmentProperty().setValue(Pos.CENTER);
		menu.setStyle(menuBackgroundColorCSS);
		layout.getChildren().addAll(menu);
		
		return layout;
	}
	
	
	
	public void ChangeWelcomingMenuNavigation(WelcomingMenuNavigation navigation) {
		currentNavigation = navigation;
		ResetWelcomingMenuNavigationButtons();
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
	
	
	
	public void ResetWelcomingMenuNavigationButtons() {
		pf_password.setVisible(true);
		tf_username.setVisible(true);
		
		bt_nav_login.setStyle(inactiveTabColorCSS);
		bt_nav_register.setStyle(inactiveTabColorCSS);
		bt_nav_guest.setStyle(inactiveTabColorCSS);
	}
	
	
	
	public void SubmitButtonAction() {
		String username = tf_username.getText();
		String password = pf_password.getText();
		
		
		if (currentNavigation == WelcomingMenuNavigation.Register) {
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
		
		else if (currentNavigation == WelcomingMenuNavigation.Login) {
			txt_infoMessage.setText("Logging in...");
			txt_infoMessage.setFill(Color.rgb(0,255,0));
			user = DatabaseManager.instance.UserLogin(username, password);
			
			
			if (user == null) {
				txt_infoMessage.setText("Incorrect username or password");
				txt_infoMessage.setFill(Color.rgb(255,0,0));
			}
			
			else {
				ChangeScene(SceneTypes.Main);
			}
		}
	}
	
	
	
	public void ClearButtonAction() {
		tf_username.setText("");
		pf_password.setText("");
		txt_infoMessage.setText("");
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
