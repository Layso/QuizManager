package com.layso.quizmanager.test;

import com.layso.quizmanager.services.*;

public class Main {
	public static void main(String[] args) {
		DatabaseTest();
		
	}
	
	
	
	public static void DatabaseTest() {
		DatabaseManager db = new DatabaseManager("jdbc:h2:~/test", "sa", "");
		System.out.println(db.Connect());
	}
}