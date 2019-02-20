package com.layso.quizmanager.launcher;


import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.services.CfgManager;
import com.layso.quizmanager.services.DatabaseManager;
import com.layso.quizmanager.services.QuizManager;

public class Main {
	public static void main(String[] args) {
		// Check arguments and setup
		CheckArguments(args.length);
		SetupProgram(args, false, true);
		
		
		QuizManager.getInstance().Run();
	}
	
	
	
	public static void CheckArguments(int argc) {
		if (argc != 1) {
			System.out.println("Usage: ./progName [configFile]");
			System.out.println("[configFile] -> File that contains required data to run program");
			System.exit(-1);
		}
	}
	
	public static void SetupProgram(String[] args, boolean loggerTerminal, boolean loggerFile) {
		CfgManager.Setup(args[0]);
		Logger.Setup(CfgManager.getInstance().Get("log.filename"), loggerTerminal, loggerFile);
		DatabaseManager.Setup(CfgManager.getInstance().Get("db.url"), CfgManager.getInstance().Get("db.user"), CfgManager.getInstance().Get("db.pass"));
	}
}
