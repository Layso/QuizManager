package com.layso.quizmanager.datamodel;

import com.layso.quizmanager.services.DatabaseManager;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class Question {
	public enum QuestionType {MultipleChoice, Associative, Open}
	
	private int id;
	private User owner;
	private String question;
	private List<String> topics;
	private String resource;
	private QuestionType type;
	private boolean publicity;
	private int difficulty;
	private int correctAnswers;
	private int falseAnswers;
	
	
	protected Question(int id, String question, List<String> topics, String resource, QuestionType type, boolean publicity, int difficulty, int correctAnswers, int falseAnswers, User owner) {
		this.id = id;
		this.question = question;
		this.topics = topics;
		this.resource = resource;
		this.type = type;
		this.publicity = publicity;
		this.difficulty = difficulty;
		this.correctAnswers = correctAnswers;
		this.falseAnswers = falseAnswers;
		this.owner = owner;
	}
	
	
	
	/* Getters for member fields*/
	public int GetID() {
		return id;
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
	
	public User GetOwner() {
		return owner;
	}
	
	
	
	/* Getters for GUI tables */
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
		return DatabaseManager.getInstance().GetUsernameByID(owner.GetID());
	}
	
	
	
	/**
	 * A getter for property list to associate Question on a TableView
	 * @return  List of PropertyValueFactory for member fields to show on table
	 */
	public static List<PropertyValueFactory> GetPropertyValueFactory() {
		List<PropertyValueFactory> list = new ArrayList<>();
		
		list.add(new PropertyValueFactory<Question,String>("questionTextTable"));
		list.add(new PropertyValueFactory<Question,String>("topicsTable"));
		list.add(new PropertyValueFactory<Question,String>("typeTable"));
		list.add(new PropertyValueFactory<Question,String>("difficultyTable"));
		list.add(new PropertyValueFactory<Question,String>("trueDifficultyTable"));
		list.add(new PropertyValueFactory<Question,String>("ownerTable"));
		
		return list;
	}
	
	
	
	/**
	 * Helper method to see if question text is equals or contains given criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	public boolean FilterQuestionText(String criteria) {
		return question.toLowerCase().equals(criteria.toLowerCase()) || question.toLowerCase().contains(criteria.toLowerCase());
	}
	
	
	
	/**
	 * Helper method to see if question topics includes given criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	public boolean FilterQuestionTopics(String criteria) {
		return getTopicsTable().toLowerCase().equals(criteria.toLowerCase()) || getTopicsTable().toLowerCase().contains(criteria.toLowerCase());
	}
	
	
	
	/**
	 * Helper method to see if question type is equals or contains given criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	public boolean FilterQuestionType(String criteria) {
		return getTypeTable().toLowerCase().equals(criteria.toLowerCase()) || getTypeTable().toLowerCase().contains(criteria.toLowerCase());
	}
	
	
	
	/**
	 * Helper method to see if difficulty of question is higher than given criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	public boolean FilterQuestionDifficulty(String criteria) {
		int criteriaDifficulty;
		
		
		try {
			criteriaDifficulty = Integer.parseInt(criteria);
		} catch (NumberFormatException e) {
			criteriaDifficulty = 0;
		}
		
		return difficulty > criteriaDifficulty;
	}
	
	
	
	/**
	 * Helper method to see if true difficulty of question is higher than given criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	public boolean FilterQuestionTrueDifficulty(String criteria) {
		double actualNumber = Double.parseDouble(getTrueDifficultyTable());
		double criteriaNumber;
		
		
		try {
			criteriaNumber = Double.parseDouble(criteria);
		} catch (NumberFormatException e) {
			criteriaNumber = 0;
		}
		
		return actualNumber > criteriaNumber;
	}
	
	
	
	/**
	 * Helper method to see if question owner includes or equals to given criteria
	 * @param criteria  User input to check if question meets the requirement
	 * @return          Returns true if question meets criteria, else returns false
	 */
	public boolean FilterQuestionOwner(String criteria) {
		return getOwnerTable().toLowerCase().equals(criteria.toLowerCase()) || getOwnerTable().toLowerCase().contains(criteria.toLowerCase());
	}
}
