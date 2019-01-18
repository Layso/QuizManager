package com.layso.quizmanager.gui;

import com.layso.quizmanager.services.QuizManager;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;



public class LoginMenuController {
	public void ChangeNavigation(ActionEvent event) {
		final String tabPaneId = "#navigationTabs";
		ToggleButton toggleButton = ((ToggleButton) event.getSource());
		TabPane tp_tabs = ((TabPane) toggleButton.getScene().lookup(tabPaneId));
		
		
		for (Tab tab : tp_tabs.getTabs()) {
			if (tab.getId().equals(((Node) event.getSource()).getId())) {
				tp_tabs.getSelectionModel().select(tab);
			}
		}
	}
	
	public void QuitButton(ActionEvent event) {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
	}
}
