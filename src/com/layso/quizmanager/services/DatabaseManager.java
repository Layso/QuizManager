package com.layso.quizmanager.services;

import com.layso.logger.datamodel.Logger;
import com.layso.quizmanager.datamodel.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
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
	
	
	
	
	
	
	public List<Quiz> GetAllPublicQuizzes() {
		// TODO
		// Implement function to include un-owned public quizzes on returned list
		return GetOwningQuizzes();
	}
	
	
	
	public void ChangeQuestion(Question oldQuestion, Question newQuestion) {
		String sqlQuery = "select QUIZ_ID from QUIZ_QUESTION_ASSOCIATION where QUESTION_ID = ?";
		int newID = SaveQuestion(newQuestion);
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, oldQuestion.GetID());
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				ChangeQuizQuestion(oldQuestion.GetID(), newID, results.getInt("QUIZ_ID"));
			}
			
			DeleteQuestionByID(oldQuestion.GetID());
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to update question ID from " + oldQuestion.GetID() + " to " + newID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	public void ChangeQuizQuestion(int oldID, int newID, int quizID) {
		String sqlQuery = "update QUIZ_QUESTION_ASSOCIATION set QUESTION_ID = ? where QUESTION_ID = ? and QUIZ_ID = ?";
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, newID);
			statement.setInt(2, oldID);
			statement.setInt(3, quizID);
			statement.execute();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to update question ID from " + oldID + " to " + newID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	public void DeleteQuestionByID(int questionID) {
		String[] queries = {"delete from RESOURCE where QUESTION_ID = ?",
							"delete from TOPIC where QUESTION_ID = ?",
							"delete from QUESTION where ID = ?",
							"delete from MCQ_ANSWERS where QUESTION_ID = ?",
							"delete from ASSOCIATIVE_CHOICES where QUESTION_ID = ?",
							"delete from OPEN_TIPS where QUESTION_ID = ?"};
		
		String sqlQueryResource = "delete from RESOURCE where QUESTION_ID = ?";
		String sqlQueryTopic = "delete from TOPIC where QUESTION_ID = ?";
		String sqlQueryQuestion = "delete from QUESTION where ID = ?";
		String sqlQueryMcqAnswers = "delete from MCQ_ANSWERS where QUESTION_ID = ?";
		String sqlQueryAssociative = "delete from ASSOCIATIVE_CHOICES where QUESTION_ID = ?";
		String sqlQueryOpenTips = "delete from OPEN_TIPS where QUESTION_ID = ?";
		
		
		try {
			PreparedStatement statement1 = connection.prepareStatement(sqlQueryResource);
			statement1.setInt(1, questionID);
			statement1.execute();
			
			PreparedStatement statement2 = connection.prepareStatement(sqlQueryTopic);
			statement2.setInt(1, questionID);
			statement2.execute();
			
			PreparedStatement statement3 = connection.prepareStatement(sqlQueryQuestion);
			statement3.setInt(1, questionID);
			statement3.execute();
			
			PreparedStatement statement4 = connection.prepareStatement(sqlQueryMcqAnswers);
			statement4.setInt(1, questionID);
			statement4.execute();
			
			PreparedStatement statement5 = connection.prepareStatement(sqlQueryAssociative);
			statement5.setInt(1, questionID);
			statement5.execute();
			
			PreparedStatement statement6 = connection.prepareStatement(sqlQueryOpenTips);
			statement6.setInt(1, questionID);
			statement6.execute();
			
			
			SafeQuestionDeletion(questionID);
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to remove question ID: " + questionID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	private void SafeQuestionDeletion(int questionID) {
		String sqlQuery = "select QUIZ_ID from QUIZ_QUESTION_ASSOCIATION where QUESTION_ID = ?";
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, questionID);
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				DeleteQuestionFromQuiz(results.getInt("QUIZ_ID"), questionID);
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to safely delete question ID: " + questionID + " from quiz: " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	public void DeleteQuestionFromQuiz(int quizID, int questionID) {
		String sqlDeleteQuery = "delete from QUIZ_QUESTION_ASSOCIATION where QUIZ_ID = ? and QUESTION_ID = ?";
		String sqlUpdateQuery = "update QUIZ set QUESTION_COUNT = ? where ID = ?";
		String sqlCheckQuery = "select QUESTION_COUNT from QUIZ where ID = ?";
		
		System.out.println("Delete " + questionID + " from " + quizID);
		try {
			PreparedStatement deleteStatement = connection.prepareStatement(sqlDeleteQuery);
			deleteStatement.setInt(1, quizID);
			deleteStatement.setInt(2, questionID);
			deleteStatement.execute();
			
			PreparedStatement checkStatement = connection.prepareStatement(sqlCheckQuery);
			checkStatement.setInt(1, quizID);
			ResultSet results = checkStatement.executeQuery();
			
			if (results.next()) {
				int questionCount = results.getInt("QUESTION_COUNT");
				if (questionCount == 1) {
					DeleteQuizByID(quizID);
				}
				
				else {
					PreparedStatement updateStatement = connection.prepareStatement(sqlUpdateQuery);
					updateStatement.setInt(1, questionCount - 1);
					updateStatement.setInt(2, quizID);
					updateStatement.execute();
				}
			}
			
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to remove question ID: " + questionID + " from quiz ID: " + quizID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	public void DeleteQuizByID(int quizID) {
		String sqlQuery = "delete from QUIZ where ID = ?";
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, quizID);
			statement.execute();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to remove quiz ID: " + quizID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	public List<Quiz> GetOwningQuizzes() {
		String sqlQuery = "select ID from QUIZ where OWNER_ID = ?";
		List<Quiz> quizzes = new ArrayList<>();
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, QuizManager.getInstance().GetUser().GetID());
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				quizzes.add(GetQuizByID(results.getInt("ID")));
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get quizzes owned by current user: " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return quizzes;
	}
	
	
	
	public Quiz GetQuizByID(int quizID) {
		String sqlQuery = "select TITLE, QUESTION_COUNT, CUSTOM_DIFFICULTY, AVERAGE_DIFFICULTY, TRUE_DIFFICULTY, OWNER_ID, PUBLICITY from QUIZ where ID = ?";
		Quiz quiz = null;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, quizID);
			ResultSet results = statement.executeQuery();
			
			if (results.next()) {
				quiz = new Quiz(quizID, results.getInt("OWNER_ID"), results.getString("TITLE"),
					GetQuestionsByQuizID(quizID), results.getInt("CUSTOM_DIFFICULTY"),
					results.getDouble("TRUE_DIFFICULTY"), results.getDouble("AVERAGE_DIFFICULTY"),
					results.getBoolean("PUBLICITY"));
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get quiz ID: " + quizID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return quiz;
	}
	
	
	
	public List<Question> GetQuestionsByQuizID(int quizID) {
		String sqlQuery = "select QUESTION_ID from QUIZ_QUESTION_ASSOCIATION where QUIZ_ID = ?";
		List<Question> questions = new ArrayList<>();
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, quizID);
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				questions.add(GetQuestionByID(results.getInt("QUESTION_ID")));
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get questions on quiz ID: " + quizID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return questions;
	}
	
	
	
	/**
	 * Method to get username by user ID
	 * @param userID    Id of the user
	 * @return          Username of the user as string
	 */
	public String GetUsernameByID(int userID) {
		String sqlQuery = "select USERNAME from USER where ID = ?";
		String username = null;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, Integer.toString(userID));
			ResultSet results = statement.executeQuery();
			
			if (results.next()) {
				username = results.getString("USERNAME");
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get user ID: " + userID + ": "+ e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return username;
	}
	
	
	
	/**
	 * Method to get all public questions saved on database. Public questions contains the private owned questions too
	 * @return  List of questions that are either public or owned by currently logged in user
	 */
	public List<Question> GetAllPublicQuestions() {
		String sqlQuery = "select ID from QUESTION where PUBLICITY = TRUE or OWNER_ID = ?";
		List<Question> questions = new ArrayList<>();
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, Integer.toString(QuizManager.getInstance().GetUser().GetID()));
			ResultSet results = statement.executeQuery();
			
			while(results.next()) {
				int id = results.getInt("ID");
				questions.add(GetQuestionByID(id));
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get public questions: " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		
		
		return questions;
	}
	
	
	
	/**
	 * Method to get the Question object with question ID
	 * @param questionID    Id of the question to retrieve
	 * @return              Question that matches the ID
	 */
	public Question GetQuestionByID(int questionID) {
		String sqlQuery = "select QUESTION, RESOURCE, TYPE, DIFFICULTY, PUBLICITY, CORRECT_ANSWERS, FALSE_ANSWERS, OWNER_ID from QUESTION where ID = ?";
		Question question = null;
		boolean questionPublicity;
		List<String> questionTopics;
		String questionText, resourcePath;
		Question.QuestionType questionType;
		int questionDifficulty, questionCorrectAnswers, questionFalseAnswers, questionOwnerID;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, Integer.toString(questionID));
			ResultSet results = statement.executeQuery();
			
			results.next();
			questionText = results.getString("QUESTION");
			resourcePath = Boolean.valueOf(results.getString("RESOURCE")) ? GetResourceNameByQuestionID(questionID) : "";
			questionType = Question.QuestionType.valueOf(results.getString("TYPE"));
			questionDifficulty = results.getInt("DIFFICULTY");
			questionCorrectAnswers = results.getInt("CORRECT_ANSWERS");
			questionFalseAnswers = results.getInt("FALSE_ANSWERS");
			questionOwnerID = results.getInt("OWNER_ID");
			questionPublicity = results.getBoolean("PUBLICITY");
			questionTopics = GetTopicsByQuestionID(questionID);
			if (results.getBoolean("RESOURCE"))
				GetResourceByQuestionID(questionID);
			
			switch (questionType) {
				case MultipleChoice: question = GetMultipleChoiceQuestionByID(questionID, questionText, questionTopics, resourcePath, questionType, questionPublicity, questionDifficulty, questionCorrectAnswers, questionFalseAnswers, questionOwnerID); break;
				case Associative: question = GetAssociativeQuestionByID(questionID, questionText, questionTopics, resourcePath, questionType, questionPublicity, questionDifficulty, questionCorrectAnswers, questionFalseAnswers, questionOwnerID); break;
				case Open: question = GetOpenQuestionByID(questionID, questionText, questionTopics, resourcePath, questionType, questionPublicity, questionDifficulty, questionCorrectAnswers, questionFalseAnswers, questionOwnerID); break;
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get questions with ID: " + questionID + " " + e.getMessage(),
				Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return question;
	}
	
	
	
	/**
	 * Helper method to create a multiple choice question
	 * @param questionID        Id of question
	 * @param text              Question text
	 * @param topics            Topics of the question as string list
	 * @param path              Path of the resource, if exists
	 * @param type              Type of the question
	 * @param publicity         Public or private value of question
	 * @param difficulty        Difficulty value of the question
	 * @param correctAnswers    Count of correct answers given to the question
	 * @param falseAnswers      Count of false answers given to the question
	 * @param ownerID           Id of the user who created the question
	 * @return                  Returns the MultipleChoiceQuestion object which matches the given question ID
	 */
	private MultipleChoiceQuestion GetMultipleChoiceQuestionByID(int questionID, String text, List<String> topics,
	                                                             String path, Question.QuestionType type,
	                                                             boolean publicity , int difficulty, int correctAnswers,
	                                                             int falseAnswers, int ownerID) {
		String sqlQuery = "select FIRST_ANSWER, SECOND_ANSWER, THIRD_ANSWER, CORRECT_ANSWER from MCQ_ANSWERS where QUESTION_ID = ?";
		MultipleChoiceQuestion question = null;
		List<String> otherAnswers = new ArrayList<>();
		String correctAnswer;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, Integer.toString(questionID));
			ResultSet results = statement.executeQuery();
			
			if (results.next()) {
				correctAnswer = results.getString("CORRECT_ANSWER");
				otherAnswers.add(results.getString("FIRST_ANSWER"));
				otherAnswers.add(results.getString("SECOND_ANSWER"));
				otherAnswers.add(results.getString("THIRD_ANSWER"));
				question = new MultipleChoiceQuestion(questionID, text, topics, path, type, publicity, difficulty,
					correctAnswers, falseAnswers, GetUserByID(ownerID), correctAnswer, otherAnswers);
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get multiple choice questions with ID: " + questionID + " " +
					e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		
		return question;
	}
	
	
	
	/**
	 * Helper method to create an associative question
	 * @param questionID        Id of question
	 * @param text              Question text
	 * @param topics            Topics of the question as string list
	 * @param path              Path of the resource, if exists
	 * @param type              Type of the question
	 * @param publicity         Public or private value of question
	 * @param difficulty        Difficulty value of the question
	 * @param correctAnswers    Count of correct answers given to the question
	 * @param falseAnswers      Count of false answers given to the question
	 * @param ownerID           Id of the user who created the question
	 * @return                  Returns the AssociativeQuestion object which matches the given question ID
	 */
	private AssociativeQuestion GetAssociativeQuestionByID(int questionID, String text, List<String> topics,
	                                                       String path, Question.QuestionType type, boolean publicity,
	                                                       int difficulty, int correctAnswers, int falseAnswers,
	                                                       int ownerID) {
		String sqlQuery = "select FIRST_CHOICE, SECOND_CHOICE from ASSOCIATIVE_CHOICES where QUESTION_ID = ?";
		AssociativeQuestion question = null;
		List<String> leftColumn = new ArrayList<>(), rightColumn = new ArrayList<>();
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, Integer.toString(questionID));
			ResultSet results =  statement.executeQuery();
			
			while (results.next()) {
				leftColumn.add(results.getString("FIRST_CHOICE"));
				rightColumn.add(results.getString("SECOND_CHOICE"));
			}
			question = new AssociativeQuestion(questionID, text, topics, path, type, publicity, difficulty,
				correctAnswers, falseAnswers, GetUserByID(ownerID), leftColumn, rightColumn);
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get associative questions with ID: " + questionID + " " +
				e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		
		return question;
	}
	
	
	
	/**
	 * Helper method to create an open question
	 * @param questionID        Id of question
	 * @param text              Question text
	 * @param topics            Topics of the question as string list
	 * @param path              Path of the resource, if exists
	 * @param type              Type of the question
	 * @param publicity         Public or private value of question
	 * @param difficulty        Difficulty value of the question
	 * @param correctAnswers    Count of correct answers given to the question
	 * @param falseAnswers      Count of false answers given to the question
	 * @param ownerID           Id of the user who created the question
	 * @return                  Returns the OpenQuestion object which matches the given question ID
	 */
	private OpenQuestion GetOpenQuestionByID(int questionID, String text, List<String> topics, String path,
	                                         Question.QuestionType type, boolean publicity, int difficulty,
	                                         int correctAnswers, int falseAnswers, int ownerID) {
		String sqlQuery = "select TIP from OPEN_TIPS where QUESTION_ID = ?";
		OpenQuestion question = null;
		String tip;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, Integer.toString(questionID));
			ResultSet results =  statement.executeQuery();
			
			if (results.next()) {
				tip = results.getString("TIP");
				question = new OpenQuestion(questionID, text, topics, path, type, publicity, difficulty, correctAnswers,
					falseAnswers, GetUserByID(ownerID), tip);
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get open questions with ID: " + questionID + " " +
				e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		
		return question;
	}
	
	
	
	/**
	 * Helper method to retrieve topics for a question
	 * @param questionID    ID of the question
	 * @return              Topics as string list for the question matches with question ID
	 */
	private List<String> GetTopicsByQuestionID(int questionID) {
		String sqlQuery = "select TOPIC from TOPIC where QUESTION_ID = ?";
		List<String> topics = new ArrayList<>();
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, Integer.toString(questionID));
			ResultSet results =  statement.executeQuery();
			
			while (results.next()) {
				topics.add(results.getString("TOPIC"));
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get open questions with ID: " + questionID + " " +
				e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return topics;
	}
	
	
	
	/**
	 * Helper method to retrieve the resource for a question
	 * @param questionID    ID of the question
	 */
	public void GetResourceByQuestionID(int questionID) {
		String sqlQuery = "select RESOURCE, SIZE from RESOURCE where QUESTION_ID = ?";
		File resource = new File(GetResourceNameByQuestionID(questionID));
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
	}
	
	
	/**
	 * Predefined database insert method for quiz creation
	 * @param quiz Quiz to save to database
	 */
	public void CreateQuiz(Quiz quiz) {
		String sqlQuery = "insert into QUIZ(TITLE, QUESTION_COUNT, CUSTOM_DIFFICULTY, AVERAGE_DIFFICULTY, TRUE_DIFFICULTY, OWNER_ID, PUBLICITY) values(?, ?, ?, ?, ?, ?, ?)";
		int quizID;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, quiz.GetQuizTitle());
			statement.setString(2, Integer.toString(quiz.GetQuestions().size()));
			statement.setString(3, Integer.toString(quiz.GetCustomDifficulty()));
			statement.setString(4, Double.toString(quiz.GetAverageDifficulty()));
			statement.setString(5, Double.toString(quiz.GetTrueDifficulty()));
			statement.setInt(6, QuizManager.getInstance().GetUser().GetID());
			statement.setString(7,  Boolean.toString(quiz.GetPublicity()));
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
			Logger.Log("Fatal Error: Failed to insert new quiz: " + quiz.GetQuizTitle() + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Method to get resource file name of a question
	 * @param questionID    ID of the question
	 * @return              File name that contains the resource for matching question ID
	 */
	public String GetResourceNameByQuestionID(int questionID) {
		return "RESOURCE_" + questionID + ".rs";
	}
	
	
	
	/**
	 * Saves given question to database. First inserts the common Question attributes. Then calls question type specific
	 * methods to insert remaining parts to the database
	 * @param question  Question to insert
	 */
	public int SaveQuestion(Question question) {
		String sqlQuery = "insert into QUESTION(QUESTION, RESOURCE, TYPE, PUBLICITY, DIFFICULTY, CORRECT_ANSWERS, FALSE_ANSWERS, OWNER_ID) values(?, ?, ?, ?, ?, ?, ?, ?)";
		int questionID = 0;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, question.GetQuestion());
			statement.setString(2, Boolean.toString(!question.GetResource().equals("")));
			statement.setString(3, question.GetType().name());
			statement.setString(4, Boolean.toString(question.GetPublicity()));
			statement.setString(5, Integer.toString(question.GetDifficulty()));
			statement.setString(6, Integer.toString(question.GetCorrectAnswers()));
			statement.setString(7, Integer.toString(question.GetFalseAnswers()));
			statement.setString(8, Integer.toString(question.GetOwner().GetID()));
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
			Logger.Log("Fatal Error: Failed to insert new question: " + question.GetQuestion(), Logger.LogType.ERROR);
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
			Logger.Log("Fatal Error: Failed to insert new multiple choice question " + question.GetQuestion() +
				": " + e.getMessage(), Logger.LogType.ERROR);
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
			Logger.Log("Fatal Error: Failed to insert new associative question " + question.GetQuestion(), Logger.LogType.ERROR);
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
			Logger.Log("Fatal Error: Failed to insert new open question " + question.GetQuestion(), Logger.LogType.ERROR);
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
	 * Method to get the User by its ID
	 * @param userID    ID of the user
	 * @return          User object (Student or Teacher) according to authority
	 */
	public User GetUserByID(int userID) {
		String sqlQuery = "select USERNAME, AUTHORITY from USER where ID = ?";
		User user = null;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, userID);
			ResultSet results = statement.executeQuery();
			
			if (results.next()) {
				String username = results.getString("USERNAME");
				boolean authority = results.getBoolean("AUTHORITY");
				user = authority ? new Teacher(userID, username) : new Student(userID, username) ;
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Couldn't get the user with ID: " + userID, Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return user;
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
		String questionTable = "CREATE TABLE QUESTION(ID INT PRIMARY KEY auto_increment, QUESTION VARCHAR(255), RESOURCE BOOLEAN, TYPE VARCHAR(255), PUBLICITY BOOLEAN, DIFFICULTY INT, CORRECT_ANSWERS INT, FALSE_ANSWERS INT, OWNER_ID INT, foreign key (OWNER_ID) references USER(ID))";
		String resourceTable = "CREATE TABLE RESOURCE(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), RESOURCE BLOB, SIZE INT)";
		String topicTable = "CREATE TABLE TOPIC(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), TOPIC VARCHAR(255))";
		String mcqChoicesTable = "CREATE TABLE MCQ_ANSWERS(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), FIRST_ANSWER VARCHAR(255), SECOND_ANSWER VARCHAR(255), THIRD_ANSWER VARCHAR(255), CORRECT_ANSWER VARCHAR(255))";
		String associativeChoicesTable = "CREATE TABLE ASSOCIATIVE_CHOICES(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), FIRST_CHOICE VARCHAR(255), SECOND_CHOICE VARCHAR(255))";
		String openTipsTable = "CREATE TABLE OPEN_TIPS(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), TIP VARCHAR(255))";
		String quizTable = "CREATE TABLE QUIZ(ID INT PRIMARY KEY auto_increment, TITLE VARCHAR(255), QUESTION_COUNT INT, CUSTOM_DIFFICULTY INT, AVERAGE_DIFFICULTY DOUBLE, TRUE_DIFFICULTY DOUBLE, OWNER_ID INT, foreign key (OWNER_ID) references USER(ID), PUBLICITY BOOLEAN)";
		String quizQuestionAsssociationTable = "CREATE TABLE QUIZ_QUESTION_ASSOCIATION(ID INT PRIMARY KEY auto_increment, QUIZ_ID INT, foreign key (QUIZ_ID) references QUIZ(ID), QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID))";
		
		return false;
	}
}
