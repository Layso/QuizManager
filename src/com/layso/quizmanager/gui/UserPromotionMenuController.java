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
	@FXML
	private TextField searchCriteriaText;
	
	@FXML
	private ChoiceBox searchCriteriaChoice;
	
	@FXML
	private TableView table;
	
	@FXML
	private TableColumn usernameColumn, authorityColumn;
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		AssociateSearchCriteriaWithTable(searchCriteriaChoice, table);
		AssociateTableWithClass(User.GetPropertyValueFactory(), usernameColumn, authorityColumn);
		SearchButton(null);
		Logger.Log("Quiz Edit/Delete Menu initialized", Logger.LogType.INFO);
	}
	
	public static void UserPromotionMenu() {
		List<User> users = DatabaseManager.getInstance().GetAllUsers();
		boolean correctInput;
		boolean run = true;
		int selection;
		
		
		while (run) {
			do {
				PrintArrayAsTable(users);
				System.out.println();
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
	
	public static void ChangeUserAuthority(User user) {
		boolean authorititive;
		int selection;
		
		
		PrintMenu("Give Authority", "Take Authority");
		do {selection = GetMenuInput();} while(selection < 1 || selection > 2);
		authorititive = selection == 1;
		DatabaseManager.getInstance().UpdateUserAuthority(user.GetID(), authorititive);
	}
	
	public static List<User> Search() {
		String searchCriteria = GetInput("Search criteria", true);
		User.UserSearchTerms termEnum;
		int searchTerm;
		
		do {
			System.out.println("[1] Username\n[2] Authority");
			searchTerm = GetMenuInput();
		} while (searchTerm < 1 || searchTerm > 2);
		
		termEnum = searchTerm == 1 ? User.UserSearchTerms.Username : User.UserSearchTerms.Authority;
		return new ArrayList(Controller.SearchHelper(DatabaseManager.getInstance().GetAllUsers(), searchCriteria, termEnum.name()));
	}
	
	public void SearchButton(ActionEvent event) {
		List<User> users = DatabaseManager.getInstance().GetAllUsers();
		table.setItems(SearchHelper(users, searchCriteriaText.getText(), searchCriteriaChoice.getSelectionModel().getSelectedItem().toString()));
	}
	
	public void PromoteButton(ActionEvent event) {
		if (table.getSelectionModel().getSelectedItem() != null) {
			User user = ((User) table.getSelectionModel().getSelectedItem());
			DatabaseManager.getInstance().UpdateUserAuthority(user.GetID(), true);
			SearchButton(event);
		}
	}
	
	public void DemoteButton(ActionEvent event) {
		if (table.getSelectionModel().getSelectedItem() != null) {
			User user = ((User) table.getSelectionModel().getSelectedItem());
			DatabaseManager.getInstance().UpdateUserAuthority(user.GetID(), false);
			SearchButton(event);
		}
	}
	
	public void BackButton(ActionEvent event) {
		ChangeScene(event, WindowStage.MainMenu);
	}
}
