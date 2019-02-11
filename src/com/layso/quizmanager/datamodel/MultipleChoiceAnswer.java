package com.layso.quizmanager.datamodel;

public class MultipleChoiceAnswer extends Answer {
	private String answer;
	
	public MultipleChoiceAnswer(int quizID, int questionID, User answerer, String answer) {
		super(quizID, questionID, answerer);
		
		System.out.println("MCQ Answer: " + answer);
		this.answer = answer;
	}
	
	public String GetAnswer() {
		return answer;
	}
}
