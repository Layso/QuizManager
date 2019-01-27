package com.layso.quizmanager.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

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
	
	public void ChangeNavigation(ActionEvent event) {
		for (Tab tab : quizTypeTabs.getTabs()) {
			if (tab.getId().equals(((Node) event.getSource()).getId())) {
				quizTypeTabs.getSelectionModel().select(tab);
				Clear(event);
			}
		}
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
