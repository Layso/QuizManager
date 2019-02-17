package com.layso.quizmanager.gui;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.CfgManager;
import com.layso.quizmanager.services.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ResourceBundle;



public class MainMenuController extends Controller implements Initializable {
	@FXML
	Button skipButton;
	
	@FXML
	ImageView img;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		skipButton.setDefaultButton(true);
		Logger.Log("Main Menu initialized", Logger.LogType.INFO);
	}
	
	public void Skip(ActionEvent event) {
		ChangeScene(event, WindowStage.CreateQuizMenu);
	}
	
	
	
	public void EditDeleteButton(ActionEvent event) {
		ChangeScene(event, WindowStage.EditDeleteQuizMenu);
	}
	
	public void SolveQuizButton(ActionEvent event) {
		ChangeScene(event, WindowStage.SolveQuizMenu);
	}
	
	public void SeeResultsButton(ActionEvent event) {
		ChangeScene(event, WindowStage.SeeResultsMenu);
	}
	
	public void CorrectAnswer(ActionEvent event) {
		ChangeScene(event, WindowStage.CheckAnswersMenu);
	}
	
	public void UserPromotion(ActionEvent event) {
		ChangeScene(event, WindowStage.UserPromoteMenu);
	}
	
	public void Test(ActionEvent event) {
		Image image = new Image("file:" + DatabaseManager.getInstance().GetResourceNameByQuestionID(9), 400, 200, true, true);
		img.setImage(image);
	}
}
