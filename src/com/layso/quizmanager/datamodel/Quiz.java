package com.layso.quizmanager.datamodel;

import java.util.List;

public class Quiz {
	int quizOwnerID;
	String quizTitle;
	List<Question> questions;
	int customDifficulty;
	double trueDifficulty;
	double averageDifficulty;
	
	
	
	public Quiz(int quizOwnerID, String quizTitle, List<Question> questions, int customDifficulty, double trueDifficulty, double averageDifficulty) {
		this.quizOwnerID = quizOwnerID;
		this.quizTitle = quizTitle;
		this.questions = questions;
		this.customDifficulty = customDifficulty;
		this.trueDifficulty = trueDifficulty;
		this.averageDifficulty = averageDifficulty;
	}
}
