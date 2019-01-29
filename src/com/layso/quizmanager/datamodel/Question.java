package com.layso.quizmanager.datamodel;

import java.util.List;

public abstract class Question {
	public enum QuestionType {MultipleChoice, Associative, Open}
	
	private int id;
	private String question;
	private List<String> topics;
	private boolean resource;
	private QuestionType type;
	
	
	
	protected Question(int id, String question, List<String> topics, boolean resource, QuestionType type) {
		this.id = id;
		this.question = question;
		this.topics = topics;
		this.resource = resource;
		this.type = type;
	}
}
