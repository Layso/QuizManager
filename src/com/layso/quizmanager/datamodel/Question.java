package com.layso.quizmanager.datamodel;

import java.util.List;

public abstract class Question {
	public enum QuestionType {MultipleChoice, Associative, Open}
	
	private int id;
	private String question;
	private List<String> topics;
	private String resource;
	private QuestionType type;
	private int difficulty;
	private int correctAnswers;
	private int falseAnswers;
	private int ownerID;
	
	
	protected Question(int id, String question, List<String> topics, String resource, QuestionType type, int difficulty, int correctAnswers, int falseAnswers, int ownerID) {
		this.id = id;
		this.question = question;
		this.topics = topics;
		this.resource = resource;
		this.type = type;
		this.difficulty = difficulty;
		this.correctAnswers = correctAnswers;
		this.falseAnswers = falseAnswers;
		this.ownerID = ownerID;
	}
	
	public int GetDifficulty() {
		return difficulty;
	}
	
	public String GetQuestionText() {
		return question;
	}
	
	public List<String> GetTopics() {
		return topics;
	}
	
	public String GetResource() {
		return resource;
	}
	
	public QuestionType GetType() {
		return type;
	}
	
	public int GetCorrectAnswers() {
		return correctAnswers;
	}
	
	public int GetFalseAnswers() {
		return falseAnswers;
	}
	
	public int GetOwnerID() {
		return ownerID;
	}
}
