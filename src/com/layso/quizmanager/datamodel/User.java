package com.layso.quizmanager.datamodel;

import com.layso.logger.datamodel.Logger;



public abstract class User {
	private int userID;
	private String username;
	private boolean sessionEnd;
	
	protected User (int userID, String username) {
		this.userID = userID;
		this.username = username;
		this.sessionEnd = false;
		
		Logger.Log("Login attempt successful for user: " + username, Logger.LogType.INFO);
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
	
	
	public boolean IsAuthoritative() {
		return false;
	}
}
