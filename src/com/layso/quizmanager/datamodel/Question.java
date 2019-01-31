package com.layso.quizmanager.datamodel;

import java.util.List;

public abstract class Question {
	public enum QuestionType {MultipleChoice, Associative, Open}
	
	private int id;
	private String question;
	private List<String> topics;
	private boolean resource;
	private QuestionType type;
	private int difficulty;
	private int correctAnswers;
	private int falseAnswers;
	
	
	
	protected Question(int id, String question, List<String> topics, boolean resource, QuestionType type, int difficulty, int correctAnswers, int falseAnswers) {
		this.id = id;
		this.question = question;
		this.topics = topics;
		this.resource = resource;
		this.type = type;
		this.difficulty = difficulty;
		this.correctAnswers = correctAnswers;
		this.falseAnswers = falseAnswers;
	}
	
	public int GetDifficulty() {
		return difficulty;
	}
}
