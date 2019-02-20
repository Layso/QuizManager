package com.layso.quizmanager.datamodel;



public interface AutoCorrectable {
	/**
	 * Method to automatically check the answer of a question
	 * @param answer    Answer given by User
	 * @return          Answer status as boolean
	 */
	boolean CheckAnswer(Answer answer);
}
