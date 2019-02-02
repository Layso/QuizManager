package com.layso.quizmanager.services;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.*;

import java.io.*;
import java.sql.*;
import java.util.List;


public class DatabaseManager {
	// One instance to rule them all, AKA singleton
	private static DatabaseManager instance;
	private Connection connection;
	
	
	
	/**
	 * A private constructor to prevent calls from different parts of code
	 * @param databaseURL       URL to the database for connection
	 * @param databaseUser      Username for database
	 * @param databasePassword  Password for database
	 */
	private DatabaseManager(String databaseURL, String databaseUser, String databasePassword) {
		try {
			connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
			Logger.Log("Database connection established", Logger.LogType.INFO);
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Can not connect to database", Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * An interface to force users instantiate singleton with parameters
	 * @param databaseURL       URL to the database for connection
	 * @param databaseUser      Username for database
	 * @param databasePassword  Password for database
	 */
	public static void Setup(String databaseURL, String databaseUser, String databasePassword) {
		if (instance == null)
			instance = new DatabaseManager(databaseURL, databaseUser, databasePassword);
	}
	
	
	
	/**
	 * Getter for singleton
	 * @return Returns the instance
	 */
	public static DatabaseManager getInstance() {
		if (instance == null){
			System.out.println("No instance created. Please call Setup() before getting instance\nTerminating program");
			System.exit(1);
		}
		
		return instance;
	}
	
	
	
	/**
	 * Predefined database insert method for quiz creation
	 * @param quiz Quiz to save to database
	 */
	public void CreateQuiz(Quiz quiz) {
		String sqlQuery = "insert into QUIZ(TITLE, QUESTION_COUNT, CUSTOM_DIFFICULTY, AVERAGE_DIFFICULTY, TRUE_DIFFICULTY, PUBLICITY) values(?, ?, ?, ?, ?, ?)";
		int quizID;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, quiz.GetTitle());
			statement.setString(2, Integer.toString(quiz.GetQuestions().size()));
			statement.setString(3, Integer.toString(quiz.GetCustomDifficulty()));
			statement.setString(4, Double.toString(quiz.GetAverageDifficulty()));
			statement.setString(5, Double.toString(quiz.GetTrueDifficulty()));
			statement.setString(6, Boolean.toString(quiz.GetPublicity()));
			statement.execute();
			
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			quizID = result.getInt("ID");
			Logger.Log("New quiz created successfully with ID: " + quizID, Logger.LogType.INFO);
			for (Question q : quiz.GetQuestions()) {
				AssociateQuizAndQuestion(SaveQuestion(q), quizID);
			}
			Logger.Log("All questions of quiz (ID: " + quizID + ") successfully inserted", Logger.LogType.INFO);
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to insert new quiz: " + quiz.GetTitle() + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	public File GetResourceByQuestionID(int questionID) {
		String sqlQuery = "select RESOURCE, SIZE from RESOURCE where QUESTION_ID = ?";
		File resource = new File("RESOURCE_" + questionID + ".rs");
		byte[] content;
		int size;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, Integer.toString(questionID));
			ResultSet results = statement.executeQuery();
			results.next();
			
			size = results.getInt("SIZE");
			content = new byte[size+1];
			
			InputStream is = results.getBinaryStream("RESOURCE");
			OutputStream os = new FileOutputStream(resource);
			
			is.read(content);
			os.write(content);
			
			is.close();
			os.close();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: SQL exception caught while trying to get resource for question ID: " + questionID, Logger.LogType.ERROR);
			System.exit(1);
		} catch (IOException e) {
			Logger.Log("Fatal Error: IO exception caught while trying to get resource for question ID: " + questionID, Logger.LogType.ERROR);
			System.exit(1);
		} catch (Exception e) {
			// TODO:
			// If this shows up, there is a potential error for image size
			System.out.println("IMAGE SIZE IS NOT EQUAL TO WHAT READ FROM DATABASE");
		}
		
