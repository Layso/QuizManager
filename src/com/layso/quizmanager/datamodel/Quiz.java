package com.layso.quizmanager.datamodel;

import java.util.List;

public class Quiz {
	int quizOwnerID;
	String quizTitle;
	List<Question> questions;
	int customDifficulty;
	double trueDifficulty;
	double averageDifficulty;
	boolean publicity;
	
	
	
	public Quiz(int quizOwnerID, String quizTitle, List<Question> questions, int customDifficulty, double trueDifficulty, double averageDifficulty, boolean publicity) {
		this.quizOwnerID = quizOwnerID;
		this.quizTitle = quizTitle;
		this.questions = questions;
		this.customDifficulty = customDifficulty;
		this.trueDifficulty = trueDifficulty;
		this.averageDifficulty = averageDifficulty;
		this.publicity = publicity;
	}
	
	
	public String GetTitle() {
		return quizTitle;
	}
	
	public int GetCustomDifficulty() {
		return customDifficulty;
	}
	
	public double GetAverageDifficulty() {
		return averageDifficulty;
	}
	
	public double GetTrueDifficulty() {
		return trueDifficulty;
	}
	
	public boolean GetPublicity() {
		return publicity;
	}
	
	public List<Question> GetQuestions() {
		return questions;
	}
}
