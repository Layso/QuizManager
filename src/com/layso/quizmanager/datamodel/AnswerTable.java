package com.layso.quizmanager.datamodel;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;



public class AnswerTable implements Searchable {
	public enum AnswerTableSearchTerms {Name, QuestionCount, TrueAnswers, FalseAnswers, UncheckedAnswers, Percentage}
	
	private Quiz quiz;
	private User answerer;
	private int notCorrectedAnswers;
	private int trueAnswers;
	private int falseAnswers;
	
	
	
	/**
	 * Constructor to set variables on initialization
	 * @param quiz                  Quiz associated with answer table
	 * @param answerer              User who solved the quiz
	 * @param notCorrectedAnswers   Number of questions that are not evaluated
	 * @param falseAnswers          Number of false questions
	 * @param trueAnswers           Number of true questions
	 */
	public AnswerTable(Quiz quiz, User answerer, int notCorrectedAnswers, int falseAnswers, int trueAnswers){
		this.quiz = quiz;
		this.answerer = answerer;
		this.trueAnswers = trueAnswers;
		this.falseAnswers = falseAnswers;
		this.notCorrectedAnswers = notCorrectedAnswers;
	}
	
	
	
	/**
	 * A getter for property list to associate AnswerTable on a TableView
	 * @return  List of PropertyValueFactory for member fields to show on table
	 */
	public static List<PropertyValueFactory> GetPropertyValueFactory() {
		List<PropertyValueFactory> list = new ArrayList<>();
		
		list.add(new PropertyValueFactory<Quiz,String>("nameTable"));
		list.add(new PropertyValueFactory<Quiz,String>("questionCountTable"));
		list.add(new PropertyValueFactory<Quiz,String>("trueAnswers"));
		list.add(new PropertyValueFactory<Quiz,String>("falseAnswers"));
		list.add(new PropertyValueFactory<Quiz,String>("uncheckedAnswers"));
		list.add(new PropertyValueFactory<Quiz,String>("percentage"));
		list.add(new PropertyValueFactory<Quiz,String>("quizSolver"));
		return list;
	}
	
	
	
	/**
	 * Search method implementation for Searchable interface to be able to compare with given criteria of given term
	 * @param criteria  Criteria to look if answer table provides
	 * @param term      Term to search the given criteria
	 * @return          Boolean result, true if the criteria is provided, else false
	 */
	@Override
	public boolean Search(String criteria, String term) {
		AnswerTableSearchTerms termEnum = AnswerTableSearchTerms.valueOf(term.replace(" ",""));
		criteria = criteria.toLowerCase();
		boolean result = false;
		
		
		try {
			switch (termEnum) {
				case Name: result = quiz.GetQuizTitle().toLowerCase().contains(criteria); break;
				case TrueAnswers: result = trueAnswers >= Integer.parseInt(criteria); break;
				case FalseAnswers: result = falseAnswers >= Integer.parseInt(criteria); break;
				case QuestionCount: result = quiz.GetQuestions().size() >= Integer.parseInt(criteria); break;
				case UncheckedAnswers: result = notCorrectedAnswers >= Integer.parseInt(criteria); break;
				case Percentage: result = ((int) (((double) trueAnswers) / (falseAnswers + trueAnswers) * 100)) >= Integer.parseInt(criteria); break;
			}
		} catch (NumberFormatException e) {
			result = false;
		}
		
		return result;
	}
	
	
	
	/**
	 * Overriding toString method to let AnswerTable be able to printed to screen more understandable for console application
	 * @return Table view of AnswerTable as String
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		
		builder.append(getNameTable());
		builder.append(" - ");
		builder.append(getQuestionCountTable());
		builder.append(" - ");
		builder.append(getTrueAnswers());
		builder.append(" - ");
		builder.append(getFalseAnswers());
		builder.append(" - ");
		builder.append(getUncheckedAnswers());
		builder.append(" - ");
		builder.append(getPercentage());
		builder.append(" - ");
		builder.append(getQuizSolver());
		
		return builder.toString();
	}
	
	
	
	/**
	 * Method to get console table column titles
	 * @return  String of titles
	 */
	public static String ConsoleTableTitle() {
		return "Quiz Name - Question Count - True Answers - False Answers - Unchecked Answers - Percentage";
	}
	
	
	
	/**
	 * Getter for not evaluated answers
	 * @return  notCorrectedAnswers ans int
	 */
	public int GetNotCorrectedAnswers() {
		return notCorrectedAnswers;
	}
	
	/**
	 * Getter for true answers
	 * @return  True answer count
	 */
	public int GetTrueAnswers() {
		return trueAnswers;
	}
	
	/**
	 * Getter for false answers
	 * @return  False answer count
	 */
	public int GetFalseAnswers() {
		return falseAnswers;
	}
	
	/**
	 * Getter for Quiz
	 * @return  Quiz that the answer table is associated with
	 */
	public Quiz GetQuiz() {
		return quiz;
	}
	
	/**
	 * Getter for answerer
	 * @return  User who solved the Quiz
	 */
	public User GetAnswerer() {
		return answerer;
	}
	
	
	
	/**
	 * PropertyValueFactory getter for quiz title
	 * @return  Quiz title as string
	 */
	public String getNameTable() {
		return quiz.GetQuizTitle();
	}
	
	/**
	 * PropertyValueFactory getter for question count
	 * @return  Question count as string
	 */
	public String getQuestionCountTable() {
		return Integer.toString(quiz.GetQuestions().size());
	}
	
	/**
	 * PropertyValueFactory getter for true answer count
	 * @return  True answer count as string
	 */
	public String getTrueAnswers() {
		return Integer.toString(trueAnswers);
	}
	
	/**
	 * PropertyValueFactory getter for false answer count
	 * @return  False answer count as string
	 */
	public String getFalseAnswers() {
		return Integer.toString(falseAnswers);
	}
	
	/**
	 * PropertyValueFactory getter for not evaluated answer count
	 * @return  Not evaluated answer count as string
	 */
	public String getUncheckedAnswers() {
		return Integer.toString(notCorrectedAnswers);
	}
	
	/**
	 * PropertyValueFactory getter for percentage. Percentage shows the success rate for the answerer of quiz
	 * @return  Percentage as string
	 */
	public String getPercentage() {
		return  "%" + ((int) (((double) trueAnswers) / (falseAnswers + trueAnswers) * 100));
	}
	
	/**
	 * PropertyValueFactory getter for answerer
	 * @return  Username of the solver of the quiz
	 */
	public String getQuizSolver() {
		return answerer.GetUsername();
	}
}
