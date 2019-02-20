package com.layso.quizmanager.datamodel;

import java.util.List;



public class OpenQuestion extends Question  {
	private String tips;
	
	
	
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
	 * @param tips              Tips for the question
	 */
	public OpenQuestion(int id, String question, List<String> topics, String resource, QuestionType type,
	                    boolean publicity, int difficulty, int correctAnswers, int falseAnswers, User owner,
	                    String tips) {
		super(id, question, topics, resource, type, publicity, difficulty, correctAnswers, falseAnswers, owner);
		this.tips = tips;
	}
	
	
	
	/**
	 * Getter for tip
	 * @return  Question tip as String
	 */
	public String GetTips() {
		return tips;
	}
}
