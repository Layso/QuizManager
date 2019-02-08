package com.layso.quizmanager.datamodel;

public class MultipleChoiceAnswer extends Answer {
	private String answer;
	
	public MultipleChoiceAnswer(int quizID, int questionID, User answerer) {
		super(quizID, questionID, answerer);
	}
	
	public String GetAnswer() {
		return answer;
	}
}
