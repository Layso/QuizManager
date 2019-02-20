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
	
	
	
	/**
	 * Protected constructor to let derived classes set the common variables on initialization
	 * @param id                ID of the question
	 * @param question          Text (body) of the question
	 * @param topics            List of topics this question associated to
	 * @param resource          Path of the resource for question, if it has
	 * @param type              QuestionType of the question
	 * @param publicity         Publicity of question
	 * @param difficulty        Difficulty defined by the creator for question
	 * @param correctAnswers    Count of correct answers given to the question
	 * @param falseAnswers      Count of false answers given to the question
	 * @param owner             Creator of the question
	 */
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
				case Owner: result = owner.GetUsername().toLowerCase().contains(criteria); break;
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
	
	
	
	/**
	 * Overriding toString method to let Question be able to printed to screen more understandable for console application
	 * @return Table view of Question as String
	 */
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
	
	
	
	/**
	 * Increases the correct answer count of question
	 */
	public void IncreaseFalseAnswers() {
		falseAnswers += 1;
	}
	
	
	
	/**
	 * Increases the false answer count of question
	 */
	public void IncreaseCorrectAnswers() {
		correctAnswers += 1;
	}
	
	
	
	/**
	 * Getter for console quiz table titles
	 * @return  Titles of columns
	 */
	public static String ConsoleTableTitle() {
		return "Question - Question Type - Topics - Owner - Difficulty - True Difficulty";
	}
	
	
	
	/**
	 * Getter for ID
	 * @return ID of question
	 */
	public int GetID() {
		return id;
	}
	
	/**
	 * Getter for difficulty
	 * @return  Difficulty of question
	 */
	public int GetDifficulty() {
		return difficulty;
	}
	
	/**
	 * Getter for question body
	 * @return  Body of question
	 */
	public String GetQuestion() {
		return question;
	}
	
	/**
	 * Getter for topics
	 * @return  Topics of question
	 */
	public List<String> GetTopics() {
		return topics;
	}
	
	/**
	 * Getter for resource
	 * @return  Resource of question
	 */
	public String GetResource() {
		return resource;
	}
	
	/**
	 * Getter for type
	 * @return  Type of question
	 */
	public QuestionType GetType() {
		return type;
	}
	
	/**
	 * Getter for publicity
	 * @return  Publicity of question
	 */
	public boolean GetPublicity() {
		return publicity;
	}
	
	/**
	 * Getter for correct answer count
	 * @return  Correct answer count of question
	 */
	public int GetCorrectAnswers() {
		return correctAnswers;
	}
	
	/**
	 * Getter for false answer count
	 * @return  False answer count of question
	 */
	public int GetFalseAnswers() {
		return falseAnswers;
	}
	
	/**
	 * Getter for owner
	 * @return  Owner of question
	 */
	public User GetOwner() {
		return owner;
	}
	
	/**
	 * Getter for true difficulty. True difficulty determined by using correct and false answers given to the question
	 * @return True difficulty of question
	 */
	public double GetTrueDifficulty() {
		double totalAnswers = correctAnswers + falseAnswers;
		
		
		// True difficulty is the ratio of true answers over the all answers. It is reversed in formula since higher
		// number means more difficult question. Multiplied by 5 since difficulty is limited between 1 and 5
		Double trueDifficulty = (totalAnswers == 0) ? 0.0 : (falseAnswers / totalAnswers) * 5;
		
		// Limiting difficulty with 1 as lower bound
		if (trueDifficulty < 1) trueDifficulty = 1.0;
		
		return trueDifficulty;
	}
	
	
	
	/**
	 * PropertyValueFactory getter for question body
	 * @return Question body as String
	 */
	public String getQuestionTextTable() {
		return question;
	}
	
	/**
	 * PropertyValueFactory getter for question type
	 * @return Question type as String
	 */
	public String getTypeTable() {
		return type.name();
	}
	
	/**
	 * PropertyValueFactory getter for question difficulty
	 * @return Question difficulty as String
	 */
	public String getDifficultyTable() {
		return Integer.toString(difficulty);
	}
	
	/**
	 * PropertyValueFactory getter for true question difficulty
	 * @return True question difficulty as String
	 */
	public String getTrueDifficultyTable() {
		return String.format("%.2f", GetTrueDifficulty());
	}
	
	/**
	 * PropertyValueFactory getter for question owner
	 * @return Owner name as String
	 */
	public String getOwnerTable() {
		return DatabaseManager.getInstance().GetUsernameByID(owner.GetID());
	}
	
	/**
	 * PropertyValueFactory getter for topics
	 * @return Question topics as String
	 */
	public String getTopicsTable() {
		StringBuilder builder = new StringBuilder();
		
		
		for (String topic : topics) {
			builder.append(topic);
			
			if (topics.indexOf(topic) != topics.size()-1)
				builder.append(" - ");
		}
		
		return builder.toString();
	}
}
