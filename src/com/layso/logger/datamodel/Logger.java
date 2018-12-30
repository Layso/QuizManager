package com.layso.logger.datamodel;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Logger {
	public enum LogType {INFO, WARNING, ERROR}
	private static boolean setupComplete = false;
	private static boolean logToFile;
	private static boolean logToTerminal;
	private static String logFileName;
	
	
	
	// Declaring constructor as private in order to prevent object creation from this class
	private Logger() {
		// Intentionally left blank
	}
	
	
	
	/**
	 * Sets the member variables to be used later on log commands
	 * @param fileName  Name of the file to be used as log file
	 * @param terminal  Option to print log message to the terminal
	 * @param file      Option to print log message to the file
	 */
	public static void Setup(String fileName, boolean terminal, boolean file) {
		logFileName = fileName;
		logToFile = terminal;
		logToTerminal = file;
		
		if (!setupComplete) {
			setupComplete = true;
			Log("", LogType.INFO);
			Log("NEW SESSION STARTED", LogType.INFO);
		}
		
		Log("Logger configurations changed: [fileName : " + fileName + "]  -  [logTerminal : " + terminal + "]  -  [logFile : " + file + "]", LogType.INFO);
	}
	
	
	
	/**
	 * Interface for user to log a message
	 * @param message   Message to log
	 * @param type      Log type to indicate on log message
	 * @return          True if setup done before and message logged, else false
	 */
	public static boolean Log(String message, LogType type) {
		boolean result = false;
		
		
		if (setupComplete) {
			result = true;
			
			if (logToFile)
				LogToFile(message, type);
			if (logToTerminal)
				LogToTerminal(message, type);
		}
		
		return result;
	}
	
	
	
	/**
	 * Logs given message to terminal
	 * @param message   Message to log
	 * @param type      Log type to indicate on log message
	 */
	private static void LogToFile(String message, LogType type) {
		try(FileWriter writer = new FileWriter(logFileName, true)) {
			writer.append(BuildMessage(message,type));
		} catch (IOException e) {
			// TODO Find a porper error handling way
		}
	}
	
	
	
	/**
	 * Logs given message to terminal
	 * @param message   Message to log
	 * @param type      Log type to indicate on log message
	 */
	private static void LogToTerminal(String message, LogType type) {
		System.out.print(BuildMessage(message, type));
	}
	
	
	
	/**
	 * Formats and tidies the message to print to file and terminal
	 * @param message   Message to log
	 * @param type      Log type to indicate on log message
	 * @return          String that is build from date, log type and message
	 */
	private static String BuildMessage(String message, LogType type) {
		StringBuilder builder = new StringBuilder();
		String format = "[yyyy/MM/dd] [HH:mm:ss,SS]";
		
		
		// Including the date with specified format
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		builder.append(dateFormat.format(new Date()));
		
		// Including the message type
		builder.append(" [");
		builder.append(type.name());
		builder.append("]");
		
		// Including the message
		builder.append(" --- ");
		builder.append(message);
		builder.append("\n");
		
		return builder.toString();
	}
}
