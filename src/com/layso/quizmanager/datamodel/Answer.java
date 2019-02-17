package com.layso.quizmanager.datamodel;

public abstract class Answer {
	int quizID;
	int questionID;
	User answerer;
	
	
	protected Answer(int quizID, int questionID, User answerer) {
		this.quizID = quizID;
		this.questionID = questionID;
		this.answerer = answerer;
	}
	
	public int GetQuizID() {
		return quizID;
	}
	
	public User GetAnswerer() {
		return answerer;
	}
}
