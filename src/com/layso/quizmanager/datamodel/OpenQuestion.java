package com.layso.quizmanager.datamodel;

import java.util.List;

public class OpenQuestion extends Question {
	private String tips;
	
	
	
	public OpenQuestion(int id, String question, List<String> topics, boolean resource, QuestionType type, int difficulty, int correctAnswers, int falseAnswers, String tips) {
		super(id, question, topics, resource, type, difficulty, correctAnswers, falseAnswers);
		this.tips = tips;
	}
}
