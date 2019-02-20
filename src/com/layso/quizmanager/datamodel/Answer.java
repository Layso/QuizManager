package com.layso.quizmanager.datamodel;



public abstract class Answer {
	private int quizID;
	private int questionID;
	private User answerer;
	
	
	
	/**
	 * Constructor to set common variables. Declared as protected to let derived classes use the constructor
	 * @param quizID        ID of the question which this answer belongs to
	 * @param questionID
	 * @param answerer
	 */
	protected Answer(int quizID, int questionID, User answerer) {
		this.quizID = quizID;
		this.questionID = questionID;
		this.answerer = answerer;
	}
	
	
	
	/**
	 * Getter for QuizID
	 * @return ID of quiz
	 */
	public int GetQuizID() {
		return quizID;
	}
	
	/**
	 * Getter for answerer
	 * @return User that give the answer
	 */
	public User GetAnswerer() {
		return answerer;
	}
}
