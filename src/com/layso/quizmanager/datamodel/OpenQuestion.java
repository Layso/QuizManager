package com.layso.quizmanager.datamodel;

import java.util.List;

public class OpenQuestion extends Question {
	private String tips;
	
	
	
	public OpenQuestion(int id, String question, List<String> topics, boolean resource, QuestionType type, String tips) {
		super(id, question, topics, resource, type);
		this.tips = tips;
	}
}
