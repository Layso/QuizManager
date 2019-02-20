package com.layso.quizmanager.datamodel;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class AnswerTable implements Searchable {
	public enum AnswerTableSearchTerms {Name, QuestionCount, TrueAnswers, FalseAnswers, UncheckedAnswers, Percentage}
	
	Quiz quiz;
	User answerer;
	int notCorrectedAnswers;
	int trueAnswers;
	int falseAnswers;
	
	
	public AnswerTable(Quiz quiz, User answerer, int notCorrectedAnswers, int falseAnswers, int trueAnswers){
		this.quiz = quiz;
		this.answerer = answerer;
		this.trueAnswers = trueAnswers;
		this.falseAnswers = falseAnswers;
		this.notCorrectedAnswers = notCorrectedAnswers;
	}
	
	public int GetNotCorrectedAnswers() {
		return notCorrectedAnswers;
	}
	
	public int GetTrueAnswers() {
		return trueAnswers;
	}
	
	public int GetFalseAnswers() {
		return falseAnswers;
	}
	
	public Quiz GetQuiz() {
		return quiz;
	}
	
	public User GetAnswerer() {
		return answerer;
	}
	
	@Override
	public boolean Search(String criteria, String term) {
		AnswerTableSearchTerms termEnum = AnswerTableSearchTerms.valueOf(term.replace(" ",""));
		criteria = criteria.toLowerCase();
		boolean result = false;
		
		
		try {
			switch (termEnum) {
				case Name: result = quiz.GetQuizTitle().toLowerCase().contains(criteria); break;
				case TrueAnswers: result = trueAnswers >= Integer.parseInt(criteria); break;
				case FalseAnswers: result = falseAnswers >= Integer.parseInt(criteria); break;
				case QuestionCount: result = quiz.GetQuestions().size() >= Integer.parseInt(criteria); break;
				case UncheckedAnswers: result = notCorrectedAnswers >= Integer.parseInt(criteria); break;
				case Percentage: result = ((int) (((double) trueAnswers) / (falseAnswers + trueAnswers) * 100)) >= Integer.parseInt(criteria); break;
			}
		} catch (NumberFormatException e) {
			result = false;
		}
		
		return result;
	}
	
	public String getNameTable() {
		return quiz.GetQuizTitle();
	}
	
	public String getQuestionCountTable() {
		return Integer.toString(quiz.GetQuestions().size());
	}
	
	public String getTrueAnswers() {
		return Integer.toString(trueAnswers);
	}
	
	public String getFalseAnswers() {
		return Integer.toString(falseAnswers);
	}
	
	public String getUncheckedAnswers() {
		return Integer.toString(notCorrectedAnswers);
	}
	
	public String getPercentage() {
		return  "%" + ((int) (((double) trueAnswers) / (falseAnswers + trueAnswers) * 100));
	}
	
	public String getQuizSolver() {
		return answerer.getUsername();
	}
	
	public static List<PropertyValueFactory> GetPropertyValueFactory() {
		List<PropertyValueFactory> list = new ArrayList<>();
		
		list.add(new PropertyValueFactory<Quiz,String>("nameTable"));
		list.add(new PropertyValueFactory<Quiz,String>("questionCountTable"));
		list.add(new PropertyValueFactory<Quiz,String>("trueAnswers"));
		list.add(new PropertyValueFactory<Quiz,String>("falseAnswers"));
		list.add(new PropertyValueFactory<Quiz,String>("uncheckedAnswers"));
		list.add(new PropertyValueFactory<Quiz,String>("percentage"));
		list.add(new PropertyValueFactory<Quiz,String>("quizSolver"));
		return list;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(getNameTable());
		builder.append(" - ");
		builder.append(getQuestionCountTable());
		builder.append(" - ");
		builder.append(getTrueAnswers());
		builder.append(" - ");
		builder.append(getFalseAnswers());
		builder.append(" - ");
		builder.append(getUncheckedAnswers());
		builder.append(" - ");
		builder.append(getPercentage());
		
		return builder.toString();
	}
}
