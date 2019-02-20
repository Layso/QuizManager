package com.layso.quizmanager.datamodel;



public class OpenAnswer extends Answer {
	private String answer;
	
	
	
	/**
	 * Constructor to create AssociativeAnswer object
	 * @param quizID        ID of the quiz which includes the question this answer is related to
	 * @param questionID    ID of the question this answer is related to
	 * @param answerer      User who give this answer
	 * @param answer        Answer of the user
	 */
	public OpenAnswer(int quizID, int questionID, User answerer, String answer) {
		super(quizID, questionID, answerer);
		this.answer = answer;
	}
	
	/**
	 * Getter for the answer
	 * @return Answer as String
	 */
	public String GetAnswer() {
		return answer;
	}
}
