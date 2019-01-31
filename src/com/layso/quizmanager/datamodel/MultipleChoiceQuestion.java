package com.layso.quizmanager.datamodel;

import java.util.List;



public class MultipleChoiceQuestion extends Question {
	private String correctAnswer;
	private List<String> otherAnswers;
	
	
	
	public MultipleChoiceQuestion(int id, String question, List<String> topics, boolean resource, QuestionType type, int difficulty, int correctAnswers, int falseAnswers, String correctAnswer, List<String> otherAnswers) {
		super(id, question, topics, resource, type, difficulty, correctAnswers, falseAnswers);
		this.correctAnswer = correctAnswer;
		this.otherAnswers = otherAnswers;
	}
}
