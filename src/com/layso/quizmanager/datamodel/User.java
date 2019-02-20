package com.layso.quizmanager.datamodel;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;



public class User implements Searchable {
	public enum UserSearchTerms {Username, Authority}
	
	private int userID;
	private String username;
	private boolean authoritative;
	
	
	
	/**
	 * Constructor to set variables on initialize
	 * @param userID        ID of the user
	 * @param username      Visible username of the user
	 * @param authoritative Authority status of the user
	 */
	public User (int userID, String username, boolean authoritative) {
		this.userID = userID;
		this.username = username;
		this.authoritative = authoritative;
	}
	
	
	
	/**
	 * A getter for property list to associate User on a TableView
	 * @return  List of PropertyValueFactory for member fields to show on table
	 */
	public static List<PropertyValueFactory> GetPropertyValueFactory() {
		List<PropertyValueFactory> list = new ArrayList<>();
		
		list.add(new PropertyValueFactory<Quiz,String>("usernameTable"));
		list.add(new PropertyValueFactory<Quiz,String>("authorityTable"));
		
		return list;
	}
	
	
	
	/**
	 * Search method implementation for Searchable interface to be able to compare with given criteria of given term
	 * @param criteria  Criteria to look if User provides
	 * @param term      Term to search the given criteria
	 * @return          Boolean result, true if the criteria is provided, else false
	 */
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
	
	
	
	/**
	 * Overriding toString method to let User be able to printed to screen more understandable for console application
	 * @return Table view of User as String
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		
		builder.append(getUsernameTable());
		builder.append(" - ");
		builder.append(getAuthorityTable());
		
		return builder.toString();
	}
	
	
	
	/**
	 * Returns the titles of columns for console table view
	 * @return  Column titles for console
	 */
	public static String ConsoleTableTitle() {
		return "Username - Authority";
	}
	
	
	
	/**
	 * Getter for user ID
	 * @return  ID of the user
	 */
	public int GetID() {
		return userID;
	}
	
	/**
	 * Getter for the username
	 * @return  Username of the user
	 */
	public String GetUsername() {
		return username;
	}
	
	/**
	 * Getter for authority. If a user is authoritative it can create, edit, evaluate quizzes. If not it only can
	 * solve the quizzes
	 * @return  Authority of user
	 */
	public boolean isAuthoritative() {
		return authoritative;
	}
	
	
	
	/**
	 * PropertyValueFactory getter for username
	 * @return  Username as String
	 */
	public String getUsernameTable() {
		return username;
	}
	
	/**
	 * PropertyValueFactory getter for authority
	 * @return  Authority level as String
	 */
	public String getAuthorityTable() {
		return Boolean.toString(authoritative);
	}
}
