package com.layso.quizmanager.datamodel;

import com.layso.quizmanager.services.DatabaseManager;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class OpenAnswer extends Answer {
	private String answer;
	
	
	
	public OpenAnswer(int quizID, int questionID, User answerer, String answer) {
		super(quizID, questionID, answerer);
		this.answer = answer;
	}
	
	public String GetAnswer() {
		return answer;
	}
	
	
}
