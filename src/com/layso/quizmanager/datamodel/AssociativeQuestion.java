package com.layso.quizmanager.datamodel;

import java.util.List;

public class AssociativeQuestion extends Question implements AutoCorrectable{
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
	
	@Override
	public boolean CheckAnswer(Answer answer) {
		AssociativeAnswer associativeAnswer = ((AssociativeAnswer) answer);
		
		
		for (int i=0; i<leftColumn.size(); ++i) {
			if (leftColumn.indexOf(associativeAnswer.GetLeft(i)) != rightColumn.indexOf(associativeAnswer.GetRight(i))) {
				return false;
			}
		}
		
		return true;
	}
}
