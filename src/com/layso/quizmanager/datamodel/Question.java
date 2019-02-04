package com.layso.quizmanager.datamodel;

import com.layso.quizmanager.services.DatabaseManager;

import java.util.List;

public abstract class Question {
	public enum QuestionType {MultipleChoice, Associative, Open}
	
	private int id;
	private String question;
	private List<String> topics;
	private String resource;
	private QuestionType type;
	private boolean publicity;
	private int difficulty;
	private int correctAnswers;
	private int falseAnswers;
	private int ownerID;
	
	
	protected Question(int id, String question, List<String> topics, String resource, QuestionType type, boolean publicity, int difficulty, int correctAnswers, int falseAnswers, int ownerID) {
		this.id = id;
		this.question = question;
		this.topics = topics;
		this.resource = resource;
		this.type = type;
		this.publicity = publicity;
		this.difficulty = difficulty;
		this.correctAnswers = correctAnswers;
		this.falseAnswers = falseAnswers;
		this.ownerID = ownerID;
	}
	
	public int GetDifficulty() {
		return difficulty;
	}
	
	public String GetQuestion() {
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
	
	public boolean GetPublicity() {
		return publicity;
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
	
	
	public String getQuestionTextTable() {
		return question;
	}
	
	public String getTopicsTable() {
		StringBuilder builder = new StringBuilder();
		
		
		for (String topic : topics) {
			builder.append(topic);
			
			if (topics.indexOf(topic) != topics.size()-1)
				builder.append(" - ");
		}
		
		return builder.toString();
	}
	
	public String getTypeTable() {
		return type.name();
	}
	
	public String getDifficultyTable() {
		return Integer.toString(difficulty);
	}
	
	public String getTrueDifficultyTable() {
		double totalAnswers = correctAnswers + falseAnswers;
		Double trueDifficulty = (totalAnswers == 0) ? 0.0 : (falseAnswers / totalAnswers) * 5;
		
		return String.format("%.2f", trueDifficulty);
	}
	
	public String getOwnerTable() {
		return DatabaseManager.getInstance().GetUsernameByID(ownerID);
	}
}
