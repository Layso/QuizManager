package com.layso.quizmanager.datamodel;

import java.util.List;

public class AssociativeQuestion extends Question {
	private List<String> leftColumn;
	private List<String> rightColumn;
	
	
	
	public AssociativeQuestion(int id, String question, List<String> topics, String resource, QuestionType type, int difficulty, int correctAnswers, int falseAnswers, int ownerID, List<String> leftColumn, List<String> rightColumn) {
		super(id, question, topics, resource, type, difficulty, correctAnswers, falseAnswers, ownerID);
		this.leftColumn = leftColumn;
		this.rightColumn = rightColumn;
	}
	
	public List<String> GetLeftColumn() {
		return leftColumn;
	}
	
	public List<String> GetRightColumn() {
		return rightColumn;
	}
}
