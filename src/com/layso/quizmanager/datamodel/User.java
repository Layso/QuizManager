package com.layso.quizmanager.datamodel;

import com.layso.logger.datamodel.Logger;



public abstract class User {
	private int userID;
	private String username;
	private boolean isAuthoritative;
	
	
	private User (int userID, String username, boolean isAuthoritative) {
		this.userID = userID;
		this.username = username;
		this.isAuthoritative = isAuthoritative;
		
		Logger.Log("Authentication granted for user " + username + " as " + (isAuthoritative ? "teacher" : "student"), Logger.LogType.INFO);
	}
	
	
	public void PrintUserMenu() {
	
	}
	
	public boolean IsRequestValid(String menuSelection) {
		return false;
	}
	
	public void ProcessUserRequest() {
	
	}
}
