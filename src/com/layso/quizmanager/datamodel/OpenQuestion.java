package com.layso.quizmanager.datamodel;

import java.util.List;

public class OpenQuestion extends Question {
	private String tips;
	
	
	
	public OpenQuestion(int id, String question, List<String> topics, String resource, QuestionType type,
	                    boolean publicity, int difficulty, int correctAnswers, int falseAnswers, int ownerID,
	                    String tips) {
		super(id, question, topics, resource, type, publicity, difficulty, correctAnswers, falseAnswers, ownerID);
		this.tips = tips;
	}
	
	public String GetTips() {
		return tips;
	}
}
