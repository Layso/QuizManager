package com.layso.quizmanager.launcher;

import com.layso.quizmanager.services.DatabaseManager;
import com.layso.logger.datamodel.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class Main {
	static final int REQUORED_ARGUMENT_COUNT = 1;
	static final int REQUIRED_CONFIG_ELEMENT = 4;
	static final int CFG_DB_URL_INDEX = 0;
	static final int CFG_DB_USR_INDEX = 1;
	static final int CFG_DB_PASS_INDEX = 2;
	static final int CFG_LOG_FILENAME_INDEX = 3;
	
	
	
	public static void main(String[] args) {
		DatabaseManager db = null;
		
		
		if (!CheckArguments(args)) {
			System.out.println("Usage: ./progName [configFile]");
			System.out.println("[configFile] -> File that contains required data to run program");
		}
		
		else {
			// TODO Setup the logger
			db = InitializeDatabase(args);
		}
	}
	
	
	
	/**
	 * Reads the config file and creates an instance for DatabaseManager
	 * @param args  Arguments given to program
	 * @return      New DatabaseManager object
	 */
	public static DatabaseManager InitializeDatabase(String[] args) {
		DatabaseManager dbManager = null;
		List<String> elements = new ArrayList<>();
		
		
		try(Scanner scanner = new Scanner(new File(args[0]))) {
			while (scanner.hasNextLine()) {
				elements.add(scanner.nextLine());
			}
			
			dbManager = new DatabaseManager(elements.get(CFG_DB_URL_INDEX), elements.get(CFG_DB_USR_INDEX), elements.get(CFG_DB_PASS_INDEX));
		} catch (FileNotFoundException e) {
			System.out.println("Fatal Error: " + e.getMessage());
			System.exit(1);
		}
		
		return dbManager;
	}
	
	
	
	/**
	 * Checks if there is a valid filename given as a program argument
	 * @param args  Argument list given to program
	 * @return      Indicator flag for program to continue or not
	 */
	public static boolean CheckArguments(String[] args) {
		boolean result;
		
		
		if (result = (args.length == REQUORED_ARGUMENT_COUNT)) {
			try (Scanner scanner = new Scanner(new File(args[0]))) {
				int lineCount = 0;
				while (scanner.hasNextLine()) {
					scanner.nextLine();
					++lineCount;
				}
				
				result = (lineCount == REQUIRED_CONFIG_ELEMENT);
			} catch (FileNotFoundException e) {
				System.out.println("Error: " + e.getMessage());
				result = false;
			}
		}
		
		return result;
	}
}
