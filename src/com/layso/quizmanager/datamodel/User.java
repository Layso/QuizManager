package com.layso.quizmanager.datamodel;

import com.layso.logger.datamodel.Logger;



public abstract class User {
	private int userID;
	private String username;
	private boolean sessionEnd;
	private boolean authoritative;
	
	
	
	protected User (int userID, String username, boolean authoritative) {
		this.userID = userID;
		this.username = username;
		this.sessionEnd = false;
		this.authoritative = authoritative;
		
		Logger.Log("Login attempt successful for user: " + username, Logger.LogType.INFO);
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
}
