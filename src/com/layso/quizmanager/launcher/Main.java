package com.layso.quizmanager.launcher;

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
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class Main extends Application {
	static final int REQUIRED_ARGUMENT_COUNT = 1;
	static final int REQUIRED_CONFIG_ELEMENT = 4;
	static final int CFG_DB_URL_INDEX = 0;
	static final int CFG_DB_USR_INDEX = 1;
	static final int CFG_DB_PASS_INDEX = 2;
	static final int CFG_LOG_FILENAME_INDEX = 3;
	
	
	
	public enum MainMenuNavigation {Login, Register, Guest;}
	
	DatabaseManager dbManager;
	
	MainMenuNavigation currentNavigation;
	Button bt_clear, bt_submit, bt_nav_login, bt_nav_register, bt_nav_guest;
	TextField tf_username;
	PasswordField pf_password;
	Text txt_menuName, txt_infoMessage;
	Image img_gameLogo;
	
	
	
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
			dbManager.Connect(configElements.get(CFG_DB_URL_INDEX), configElements.get(CFG_DB_USR_INDEX), configElements.get(CFG_DB_PASS_INDEX));
			launch();
		}
		
		
		
		
		/*
		
			Logger.Setup(configElements.get(CFG_LOG_FILENAME_INDEX), true, true);
			dbManager = new DatabaseManager();
			dbManager.Connect(configElements.get(CFG_DB_URL_INDEX), configElements.get(CFG_DB_USR_INDEX), configElements.get(CFG_DB_PASS_INDEX));
			QuizManager quizManager = new QuizManager();
			
			Logger.Log("Object initializations are done", Logger.LogType.INFO);
			quizManager.Run();
			Logger.Log("Program shutting down", Logger.LogType.INFO);
		}*/
	}
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane mainLayout = PrepareMainMenuLayout();
		Scene mainScene = new Scene(mainLayout, 1024, 768);
		
		primaryStage.setTitle("Quizmania - Learn, practice, achieve");
		primaryStage.setScene(mainScene);
		primaryStage.show();
		
		
		ChangeMenuNavigation(MainMenuNavigation.Login);
		
		/*
		button_login = new Button();
		button_login.setText("Login");
		button_login.setOnAction(event -> {
			System.out.println("Logine basıldı heyo");
		});
		
		primaryStage.setTitle("Quizmania");
		
		StackPane layout = new StackPane();
		layout.getChildren().add(button_login);
		
		Scene scene = new Scene(layout, 1024, 768);
		primaryStage.setScene(scene);
		primaryStage.show();*/
		
	}
	
	
	public StackPane PrepareMainMenuLayout() {
		Color buttonTextColor = Color.rgb(255,255,255);
		String inactiveButtonColor = "-fx-background-color: rgb(75,75,75)";
		
		
		ImageView imgView = new ImageView();
		StackPane layout = new StackPane();
		HBox navigationButtonRow = new HBox(5);
		HBox inputButtonRow = new HBox(5);
		VBox navigation = new VBox(5);
		VBox menu = new VBox(50);
		
		tf_username = new TextField(); pf_password = new PasswordField();
		bt_clear = new Button(); bt_submit = new Button();
		bt_nav_login = new Button(); bt_nav_register = new Button(); bt_nav_guest = new Button();
		txt_menuName = new Text(); txt_infoMessage = new Text();
		
		
		img_gameLogo = new Image("file:Quizman.png", 1000, 0, false, false);
		imgView.setImage(img_gameLogo);
		
		txt_menuName.setFont(Font.font(55));
		
		tf_username.setPromptText("Enter your username");
		tf_username.setMaxSize(250, 20);
		pf_password.setPromptText("Enter your password");
		pf_password.setMaxSize(250, 20);
		
		bt_clear.setText("Clear");
		bt_clear.setMinSize(122.5, 25);
		bt_clear.setStyle(inactiveButtonColor);
		bt_clear.setTextFill(buttonTextColor);
		bt_clear.setOnAction(event -> ClearButtonAction());
		bt_submit.setText("Submit");
		bt_submit.setMinSize(122.5, 25);
		bt_submit.setStyle(inactiveButtonColor);
		bt_submit.setTextFill(buttonTextColor);
		bt_submit.setOnAction(event -> SubmitButtonAction());
		
		bt_nav_login.setText("Login");
		bt_nav_login.setMinSize(80, 25);
		bt_nav_login.setTextFill(buttonTextColor);
		bt_nav_login.setOnAction(event -> ChangeMenuNavigation(MainMenuNavigation.Login));
		bt_nav_register.setText("Register");
		bt_nav_register.setMinSize(80, 25);
		bt_nav_register.setOnAction(event -> ChangeMenuNavigation(MainMenuNavigation.Register));
		bt_nav_register.setTextFill(buttonTextColor);
		bt_nav_guest.setText("Guest");
		bt_nav_guest.setMinSize(80, 25);
		bt_nav_guest.setOnAction(event -> ChangeMenuNavigation(MainMenuNavigation.Guest));
		bt_nav_guest.setTextFill(buttonTextColor);
		
		navigationButtonRow.getChildren().addAll(bt_nav_login, bt_nav_register, bt_nav_guest);
		navigationButtonRow.alignmentProperty().setValue(Pos.CENTER);
		inputButtonRow.getChildren().addAll(bt_clear, bt_submit);
		inputButtonRow.alignmentProperty().setValue(Pos.CENTER);
		navigation.getChildren().addAll(txt_menuName, tf_username, pf_password, inputButtonRow, navigationButtonRow);
		navigation.alignmentProperty().setValue(Pos.CENTER);
		menu.getChildren().addAll(imgView, txt_menuName, navigation, txt_infoMessage);
		menu.alignmentProperty().setValue(Pos.CENTER);
		layout.getChildren().addAll(menu);
		
		return layout;
	}
	
	
	public void ChangeMenuNavigation(MainMenuNavigation navigation) {
		String activeButtonColor = "-fx-background-color: rgb(125,125,125)";
		
		
		currentNavigation = navigation;
		ResetMainMenuNavigationButtons();
		txt_infoMessage.setText("");
		switch (navigation) {
			case Login: bt_nav_login.setStyle(activeButtonColor); txt_menuName.setText("Login"); break;
			case Guest: bt_nav_guest.setStyle(activeButtonColor); txt_menuName.setText("Guest Login"); break;
			case Register: bt_nav_register.setStyle(activeButtonColor); txt_menuName.setText("Register"); break;
		}
	}
	
	
	
	public void ResetMainMenuNavigationButtons () {
		String inactiveButtonColor = "-fx-background-color: rgb(75,75,75)";
		
		
		bt_nav_login.setStyle(inactiveButtonColor);
		bt_nav_register.setStyle(inactiveButtonColor);
		bt_nav_guest.setStyle(inactiveButtonColor);
	}
	
	
	
	public void SubmitButtonAction() {
		String username = tf_username.getText();
		String password = pf_password.getText();
		
		
		if (currentNavigation == MainMenuNavigation.Register) {
			if (DatabaseManager.instance.UserExists(username)) {
				txt_infoMessage.setText("This username already exists");
				txt_infoMessage.setFill(Color.rgb(255,0,0));
			}
			
			else {
				DatabaseManager.instance.UserRegister(username, password);
				txt_infoMessage.setText("New user has successfully created");
				txt_infoMessage.setFill(Color.rgb(0,255,0));
			}
		}
		
		else if (currentNavigation == MainMenuNavigation.Login) {
			
			if (DatabaseManager.instance.UserLogin(username, password) == null) {
				txt_infoMessage.setText("Incorrect username or password");
				txt_infoMessage.setFill(Color.rgb(255,0,0));
				
			}
			
			else {
				System.out.println("ehe");
				txt_infoMessage.setText("Logging in...");
				txt_infoMessage.setFill(Color.rgb(0,255,0));
			}
		}
	}
	
	public void ClearButtonAction() {
	 tf_username.setText("");
	 pf_password.setText("");
	 txt_infoMessage.setText("");
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
