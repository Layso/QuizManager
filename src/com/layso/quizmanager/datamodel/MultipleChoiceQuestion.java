package com.layso.quizmanager.datamodel;

import java.util.List;



public class MultipleChoiceQuestion extends Question implements AutoCorrectable {
	private String correctAnswer;
	private List<String> otherAnswers;
	
	
	
	public MultipleChoiceQuestion(int id, String question, List<String> topics, String resource, QuestionType type,
	                              boolean publicity, int difficulty, int correctAnswers, int falseAnswers, User owner,
	                              String correctAnswer, List<String> otherAnswers) {
		super(id, question, topics, resource, type, publicity, difficulty, correctAnswers, falseAnswers, owner);
		this.correctAnswer = correctAnswer;
		this.otherAnswers = otherAnswers;
	}
	
	public String GetCorrectAnswer() {
		return correctAnswer;
	}
	
	public List<String> GetAnswers() {
		return otherAnswers;
	}
	
	@Override
	public boolean CheckAnswer(Answer answer) {
		MultipleChoiceAnswer mcqAnswer = ((MultipleChoiceAnswer) answer);
		return mcqAnswer.GetAnswer().equals(correctAnswer);
	}
}
