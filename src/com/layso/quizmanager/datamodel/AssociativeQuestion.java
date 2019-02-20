package com.layso.quizmanager.datamodel;

import java.util.List;



public class AssociativeQuestion extends Question implements AutoCorrectable{
	// Minimum number of rows allowed to create an Associative Question
	public static final int MINIMUM_ROW_COUNT = 2;
	// Maximum number of rows allowed to create an Associative Question
	public static final int MAXIMUM_ROW_COUNT = 5;
	
	private List<String> leftColumn;
	private List<String> rightColumn;
	
	
	
	/**
	 * Constructor set variables on initialization
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
	 * @param leftColumn        Left column of selectable choices
	 * @param rightColumn       Right column of selectable choices associated with left column
	 */
	public AssociativeQuestion(int id, String question, List<String> topics, String resource, QuestionType type,
	                           boolean publicity, int difficulty, int correctAnswers, int falseAnswers, User owner,
	                           List<String> leftColumn, List<String> rightColumn) {
		super(id, question, topics, resource, type, publicity, difficulty, correctAnswers, falseAnswers, owner);
		this.leftColumn = leftColumn;
		this.rightColumn = rightColumn;
	}
	
	
	
	/**
	 * CheckAnswer implementation to make Associative Question auto correctable once it is solved/answered
	 * @param answer    Amswer given by the user for this question
	 * @return          Boolean value of correction status
	 */
	@Override
	public boolean CheckAnswer(Answer answer) {
		AssociativeAnswer associativeAnswer = ((AssociativeAnswer) answer);
		
		
		// To consider the answer is true, indexes of associated Strings should be match with original association of answers
		// If there is any single mismatch, declare the answer as false
		for (int i=0; i<leftColumn.size(); ++i) {
			if (leftColumn.indexOf(associativeAnswer.GetLeft(i)) != rightColumn.indexOf(associativeAnswer.GetRight(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	
	
	/**
	 * Getter for left column
	 * @return  String list of left column
	 */
	public List<String> GetLeftColumn() {
		return leftColumn;
	}
	
	/**
	 * Getter for right column
	 * @return String list of right column
	 */
	public List<String> GetRightColumn() {
		return rightColumn;
	}
}
