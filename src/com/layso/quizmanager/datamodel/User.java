package com.layso.quizmanager.datamodel;

import com.layso.logger.datamodel.Logger;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;


public class User implements Searchable {
	public enum UserSearchTerms {Username, Authority}
	
	private int userID;
	private String username;
	private boolean sessionEnd;
	private boolean authoritative;
	
	
	
	public User (int userID, String username, boolean authoritative) {
		this.userID = userID;
		this.username = username;
		this.sessionEnd = false;
		this.authoritative = authoritative;
	}
	
	
	
	public int GetID() {
		return userID;
	}
	
	
	
	
	public String getUsername() {
		return username;
	}
	
	public boolean isAuthoritative() {
		return authoritative;
	}
	
	public void PrintUserMenu() {
	
	}
	
	public boolean IsRequestValid(String menuSelection) {
		return true;
	}
	
	public void ProcessUserRequest(String menuSelection) {
		if ("e".equals(menuSelection)) {
			sessionEnd = true;
		}
	}
	
	public boolean LoggedOut() {
		return sessionEnd;
	}
	
	public String getUsernameTable() {
		return username;
	}
	
	public String getAuthorityTable() {
		return Boolean.toString(authoritative);
	}
	
	public static List<PropertyValueFactory> GetPropertyValueFactory() {
		List<PropertyValueFactory> list = new ArrayList<>();
		
		list.add(new PropertyValueFactory<Quiz,String>("usernameTable"));
		list.add(new PropertyValueFactory<Quiz,String>("authorityTable"));
		
		return list;
	}
	
	
	@Override
	public boolean Search(String criteria, String term) {
		UserSearchTerms termEnum = UserSearchTerms.valueOf(term.replace(" ",""));
		criteria = criteria.toLowerCase();
		boolean result = false;
		
		
		switch (termEnum) {
			case Username: result = username.toLowerCase().contains(criteria); break;
			case Authority: result = Boolean.toString(authoritative).toLowerCase().contains(criteria); break;
		}
		
		return result;
	}
}
