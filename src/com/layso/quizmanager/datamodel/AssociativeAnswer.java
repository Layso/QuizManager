package com.layso.quizmanager.datamodel;

import java.util.List;

public class AssociativeAnswer extends Answer {
	private List<String> leftSide, rightSide;
	
	public AssociativeAnswer(int quizID, int questionID, User answerer, List<String> leftSide, List<String> rightSide) {
		super(quizID, questionID, answerer);
		
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}
}
