package com.layso.quizmanager.datamodel;

import com.layso.quizmanager.services.DatabaseManager;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Question implements Searchable{
	public enum QuestionType {MultipleChoice, Associative, Open}
	public enum QuestionSearchTerms{Question, Topics, Difficulty, TrueDifficulty, Type, Owner}
	
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
		this.resource = new File(resource).exists() ? resource : "";
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
	
	public double GetTrueDifficulty() {
		double totalAnswers = correctAnswers + falseAnswers;
		Double trueDifficulty = (totalAnswers == 0) ? 0.0 : (falseAnswers / totalAnswers) * 5;
		
		if (falseAnswers == 0 && totalAnswers > 0) trueDifficulty = 1.0;
		else if (trueDifficulty < 1) trueDifficulty = 1.0;
		
		
		
		return trueDifficulty;
	}
	
	public void IncreaseFalseAnwers() {
		falseAnswers += 1;
	}
	
	public void IncreaseCorrectAnswers() {
		correctAnswers += 1;
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
		return String.format("%.2f", GetTrueDifficulty());
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
	 * Search method implementation for Searchable interface to be able to compare with given criteria of given term
	 * @param criteria  Criteria to look if question provides
	 * @param term      Term to search the given criteria
	 * @return          Boolean result, true if the criteria is provided, else false
	 */
	@Override
	public boolean Search(String criteria, String term) {
		QuestionSearchTerms termEnum = QuestionSearchTerms.valueOf(term.replace(" ", ""));
		criteria = criteria.toLowerCase();
		boolean result = false;
		
		
		try {
			switch (termEnum) {
				case Type: result = type.name().toLowerCase().contains(criteria); break;
				case Owner: result = owner.getUsername().toLowerCase().contains(criteria); break;
				case Topics: result = getTopicsTable().contains(criteria); break;
				case Question: result = question.toLowerCase().contains(criteria); break;
				case Difficulty: result = difficulty >= Integer.parseInt(criteria); break;
				case TrueDifficulty: result = GetTrueDifficulty() >= Integer.parseInt(criteria); break;
			}
		} catch (NumberFormatException e) {
			result = false;
		}
		
		return result;
	}
	
	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		
		build.append(getQuestionTextTable());
		build.append(" - ");
		build.append(getTypeTable());
		build.append(" - ");
		build.append(getTopicsTable());
		build.append(" - ");
		build.append(getOwnerTable());
		build.append(" - ");
		build.append(getDifficultyTable());
		build.append(" - ");
		build.append(getTrueDifficultyTable());
		
		
		return build.toString();
	}
}