		return resource;
	}
	
	
	/**
	 * Saves given question to database. First inserts the common Question attributes. Then calls question type specific
	 * methods to insert remaining parts to the database
	 * @param question  Question to insert
	 */
	private int SaveQuestion(Question question) {
		String sqlQuery = "insert into QUESTION(QUESTION, RESOURCE, TYPE, DIFFICULTY, CORRECT_ANSWERS, FALSE_ANSWERS, OWNER_ID) values(?, ?, ?, ?, ?, ?, ?)";
		int questionID = 0;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, question.GetQuestionText());
			statement.setString(2, Boolean.toString(!question.GetResource().equals("")));
			statement.setString(3, question.GetType().name());
			statement.setString(4, Integer.toString(question.GetDifficulty()));
			statement.setString(5, Integer.toString(question.GetCorrectAnswers()));
			statement.setString(6, Integer.toString(question.GetFalseAnswers()));
			statement.setString(7, Integer.toString(question.GetOwnerID()));
			statement.execute();
			
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			questionID = result.getInt("ID");
			switch (question.GetType()) {
				case MultipleChoice: SaveMultipleChoiceQuestion(((MultipleChoiceQuestion) question), questionID); break;
				case Associative: SaveAssociativeQuestion(((AssociativeQuestion) question), questionID); break;
				case Open: SaveOpenQuestion(((OpenQuestion) question), questionID); break;
			}
			
			SaveTopics(questionID, question.GetTopics());
			SaveResource(questionID, question.GetResource());
			Logger.Log("New question inserted successfully with ID: " + questionID, Logger.LogType.INFO);
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to insert new question: " + question.GetQuestionText(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return questionID;
	}
	
	
	
	/**
	 * Helper function to insert resource to database. If the path is not empty then file content will be inserted
	 * @param questionID    ID of the associated question for resource
	 * @param resource      Path of image file to save
	 */
	private void SaveResource(int questionID, String resource) {
		if (!resource.equals("")) {
			String sqlQuery = "insert into RESOURCE(QUESTION_ID, RESOURCE, SIZE) values(?, FILE_READ(?), ?)";
			long size = new File(resource).length();
			
			if (size > 0) {
				try {
					PreparedStatement statement = connection.prepareStatement(sqlQuery);
					statement.setString(1, Integer.toString(questionID));
					statement.setString(2, resource);
					statement.setString(3, Long.toString(size));
					statement.execute();
					Logger.Log("Resource inserted successfully for question ID: " + questionID, Logger.LogType.INFO);
				} catch (SQLException e) {
					Logger.Log("Fatal Error: Failed to insert resource for question ID: " + questionID, Logger.LogType.ERROR);
					System.exit(1);
				}
			}
		}
	}
	
	
	
	/**
	 * Helper function to insert topics to database
	 * @param questionID    ID of the associated question for topics
	 * @param topics        List that includes all the topics
	 */
	private void SaveTopics(int questionID, List<String> topics) {
		String sqlQuery = "insert into TOPIC(QUESTION_ID, TOPIC) values(?, ?)";
		
		
		try {
			for (String topic : topics) {
				PreparedStatement statement = connection.prepareStatement(sqlQuery);
				statement.setString(1, Integer.toString(questionID));
				statement.setString(2, topic);
				statement.execute();
				Logger.Log("Topics inserted successfully for question ID: " + questionID, Logger.LogType.INFO);
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to insert topics for question ID: " + questionID, Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Helper function to save choices of a multiple choice question
	 * @param question      Question to insert
	 * @param questionID    ID of question to associate answers with
	 */
	private void SaveMultipleChoiceQuestion(MultipleChoiceQuestion question, int questionID) {
		String sqlQuery = "insert into MCQ_ANSWERS(QUESTION_ID, FIRST_ANSWER, SECOND_ANSWER, THIRD_ANSWER, CORRECT_ANSWER) values(?, ?, ?, ?, ?)";
		List<String> answers = question.GetAnswers();
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, Integer.toString(questionID));
			statement.setString(5, question.GetCorrectAnswer());
			for (int i=2; i < answers.size()+2; ++i) {
				statement.setString(i, answers.get(i-2));
			}
			
			statement.execute();
			Logger.Log("All answers are insert for new multiple choice question ID: " + questionID, Logger.LogType.INFO);
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to insert new multiple choice question " + question.GetQuestionText(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Helper function to save left and right columns of an associative question
	 * @param question      Question to insert
	 * @param questionID    ID of question to associate columns with
	 */
	private void SaveAssociativeQuestion(AssociativeQuestion question, int questionID) {
		String sqlQuery = "insert into ASSOCIATIVE_CHOICES(QUESTION_ID, FIRST_CHOICE, SECOND_CHOICE) values(?, ?, ?)";
		List<String> leftSide = question.GetLeftColumn(), rightSide = question.GetRightColumn();
		
		
		try {
			for (int i=0; i<leftSide.size(); ++i) {
				PreparedStatement statement = connection.prepareStatement(sqlQuery);
				statement.setString(1, Integer.toString(questionID));
				statement.setString(2, leftSide.get(i));
				statement.setString(3, rightSide.get(i));
				statement.execute();
			}
			Logger.Log("All rows are insert for new associative question ID: " + questionID, Logger.LogType.INFO);
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to insert new associative question " + question.GetQuestionText(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Helper function to save tips of an open questions
	 * @param question      Question to insert
	 * @param questionID    ID of question to associate tips with
	 */
	private void SaveOpenQuestion(OpenQuestion question, int questionID) {
		String sqlQuery = "insert into OPEN_TIPS(QUESTION_ID, TIP) values(?, ?)";
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, Integer.toString(questionID));
			statement.setString(2, question.GetTips());
			statement.execute();
			Logger.Log("Tips are insert for new multiple choice question ID: " + questionID, Logger.LogType.INFO);
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to insert new open question " + question.GetQuestionText(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Helper function to associate question ID with quiz ID to finalize quiz creation
	 * @param questionID    ID of question to associate
	 * @param quizID        ID of quiz to associate with
	 */
	private void AssociateQuizAndQuestion(int questionID, int quizID) {
		String sqlQuery = "insert into QUIZ_QUESTION_ASSOCIATION(QUIZ_ID, QUESTION_ID) values(?, ?)";
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, Integer.toString(quizID));
			statement.setString(2, Integer.toString(questionID));
			statement.execute();
			Logger.Log("Question ID: " + questionID + " associated with quiz ID: " + quizID, Logger.LogType.INFO);
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to insert new question (ID: " + questionID + ") to associated quiz (ID: " + quizID + ")", Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Predefined database insert method for user register. Gets credentials and tries to insert to database
	 * @param username  Username for new user
	 * @param password  Password for new user
	 */
	public void UserRegister(String username, String password) {
		String sqlQuery = "insert into USER(USERNAME, PASSWORD, AUTHORITY) values(?, ?, ?)";
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, Boolean.toString(false));
			statement.execute();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to insert new user " + username, Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Predefined database search method for user login. Gets all user information from database and then
	 * checks if given credentials exist on the table
	 * @param username  Username to check
	 * @param password  Password to check
	 * @return          If doesn't exists returns null, else returns a User object according to authority level
	 */
	public User UserLogin(String username, String password) {
		String sqlQuery = "select ID, USERNAME, PASSWORD, AUTHORITY from USER";
		User user = null;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			ResultSet results = statement.executeQuery();
			
			while(results.next()) {
				if (results.getString("USERNAME").equals(username) && results.getString("PASSWORD").equals(password)) {
					int id = Integer.parseInt(results.getString("ID"));
					boolean auth = Boolean.valueOf(results.getString("AUTHORITY"));
					user =  auth ? new Teacher(id, username) : new Student(id, username);
				}
			}
			
		} catch (SQLException e) {
			Logger.Log("Fatal Error: SQL exception caught while trying user login", Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return user;
	}
	
	
	
	/**
	 * Checks if given username exists in database
	 * @param username  Username to check
	 * @return          True if username exists, else false
	 */
	public boolean UserExists(String username) {
		String sqlQuery = "select * from USER where USERNAME=(?)";
		boolean result = false;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, username);
			ResultSet results = statement.executeQuery();
			result = results.next();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: SQL exception caught while trying to search user", Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return result;
	}
	
	
	
	public boolean SchemaCheck() {
		String userTable = "CREATE TABLE USER(ID INT PRIMARY KEY auto_increment, USERNAMAE VARCHAR(255), PASSWORD VARCHAR(255), AUTHORITY BOOLEAN)";
		String questionTable = "CREATE TABLE QUESTION(ID INT PRIMARY KEY auto_increment, QUESTION VARCHAR(255), RESOURCE BOOLEAN, TYPE VARCHAR(255), DIFFICULTY INT, CORRECT_ANSWERS INT, FALSE_ANSWERS INT, OWNER_ID INT, foreign key (OWNER_ID) references USER(ID))";
		String resourceTable = "CREATE TABLE RESOURCE(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), RESOURCE BLOB, SIZE INT)";
		String topicTable = "CREATE TABLE TOPIC(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), TOPIC VARCHAR(255))";
		String mcqChoicesTable = "CREATE TABLE MCQ_CHOICES(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), FIRST_ANSWER VARCHAR(255), SECOND_ANSWER VARCHAR(255), THIRD_ANSWER VARCHAR(255), CORRECT_ANSWER VARCHAR(255))";
		String associativeChoicesTable = "CREATE TABLE ASSOCIATIVE_CHOICES(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), FIRST_CHOICE VARCHAR(255), SECOND_CHOICE VARCHAR(255))";
		String openTipsTable = "CREATE TABLE OPEN_TIPS(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), TIP VARCHAR(255))";
		String quizTable = "CREATE TABLE QUIZ(ID INT PRIMARY KEY auto_increment, TITLE VARCHAR(255), QUESTION_COUNT INT, CUSTOM_DIFFICULTY INT, AVERAGE_DIFFICULTY DOUBLE, TRUE_DIFFICULTY DOUBLE, PUBLICITY BOOLEAN)";
		String quizQuestionAsssociationTable = "CREATE TABLE QUIZ_QUESTION_ASSOCIATION(ID INT PRIMARY KEY auto_increment, QUIZ_ID INT, foreign key (QUIZ_ID) references QUIZ(ID), QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID))";
		
		return false;
	}
}
