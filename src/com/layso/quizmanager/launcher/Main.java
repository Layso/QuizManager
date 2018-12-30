package com.layso.quizmanager.launcher;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.Manager;
import com.layso.quizmanager.services.DatabaseManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class Main {
	static final int REQUIRED_ARGUMENT_COUNT = 1;
	static final int REQUIRED_CONFIG_ELEMENT = 4;
	static final int CFG_DB_URL_INDEX = 0;
	static final int CFG_DB_USR_INDEX = 1;
	static final int CFG_DB_PASS_INDEX = 2;
	static final int CFG_LOG_FILENAME_INDEX = 3;
	
	
	
	public static void main(String[] args) {
		List<String> configElements;
		DatabaseManager dbManager;
		
		if (!CheckArguments(args)) {
			System.out.println("Usage: ./progName [configFile]");
			System.out.println("[configFile] -> File that contains required data to run program");
		}
		
		else {
			configElements = ReadConfigFile(args);
			Logger.Setup(configElements.get(CFG_LOG_FILENAME_INDEX), true, true);
			dbManager = new DatabaseManager();
			dbManager.Connect(configElements.get(CFG_DB_URL_INDEX), configElements.get(CFG_DB_USR_INDEX), configElements.get(CFG_DB_PASS_INDEX));
			Manager manager = new Manager();
			
			Logger.Log("Object initializations are done", Logger.LogType.INFO);
			manager.Run();
			Logger.Log("Program shutting down", Logger.LogType.INFO);
		}
	}
	
	
	
	
	/**
	 * Reads the config file and creates an array of config elements
	 * @param args  Arguments given to program
	 * @return      List of config file elements
	 */
	public static List<String> ReadConfigFile(String[] args) {
		List <String> elements = new ArrayList<>();
		
		
		try(Scanner scanner = new Scanner(new File(args[0]))) {
			while (scanner.hasNextLine()) {
				elements.add(scanner.nextLine());
			}
			
			if (elements.size() != REQUIRED_CONFIG_ELEMENT) {
				throw new IndexOutOfBoundsException("There isn't enough configuration options for given file: " + args[0]);
			}
		} catch (Exception e) {
			System.out.println("Fatal Error: " + e.getMessage());
			System.exit(1);
		}
		
		return elements;
	}
	
	
	
	/**
	 * Checks if there is a valid filename given as a program argument
	 * @param args  Argument list given to program
	 * @return      Indicator flag for program to continue or not
	 */
	public static boolean CheckArguments(String[] args) {
		boolean result;
		
		
		if (result = (args.length == REQUIRED_ARGUMENT_COUNT)) {
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
