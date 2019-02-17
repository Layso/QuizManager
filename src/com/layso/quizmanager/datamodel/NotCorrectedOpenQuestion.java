package com.layso.quizmanager.datamodel;

import com.layso.quizmanager.services.DatabaseManager;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class NotCorrectedOpenQuestion implements Searchable {
	public enum NotCorrectedQuestionSearchTerms {Question, Answer, Answerer}
	
	int id;
	Quiz quiz;
	OpenQuestion question;
	String answer;
	User answerer;
	
	
	
	public NotCorrectedOpenQuestion(int id, Quiz quiz, OpenQuestion question, String answer, User answerer) {
		this.id = id;
		this.quiz = quiz;
		this.question = question;
		this.answer = answer;
		this.answerer = answerer;
	}
	
	public int GetID() {
		return id;
	}
	
	public Quiz GetQuiz() {
		return quiz;
	}
	
	public OpenQuestion GetQuestion() {
		return question;
	}
	
	public String GetAnswer() {
		return answer;
	}
	
	public User GetAnswerer() {
		return answerer;
	}
	
	
	public String getQuestion() {
		return question.GetQuestion();
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public String getAnswerer() {
		return answerer.getUsername();
	}
	
	public static List<PropertyValueFactory> GetPropertyValueFactory() {
		List<PropertyValueFactory> list = new ArrayList<>();
		
		list.add(new PropertyValueFactory<Quiz,String>("question"));
		list.add(new PropertyValueFactory<Quiz,String>("answer"));
		list.add(new PropertyValueFactory<Quiz,String>("answerer"));
		
		return list;
	}
	
	@Override
	public boolean Search(String criteria, String term) {
		NotCorrectedQuestionSearchTerms termEnum = NotCorrectedQuestionSearchTerms.valueOf(term.replace(" ",""));
		criteria = criteria.toLowerCase();
		boolean result = false;
		
		
		try {
			switch (termEnum) {
				case Question: result = getQuestion().toLowerCase().contains(criteria); break;
				case Answer: result = answer.toLowerCase().contains(criteria); break;
				case Answerer: result = answerer.getUsername().toLowerCase().contains(criteria); break;
			}
		} catch (NumberFormatException e) {
			result = false;
		}
		
		return result;
	}
}
