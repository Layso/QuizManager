package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.User;
import com.layso.quizmanager.services.DatabaseManager;
import com.layso.quizmanager.services.QuizManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;



public class UserPromotionMenuController extends Controller implements Initializable {
	// GUI elements
	@FXML
	private TextField searchCriteriaText;
	
	@FXML
	private ChoiceBox searchCriteriaChoice;
	
	@FXML
	private TableView table;
	
	@FXML
	private TableColumn usernameColumn, authorityColumn;
	
	
	
	/**
	 * Overriding initialize method to setup stage
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Associating search functionality with GUI elements and table with class
		AssociateSearchCriteriaWithTable(searchCriteriaChoice, table);
		AssociateTableWithClass(User.GetPropertyValueFactory(), usernameColumn, authorityColumn);
		SearchButton(null);
		
		Logger.Log("Quiz Edit/Delete Menu initialized", Logger.LogType.INFO);
	}
	
	
	
	/**
	 * Method to start SolveQuizMenu on console
	 */
	public static void UserPromotionMenu() {
		List<User> users = DatabaseManager.getInstance().GetAllUsers();
		boolean correctInput;
		boolean run = true;
		int selection;
		
		
		// Run until user specifies otherwise
		while (run) {
			do {
				// Print Users as table
				System.out.println(User.ConsoleTableTitle());
				PrintArrayAsTable(users);
				System.out.println();
				
				// Print menu selections, get input and process
				PrintMenu("Select User", "Search", "Back");
				switch (GetMenuInput()) {
					case 1:
						if (users.size() > 0) {
							do {selection = GetMenuInput();} while(selection < 1 || selection > users.size());
							ChangeUserAuthority(users.get(selection-1));
						} else {
							System.out.println("No user to select");
						}
						correctInput = true;
						break;
					case 2: correctInput = true; users = Search(); break;
					case 3: correctInput = true; run = false; QuizManager.getInstance().SetCurrentStage(WindowStage.MainMenu); break;
					default: correctInput = false;
				}
			} while (!correctInput);
		}
	}
	
	
	
	/**
	 * Console helper method to change the authority status of selected User
	 * @param user  User to change authority
	 */
	public static void ChangeUserAuthority(User user) {
		boolean authorititive;
		int selection;
		
		
		PrintMenu("Give Authority", "Take Authority");
		do {selection = GetMenuInput();} while(selection < 1 || selection > 2);
		authorititive = selection == 1;
		DatabaseManager.getInstance().UpdateUserAuthority(user.GetID(), authorititive);
	}
	
	
	
	/**
	 * Console helper method to search a User
	 * @return  Filtered list of users
	 */
	public static List<User> Search() {
		// Get search criteria
		String searchCriteria = GetInput("Search criteria", true);
		User.UserSearchTerms termEnum;
		int searchTerm;
		
		// Get search term
		do {
			System.out.println("[1] Username\n[2] Authority");
			searchTerm = GetMenuInput();
		} while (searchTerm < 1 || searchTerm > 2);
		
		// Get filtered list and return
		termEnum = searchTerm == 1 ? User.UserSearchTerms.Username : User.UserSearchTerms.Authority;
		return new ArrayList(Controller.SearchHelper(DatabaseManager.getInstance().GetAllUsers(), searchCriteria, termEnum.name()));
	}
	
	
	
	/**
	 * GUI search button action
	 * @param event ActionEvent created by GUI
	 */
	public void SearchButton(ActionEvent event) {
		List<User> users = DatabaseManager.getInstance().GetAllUsers();
		table.setItems(SearchHelper(users, searchCriteriaText.getText(), searchCriteriaChoice.getSelectionModel().getSelectedItem().toString()));
	}
	
	
	
	/**
	 * GUI promote button action
	 * @param event ActionEvent created by GUI
	 */
	public void PromoteButton(ActionEvent event) {
		if (table.getSelectionModel().getSelectedItem() != null) {
			User user = ((User) table.getSelectionModel().getSelectedItem());
			DatabaseManager.getInstance().UpdateUserAuthority(user.GetID(), true);
			SearchButton(event);
		}
	}
	
	
	
	/**
	 * GUI demote button action
	 * @param event ActionEvent created by GUI
	 */
	public void DemoteButton(ActionEvent event) {
		if (table.getSelectionModel().getSelectedItem() != null) {
			User user = ((User) table.getSelectionModel().getSelectedItem());
			DatabaseManager.getInstance().UpdateUserAuthority(user.GetID(), false);
			SearchButton(event);
		}
	}
	
	
	
	/**
	 * GUI back to main menu button
	 * @param event ActionEvent created by GUI
	 */
	public void BackButton(ActionEvent event) {
		ChangeScene(event, WindowStage.MainMenu);
	}
}
