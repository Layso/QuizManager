package com.layso.quizmanager.datamodel;

import java.util.List;



public class AssociativeAnswer extends Answer {
	private List<String> leftSide, rightSide;
	
	
	
	/**
	 * Constructor to create AssociativeAnswer object
	 * @param quizID        ID of the quiz which includes the question this answer is related to
	 * @param questionID    ID of the question this answer is related to
	 * @param answerer      User who give this answer
	 * @param leftSide      String list of left column answers given by user
	 * @param rightSide     String list of right column answers which associated to left column
	 */
	public AssociativeAnswer(int quizID, int questionID, User answerer, List<String> leftSide, List<String> rightSide) {
		super(quizID, questionID, answerer);
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}
	
	
	
	/**
	 * Getter for left hand side column
	 * @param i Index of the answer
	 * @return  Answer
	 */
	public String GetLeft(int i) {
		return leftSide.get(i);
	}
	
	/**
	 * Getter for the right hand side column
	 * @param i Index of the answer
	 * @return  Answer
	 */
	public String GetRight(int i) {
		return rightSide.get(i);
	}
}
