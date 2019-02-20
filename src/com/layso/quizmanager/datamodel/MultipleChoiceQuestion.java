package com.layso.quizmanager.datamodel;

import java.util.List;



public class MultipleChoiceQuestion extends Question implements AutoCorrectable {
	// Required number of other answers
	public static final int OTHER_ANSWER_COUNT = 3;
	
	private String correctAnswer;
	private List<String> otherAnswers;
	
	
	
	/**
	 * Constructor set variables on initialization
	 * @param id                ID of the question
	 * @param question          Text (body) of the question
	 * @param topics            List of topics this question associated to
	 * @param resource          Path of the resource for question, if it has
	 * @param type              QuestionType of the question
	 * @param publicity         Publicity of question
	 * @param difficulty        Difficulty defined by the creator for question
	 * @param correctAnswers    Count of correct answers given to the question
	 * @param falseAnswers      Count of false answers given to the question
	 * @param owner             Creator of the question
	 * @param correctAnswer     Answer that is considered as correct for this question
	 * @param otherAnswers      List of other answers that is going to be shown to the user
	 */
	public MultipleChoiceQuestion(int id, String question, List<String> topics, String resource, QuestionType type,
	                              boolean publicity, int difficulty, int correctAnswers, int falseAnswers, User owner,
	                              String correctAnswer, List<String> otherAnswers) {
		super(id, question, topics, resource, type, publicity, difficulty, correctAnswers, falseAnswers, owner);
		this.correctAnswer = correctAnswer;
		this.otherAnswers = otherAnswers;
	}
	
	
	
	/**
	 * CheckAnswer implementation to make Multiple Choice Question auto correctable once it is solved/answered
	 * @param answer    Amswer given by the user for this question
	 * @return          Boolean value of correction status
	 */
	@Override
	public boolean CheckAnswer(Answer answer) {
		// To consider answer is correct, answer of the user should match the correct answer
		MultipleChoiceAnswer mcqAnswer = ((MultipleChoiceAnswer) answer);
		return mcqAnswer.GetAnswer().equals(correctAnswer);
	}
	
	
	
	/**
	 * Getter for correct answer
	 * @return  Correct answer of the question
	 */
	public String GetCorrectAnswer() {
		return correctAnswer;
	}
	
	/**
	 * Getter for the other answers
	 * @return  List of other answers which are not correct
	 */
	public List<String> GetAnswers() {
		return otherAnswers;
	}
}
