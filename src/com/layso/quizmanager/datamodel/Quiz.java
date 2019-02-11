package com.layso.quizmanager.datamodel;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class Quiz implements Searchable {
	public enum QuizSearchTerms {Name, QuestionCount, Difficulty, TrueDifficulty}
	
	int id;
	int quizOwnerID;
	String quizTitle;
	List<Question> questions;
	int customDifficulty;
	double trueDifficulty;
	double averageDifficulty;
	boolean publicity;
	
	
	public Quiz(int id, int quizOwnerID, String quizTitle, List<Question> questions, int customDifficulty, double trueDifficulty, double averageDifficulty, boolean publicity) {
		this.id = id;
		this.quizOwnerID = quizOwnerID;
		this.quizTitle = quizTitle;
		this.questions = questions;
		this.customDifficulty = customDifficulty;
		this.trueDifficulty = trueDifficulty;
		this.averageDifficulty = averageDifficulty;
		this.publicity = publicity;
	}
	
	public int GetQuizID() {
		return id;
	}
	
	public String GetQuizTitle() {
		return quizTitle;
	}
	
	public int GetCustomDifficulty() {
		return customDifficulty;
	}
	
	public double GetAverageDifficulty() {
		return averageDifficulty;
	}
	
	public double GetTrueDifficulty() {
		return trueDifficulty;
	}
	
	public boolean GetPublicity() {
		return publicity;
	}
	
	public List<Question> GetQuestions() {
		return questions;
	}
	
	public String getQuizNameTable() {
		return quizTitle;
	}
	
	public String getQuestionCountTable() {
		return Integer.toString(questions.size());
	}
	
	public String getDifficultyTable() {
		return customDifficulty == -1 ? Double.toString(averageDifficulty) : Integer.toString(customDifficulty);
	}
	
	public String getTrueDifficultyTable() {
		return Double.toString(trueDifficulty);
	}
	
	public static List<PropertyValueFactory> GetPropertyValueFactory() {
		List<PropertyValueFactory> list = new ArrayList<>();
		
		list.add(new PropertyValueFactory<Quiz,String>("quizNameTable"));
		list.add(new PropertyValueFactory<Quiz,String>("questionCountTable"));
		list.add(new PropertyValueFactory<Quiz,String>("difficultyTable"));
		list.add(new PropertyValueFactory<Quiz,String>("trueDifficultyTable"));
		
		return list;
	}
	
	
	@Override
	public boolean Search(String criteria, String term) {
		// TODO: remove whitespace
		QuizSearchTerms termEnum = QuizSearchTerms.valueOf(term.replace(" ",""));
		criteria = criteria.toLowerCase();
		boolean result = false;
		
		
		if (criteria.equals("")) {
			result = true;
		}
		
		else {
			try {
				switch (termEnum) {
					case Name: result = quizTitle.toLowerCase().contains(criteria); break;
					case Difficulty: result = (customDifficulty == -1 ? averageDifficulty : customDifficulty) >= Integer.parseInt(criteria); break;
					case QuestionCount: result = questions.size() >= Integer.parseInt(criteria); break;
					case TrueDifficulty: result = trueDifficulty >= Integer.parseInt(criteria); break;
				}
			} catch (NumberFormatException e) {
				result = false;
			}
		}
		
		return result;
	}
}
