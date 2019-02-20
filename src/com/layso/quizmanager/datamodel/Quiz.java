package com.layso.quizmanager.datamodel;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;



public class Quiz implements Searchable {
	public enum QuizSearchTerms {Name, QuestionCount, Difficulty, TrueDifficulty}
	
	private int id;
	private int quizOwnerID;
	private String quizTitle;
	private List<Question> questions;
	private int customDifficulty;
	private double trueDifficulty;
	private double averageDifficulty;
	private boolean publicity;
	
	
	
	/**
	 * Constructor to set variables on initialization
	 * @param id                ID of the quiz
	 * @param quizOwnerID       ID of the User who created the quiz
	 * @param quizTitle         Title of the quiz
	 * @param questions         Question list of the quiz
	 * @param customDifficulty  Custom difficulty (if defined) of the quiz
	 * @param trueDifficulty    True difficulty of the quiz
	 * @param averageDifficulty Average difficulty of the quiz
	 * @param publicity         Publicity of the quiz
	 */
	public Quiz(int id, int quizOwnerID, String quizTitle, List<Question> questions, int customDifficulty, double trueDifficulty, double averageDifficulty, boolean publicity) {
		this.id = id;
		this.quizOwnerID = quizOwnerID;
		this.quizTitle = quizTitle;
		this.questions = questions;
		this.customDifficulty = customDifficulty;
		this.trueDifficulty = trueDifficulty;
		this.averageDifficulty = averageDifficulty;
		this.publicity = publicity;
	}
	
	
	
	/**
	 * Method to recalculate the average and true difficulty of the quiz by getting all of the questions if there is
	 * a change for the questions
	 */
	public void RecalculateDifficulties() {
		double newAverageDifficulty = 0;
		double newTrueDifficulty = 0;
		
		for (Question question : questions) {
			newAverageDifficulty += ((double) question.GetDifficulty());
			newTrueDifficulty += question.GetTrueDifficulty();
		}
		
		averageDifficulty = newAverageDifficulty / questions.size();
		trueDifficulty = newTrueDifficulty / questions.size();
	}
	
	
	
	/**
	 * A getter for property list to associate Quiz on a TableView
	 * @return  List of PropertyValueFactory for member fields to show on table
	 */
	public static List<PropertyValueFactory> GetPropertyValueFactory() {
		List<PropertyValueFactory> list = new ArrayList<>();
		
		list.add(new PropertyValueFactory<Quiz,String>("quizNameTable"));
		list.add(new PropertyValueFactory<Quiz,String>("questionCountTable"));
		list.add(new PropertyValueFactory<Quiz,String>("difficultyTable"));
		list.add(new PropertyValueFactory<Quiz,String>("trueDifficultyTable"));
		
		return list;
	}
	
	
	
	/**
	 * Search method implementation for Searchable interface to be able to compare with given criteria of given term
	 * @param criteria  Criteria to look if quiz provides
	 * @param term      Term to search the given criteria
	 * @return          Boolean result, true if the criteria is provided, else false
	 */
	@Override
	public boolean Search(String criteria, String term) {
		QuizSearchTerms termEnum = QuizSearchTerms.valueOf(term.replace(" ",""));
		criteria = criteria.toLowerCase();
		boolean result = false;
		
		
		try {
			switch (termEnum) {
				case Name: result = quizTitle.toLowerCase().contains(criteria); break;
				case Difficulty: result = (customDifficulty == -1 ? averageDifficulty : customDifficulty) >= Integer.parseInt(criteria); break;
				case QuestionCount: result = questions.size() >= Integer.parseInt(criteria); break;
				case TrueDifficulty: result = trueDifficulty >= Integer.parseInt(criteria); break;
			}
		} catch (NumberFormatException e) {
			result = false;
		}
		
		return result;
	}
	
	
	
	/**
	 * Overriding toString method to let Quiz be able to printed to screen more understandable for console application
	 * @return Table view of Quiz as String
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		
		builder.append(getQuizNameTable());
		builder.append(" - ");
		builder.append(getQuestionCountTable());
		builder.append(" - ");
		builder.append(getDifficultyTable());
		builder.append(" - ");
		builder.append(getTrueDifficultyTable());
		
		return builder.toString();
	}
	
	
	
	/**
	 * Returns the titles of columns for console table view
	 * @return  Column titles for console
	 */
	public static String ConsoleTableTitle() {
		return "Quizzes: Quiz Title - Question Count - Difficulty - True Difficulty";
	}
	
	
	
	/**
	 * Getter for quiz ID
	 * @return  ID of quiz
	 */
	public int GetID() {
		return id;
	}
	
	/**
	 * Getter for owner ID
	 * @return  ID of owner
	 */
	public int GetOwnerID() {
		return quizOwnerID;
	}
	
	/**
	 * Getter for title of the quiz
	 * @return  Title of the quiz
	 */
	public String GetQuizTitle() {
		return quizTitle;
	}
	
	/**
	 * Getter for the custom difficulty of quiz
	 * @return  Custom difficulty of quiz
	 */
	public int GetCustomDifficulty() {
		return customDifficulty;
	}
	
	/**
	 * Getter for the average difficulty of quiz
	 * @return  Average difficulty of quiz
	 */
	public double GetAverageDifficulty() {
		return averageDifficulty;
	}
	
	/**
	 * Getter for the true difficulty of quiz
	 * @return  True difficulty of quiz
	 */
	public double GetTrueDifficulty() {
		return trueDifficulty;
	}
	
	/**
	 * Getter for the publicity of quiz
	 * @return  Publicity of quiz
	 */
	public boolean GetPublicity() {
		return publicity;
	}
	
	/**
	 * Getter for the questions of quiz
	 * @return  Question list of quiz
	 */
	public List<Question> GetQuestions() {
		return questions;
	}
	
	
	
	/**
	 * PropertyValueFactory getter for quiz name
	 * @return  Name of the quiz
	 */
	public String getQuizNameTable() {
		return quizTitle;
	}
	
	/**
	 * PropertyValueFactory getter for question count
	 * @return  Question count of the quiz
	 */
	public String getQuestionCountTable() {
		return Integer.toString(questions.size());
	}
	
	/**
	 * PropertyValueFactory getter for quiz difficulty
	 * @return  Difficulty of the quiz
	 */
	public String getDifficultyTable() {
		return customDifficulty == -1 ? Double.toString(averageDifficulty) : Integer.toString(customDifficulty);
	}
	
	/**
	 * PropertyValueFactory getter for true difficulty of quiz
	 * @return  True difficulty of the quiz
	 */
	public String getTrueDifficultyTable() {
		return Double.toString(trueDifficulty);
	}
}
