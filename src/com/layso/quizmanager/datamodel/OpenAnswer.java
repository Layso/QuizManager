package com.layso.quizmanager.datamodel;

public class OpenAnswer extends Answer {
	private String answer;
	
	public OpenAnswer(int quizID, int questionID, User answerer, String answer) {
		super(quizID, questionID, answerer);
		
		this.answer = answer;
	}
	
	public String GetAnswer() {
		return answer;
	}
}
