package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.Searchable;
import com.layso.quizmanager.services.CfgManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;



public class Controller {
	public enum WindowStage {LoginMenu, MainMenu, CreateQuizMenu, UserPromoteMenu, EditDeleteQuizMenu, SolveQuizMenu, SeeResultsMenu, CheckAnswersMenu}
	
	
	
	/**
	 * A method to change the window with a predefined one
	 * @param event ActionEvent produced from GUI
	 * @param stage Enumerated type that indicates the target window
	 */
	public void ChangeScene(ActionEvent event, WindowStage stage) {
		Stage currentWindow = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		double width = Integer.parseInt(CfgManager.getInstance().Get("gui.windowWidth"));
		double height = Integer.parseInt(CfgManager.getInstance().Get("gui.windowHeight"));
		Parent parent = null;
		
		
		try {
			// Load target GUI elements
			switch (stage) {
				case LoginMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.login.fxml"))); break;
				case MainMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.main.fxml"))); break;
				case CreateQuizMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.createQuiz.fxml"))); break;
				case UserPromoteMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.userPromotion.fxml"))); break;
				case EditDeleteQuizMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.editDeleteQuiz.fxml"))); break;
				case SolveQuizMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.solveQuiz.fxml"))); break;
				case SeeResultsMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.seeResults.fxml"))); break;
				case CheckAnswersMenu: parent = FXMLLoader.load(getClass().getResource(CfgManager.getInstance().Get("gui.checkAnswers.fxml"))); break;
			}
			
			// Set new scene
			Scene mainMenuScene = new Scene(parent, width, height);
			currentWindow.setScene(mainMenuScene);
			Logger.Log("Scene changed successfully to: " + stage.name(), Logger.LogType.INFO);
		} catch (NullPointerException e) {
			Logger.Log("Fatal Error: FXML filename fetch for failed: " + e.getMessage() + ": Terminating program", Logger.LogType.ERROR);
			System.exit(-1);
		} catch (IOException e) {
			Logger.Log("Fatal Error: GUI replacement failed: " + e.getMessage() + ": Terminating program", Logger.LogType.ERROR);
			System.exit(-1);
		}
	}
	
	
	
	/**
	 * Helper method for scene controllers to associate table columns with class variables
	 * @param factoryList   List of PropertyValueFactory taken by class to associate
	 * @param columns       List of columns to associate each property value factory with
	 */
	public void AssociateTableWithClass(List<PropertyValueFactory> factoryList, TableColumn ... columns) {
		for (int i=0; i<columns.length; ++i) {
			columns[i].setCellValueFactory(factoryList.get(i));
		}
	}
	
	
	
	/**
	 * Helper method to associate search term choice box with table columns
	 * @param choiceBoxes   SearchTerm choice box
	 * @param table         Table of contents
	 */
	public void AssociateSearchCriteriaWithTable(ChoiceBox choiceBoxes, TableView table) {
		ObservableList<String> availableChoices = FXCollections.observableArrayList();
		for (int i=0; i<table.getColumns().size(); ++i) {
			availableChoices.add(((TableColumn) table.getColumns().get(i)).getText());
		}
		
		choiceBoxes.setItems(availableChoices);
		choiceBoxes.getSelectionModel().selectFirst();
	}
	
	
	
	/**
	 * Helper function to clear user inputs
	 * @param fields    Array of any field that can hold user input
	 */
	public void ClearTextFields(TextField ... fields) {
		for (TextField field : fields) {
			field.setText("");
		}
	}
	
	
	
	/**
	 * Opens a file chooser to let user select an image (only with extensions .png .jpg or .gif) to support question
	 * @return Returns the full path of the selected file
	 */
	public String FileSelector() {
		String path = "";
		
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose image to support the question");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
		File file = fileChooser.showOpenDialog(null);
		
		if (file != null) {
			path = file.getAbsolutePath();
		}
		
		return path;
	}
	
	
	
	/**
	 * Changes the tab according to the clicked button ID
	 * @param event ActionEvent created by GUI
	 * @param tabs  TabPane object to select new tab from
	 */
	public void ChangeNavigation(ActionEvent event, TabPane tabs) {
		for (Tab tab : tabs.getTabs()) {
			if (tab.getId().equals(((Node) event.getSource()).getId())) {
				tabs.getSelectionModel().select(tab);
			}
		}
	}
	
	
	
	/**
	 * Generic search method to prevent duplications on list searches
	 * @param list              List of objects to be check if they provide the criteria
	 * @param searchCriteria    Criteria of search, the thing user is looking for
	 * @param searchTerm        Term of search, in which part of the object user is looking for the criteria
	 * @param <T>               An object which implements the Searchable interface
	 * @return                  Observable list of items which provide the criteria
	 */
	public static <T extends Searchable> ObservableList<T> SearchHelper(List<T> list, String searchCriteria, String searchTerm) {
		ObservableList<T> data = FXCollections.observableArrayList();
		
		
		for (T item : list) {
			if (searchCriteria.equals("") || item.Search(searchCriteria, searchTerm)) {
				data.add(item);
			}
		}
		
		return data;
	}
	
	
	/**
	 * Console application helper method to print given menu items
	 * @param menuItems List of string that holds menu options
	 */
	public static void PrintMenu(String ... menuItems) {
		for (int i=0; i< menuItems.length; ++i) {
			System.out.println("[" + (i+1) + "] " + menuItems[i]);
		}
	}
	
	
	
	/**
	 * Console application helper to get an integer value from user
	 * @return  Integer value given by user
	 */
	public static int GetMenuInput() {
		int menuInput;
		
		
		// Ask for input until a valid int has given
		do {
			try {
				menuInput = Integer.parseInt(GetInput("Select", true));
			} catch (NumberFormatException e) {
				menuInput = -1;
			}
		} while (menuInput == -1);
		
		
		return menuInput;
	}
	
	
	
	/**
	 * Console application helper method to get an input with a prompted message
	 * @param prompt    Message to prompt
	 * @param newLine   Option to print new line or not
	 * @return          Input of user
	 */
	public static String GetInput(String prompt, boolean newLine) {
		String input;
		
		
		System.out.print(prompt + ": ");
		input = new Scanner(System.in).nextLine();
		if (newLine)
			System.out.println();
		
		return input;
	}
	
	
	
	/**
	 * Console helper method to print any list as table using their toString method
	 * @param list  List of elements
	 * @param <T>   Type of elements
	 */
	public static <T> void PrintArrayAsTable (List<T> list) {
		for (int i=0; i<list.size(); ++i) {
			System.out.println("[" + (i+1) + "] " + list.get(i).toString());
		}
	}
}
