package com.layso.quizmanager.datamodel;

import java.util.List;
import java.util.Map;

public class AssociativeQuestion extends Question {
	public static final int MINIMUM_ROW_COUNT = 2;
	private List<String> leftColumn;
	private List<String> rightColumn;
	
	
	
	public AssociativeQuestion(int id, String question, List<String> topics, String resource, QuestionType type,
	                           boolean publicity, int difficulty, int correctAnswers, int falseAnswers, User owner,
	                           List<String> leftColumn, List<String> rightColumn) {
		super(id, question, topics, resource, type, publicity, difficulty, correctAnswers, falseAnswers, owner);
		this.leftColumn = leftColumn;
		this.rightColumn = rightColumn;
	}
	
	public List<String> GetLeftColumn() {
		return leftColumn;
	}
	
	public List<String> GetRightColumn() {
		return rightColumn;
	}
	
	public boolean CheckAnswer(AssociativeAnswer answer) {
		for (int i=0; i<leftColumn.size(); ++i) {
			if (leftColumn.indexOf(answer.GetLeft(i)) != rightColumn.indexOf(answer.GetRight(i))) {
				return false;
			}
		}
		
		return true;
	}
}
