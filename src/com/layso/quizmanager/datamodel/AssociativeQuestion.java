package com.layso.quizmanager.datamodel;

import java.util.List;

public class AssociativeQuestion extends Question {
	private List<String> leftColumn;
	private List<String> rightColumn;
	
	
	
	public AssociativeQuestion(int id, String question, List<String> topics, boolean resource, QuestionType type, List<String> leftColumn, List<String> rightColumn) {
		super(id, question, topics, resource, type);
		this.leftColumn = leftColumn;
		this.rightColumn = rightColumn;
	}
}
