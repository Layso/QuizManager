package com.layso.quizmanager.datamodel;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;



public class NotCorrectedOpenQuestion implements Searchable {
	public enum NotCorrectedQuestionSearchTerms {Question, Answer, Answerer}
	
	private int id;
	private Quiz quiz;
	private OpenQuestion question;
	private String answer;
	private User answerer;
	
	
	
	/**
	 * Constructor to set variables on initialization
	 * @param id        ID of evaluation
	 * @param quiz      ID of quiz that includes the question
	 * @param question  ID of open question
	 * @param answer    Answer given by User
	 * @param answerer  User that give the answer
	 */
	public NotCorrectedOpenQuestion(int id, Quiz quiz, OpenQuestion question, String answer, User answerer) {
		this.id = id;
		this.quiz = quiz;
		this.question = question;
		this.answer = answer;
		this.answerer = answerer;
	}
	
	
	
	/**
	 * A getter for property list to associate NotCorrectedOpenQuestion on a TableView
	 * @return  List of PropertyValueFactory for member fields to show on table
	 */
	public static List<PropertyValueFactory> GetPropertyValueFactory() {
		List<PropertyValueFactory> list = new ArrayList<>();
		
		list.add(new PropertyValueFactory<Quiz,String>("question"));
		list.add(new PropertyValueFactory<Quiz,String>("answer"));
		list.add(new PropertyValueFactory<Quiz,String>("answerer"));
		
		return list;
	}
	
	
	
	/**
	 * Search method implementation for Searchable interface to be able to compare with given criteria of given term
	 * @param criteria  Criteria to look if this not corrected answer provides
	 * @param term      Term to search the given criteria
	 * @return          Boolean result, true if the criteria is provided, else false
	 */
	@Override
	public boolean Search(String criteria, String term) {
		NotCorrectedQuestionSearchTerms termEnum = NotCorrectedQuestionSearchTerms.valueOf(term.replace(" ",""));
		criteria = criteria.toLowerCase();
		boolean result = false;
		
		
		try {
			switch (termEnum) {
				case Question: result = getQuestion().toLowerCase().contains(criteria); break;
				case Answer: result = answer.toLowerCase().contains(criteria); break;
				case Answerer: result = answerer.GetUsername().toLowerCase().contains(criteria); break;
			}
		} catch (NumberFormatException e) {
			result = false;
		}
		
		return result;
	}
	
	
	
	/**
	 * Overriding toString method to let NotCorrectedOpenQuestion be able to printed to screen more understandable for console application
	 * @return Table view of NotCorrectedOpenQuestion as String
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		
		builder.append(getQuestion());
		builder.append(" - ");
		builder.append(getAnswer());
		builder.append(" - ");
		builder.append(getAnswerer());
		
		return builder.toString();
	}
	
	
	
	/**
	 * Getter for table titles for console
	 * @return  Titles of columns for console table
	 */
	public static String ConsoleTableTitle() {
		return "Question - Answer - Answerer";
	}
	
	
	
	/**
	 * Getter for ID
	 * @return ID of not corrected answer
	 */
	public int GetID() {
		return id;
	}
	
	/**
	 * Getter for Quiz
	 * @return Quiz associated with not corrected answer
	 */
	public Quiz GetQuiz() {
		return quiz;
	}
	
	/**
	 * Getter for Question
	 * @return Question associated with not corrected answer
	 */
	public OpenQuestion GetQuestion() {
		return question;
	}
	
	/**
	 * Getter for answer
	 * @return Answer given by the user
	 */
	public String GetAnswer() {
		return answer;
	}
	
	/**
	 * Getter for answerer
	 * @return User that give the answer
	 */
	public User GetAnswerer() {
		return answerer;
	}
	
	
	
	/**
	 * PropertyValueFactory getter for question
	 * @return  Question body as string
	 */
	public String getQuestion() {
		return question.GetQuestion();
	}
	
	/**
	 * PropertyValueFactory getter for answer
	 * @return  Given answer as string
	 */
	public String getAnswer() {
		return answer;
	}
	
	/**
	 * PropertyValueFactory getter for answerer
	 * @return  Username of User as string
	 */
	public String getAnswerer() {
		return answerer.GetUsername();
	}
}
