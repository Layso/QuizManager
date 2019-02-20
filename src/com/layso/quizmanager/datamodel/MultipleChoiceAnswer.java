package com.layso.quizmanager.datamodel;



public class MultipleChoiceAnswer extends Answer {
	private String answer;
	
	
	
	/**
	 * Constructor to create MultipleChoiceAnswer object
	 * @param quizID        ID of the quiz which includes the question this answer is related to
	 * @param questionID    ID of the question this answer is related to
	 * @param answerer      User who give this answer
	 * @param answer        Answer of the user
	 */
	public MultipleChoiceAnswer(int quizID, int questionID, User answerer, String answer) {
		super(quizID, questionID, answerer);
		this.answer = answer;
	}
	
	/**
	 * Getter for the answer
	 * @return  Answer as String
	 */
	public String GetAnswer() {
		return answer;
	}
}
