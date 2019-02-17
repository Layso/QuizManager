package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.User;
import com.layso.quizmanager.services.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
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
