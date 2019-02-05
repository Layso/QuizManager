package com.layso.quizmanager.datamodel;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
	int id;
	int quizOwnerID;
	String quizTitle;
	List<Question> questions;
	int customDifficulty;
	double trueDifficulty;
	double averageDifficulty;
	boolean publicity;
	String name;
	
	
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
	
	public static List<PropertyValueFactory> GetPropertyValueFactory() {
		List<PropertyValueFactory> list = new ArrayList<>();
		
		list.add(new PropertyValueFactory<Quiz,String>("quizNameTable"));
		list.add(new PropertyValueFactory<Quiz,String>("questionCountTable"));
		list.add(new PropertyValueFactory<Quiz,String>("difficultyTable"));
		
		return list;
	}
	
	
	public boolean FilterQuizName(String criteria) {
		return false;
	}
	
	public boolean FilterQuizQuestionCount(String criteria) {
		return false;
	}
	
	public boolean FilterQuizDifficulty(String criteria) {
		return false;
	}
	
	public String getName() {
		return name;
	}
}
