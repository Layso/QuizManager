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
	
	
	
	/**
	 * Method to see if user has solved the quiz before or not
	 * @param quizID    ID of quiz to check
	 * @param userID    ID of user to check
	 * @return          True if there is a result entry for user and quiz, else false
	 */
	public boolean DidUserSolveQuiz(int quizID, int userID) {
		String sqlQuery = "select *  from ANSWER_TABLE where QUIZ_ID = ? and USER_ID = ?";
		boolean result;
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, quizID);
			statement.setInt(2, userID);
			ResultSet results =  statement.executeQuery();
			
			result = results.next();
		} catch (SQLException e) {
			result = false;
			Logger.Log("Fatal Error: Failed to update userID: " + userID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return result;
	}
	
	
	
	/**
	 * Method to user's authority status
	 * @param userID        ID of user to update
	 * @param authorititive New authority status for user
	 */
	public  void UpdateUserAuthority(int userID, boolean authorititive) {
		String sqlQuery = "update USER set AUTHORITY = ? where ID = ?";
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setBoolean(1, authorititive);
			statement.setInt(2, userID);
			statement.execute();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to update userID: " + userID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Method to get all users
	 * @return List of users
	 */
	public List<User> GetAllUsers() {
		String sqlQuery = "select ID, USERNAME, AUTHORITY from USER";
		List<User> list = new ArrayList<>();
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			ResultSet results = statement.executeQuery();
			
			while(results.next()) {
				User newUser = new User(results.getInt("ID"), results.getString("USERNAME"), results.getBoolean("AUTHORITY"));
				list.add(newUser);
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get all users: " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		
		return list;
	}
	
	
	
	/**
	 * Method to get results for given user ID. Results for users with and without authority are different
	 * @param userID    ID of user to get associated results
	 * @return          If user has authority returns results of quizzes created by user, else returns results of quizzes solved by user
	 */
	public List<AnswerTable> GetAllAnswersByUserID (int userID) {
		String sqlQueryAuthoratitive = "select QUIZ_ID, USER_ID, NOT_CORRECTED_ANSWERS, TRUE_ANSWERS, FALSE_ANSWERS from ANSWER_TABLE where QUIZ_ID = ?";
		String sqlQueryNonAuthoratitive = "select QUIZ_ID, USER_ID, NOT_CORRECTED_ANSWERS, TRUE_ANSWERS, FALSE_ANSWERS from ANSWER_TABLE where USER_ID = ?";
		List<AnswerTable> list = new ArrayList<>();
		
		
		try {
			PreparedStatement statement;
			if (GetUserByID(userID).isAuthoritative()) {
				List<Quiz> quizzes = GetOwningQuizzes();
				for (Quiz quiz : quizzes) {
					statement = connection.prepareStatement(sqlQueryAuthoratitive);
					statement.setInt(1, quiz.GetID());
					ResultSet results = statement.executeQuery();
					
					while (results.next()) {
						AnswerTable answerTable = new AnswerTable(GetQuizByID(results.getInt("QUIZ_ID")),
							GetUserByID(results.getInt("USER_ID")), results.getInt("NOT_CORRECTED_ANSWERS"),
							results.getInt("FALSE_ANSWERS"), results.getInt("TRUE_ANSWERS"));
						
						list.add(answerTable);
					}
				}
			}
			
			else {
				statement = connection.prepareStatement(sqlQueryNonAuthoratitive);
				statement.setInt(1, userID);
				ResultSet results = statement.executeQuery();
				
				
				while (results.next()) {
					AnswerTable answerTable = new AnswerTable(GetQuizByID(results.getInt("QUIZ_ID")),
						GetUserByID(results.getInt("USER_ID")), results.getInt("NOT_CORRECTED_ANSWERS"),
						results.getInt("FALSE_ANSWERS"), results.getInt("TRUE_ANSWERS"));
					
					list.add(answerTable);
				}
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get answer table list: " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return list;
	}
	
	
	
	/**
	 * Method to get ready to evaluate answers for quizzes created by current user
	 * @return  List of Uncorrected Questions
	 */
	public List<NotCorrectedOpenQuestion> GetAllUncorrectedQuestions() {
		String sqlQuery = "select ID, QUESTION_ID, QUIZ_ID, USER_ID, ANSWER from NOT_CORRECTED_OPEN";
		List<NotCorrectedOpenQuestion> list = new ArrayList<>();
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				Quiz quiz = GetQuizByID(results.getInt("QUIZ_ID"));
				if (quiz.GetOwnerID() == QuizManager.getInstance().GetUser().GetID()) {
					NotCorrectedOpenQuestion ncoq = new NotCorrectedOpenQuestion(results.getInt("ID"), quiz,
						((OpenQuestion) GetQuestionByID(results.getInt("QUESTION_ID"))),
						results.getString("ANSWER"), GetUserByID(results.getInt("USER_ID")));
					
					list.add(ncoq);
				}
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get not corrected answers list: " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return list;
	}
	
	
	
	/**
	 * Method to save answer to database
	 * @param quizID            ID of solved quiz
	 * @param userID            ID of user who solved the quiz
	 * @param correctedCount    Number of not correcteds
	 * @param trueCount         Number of trues
	 * @param falseCount        Number of falses
	 */
	public void SaveAnswer(int quizID, int userID, int correctedCount, int trueCount, int falseCount) {
		String sqlQuery = "insert into ANSWER_TABLE(QUIZ_ID, USER_ID, NOT_CORRECTED_ANSWERS, TRUE_ANSWERS, FALSE_ANSWERS) values(?, ?, ?, ?, ?)";
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, quizID);
			statement.setInt(2, userID);
			statement.setInt(3, correctedCount);
			statement.setInt(4, trueCount);
			statement.setInt(5, falseCount);
			statement.execute();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to save answers for quizID: " + quizID + "  for userID: " + userID
				+ ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Helper to get answer tables according to quiz and user ID
	 * @param quizID    ID of solved quiz
	 * @param userID    ID of solver
	 * @return          Results of the quiz
	 */
	private AnswerTable GetAnswerTableByQuizAndUser(int quizID, int userID) {
		String sqlQuery = "select NOT_CORRECTED_ANSWERS, TRUE_ANSWERS, FALSE_ANSWERS from ANSWER_TABLE where QUIZ_ID = ? and USER_ID = ?";
		AnswerTable table = null;
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, quizID);
			statement.setInt(2, userID);
			ResultSet results = statement.executeQuery();
			
			if (results.next()) {
				table = new AnswerTable(GetQuizByID(quizID), GetUserByID(userID), results.getInt("NOT_CORRECTED_ANSWERS"),
					results.getInt("FALSE_ANSWERS"), results.getInt("TRUE_ANSWERS"));
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get answer table for quizID: " + quizID + "  for userID: " + userID
				+ ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		
		return table;
	}
	
	
	
	/**
	 * Method to update not corrected open answer
	 * @param notCorrected  Answer that wasn't corrected
	 * @param result        Result of answer
	 */
	public void UpdateNotCorrected(NotCorrectedOpenQuestion notCorrected, boolean result) {
		String sqlDeleteQuery = "delete from NOT_CORRECTED_OPEN where ID = ?";
		String sqlUpdateQuery = "update ANSWER_TABLE set NOT_CORRECTED_ANSWERS = ?, TRUE_ANSWERS = ?, FALSE_ANSWERS = ? where QUIZ_ID = ? and USER_ID = ?";
		AnswerTable table = GetAnswerTableByQuizAndUser(notCorrected.GetQuiz().GetID(), notCorrected.GetAnswerer().GetID());
		
		
		try {
			PreparedStatement deleteStatement = connection.prepareStatement(sqlDeleteQuery);
			deleteStatement.setInt(1, notCorrected.GetID());
			deleteStatement.execute();
			
			PreparedStatement updateStatement = connection.prepareStatement(sqlUpdateQuery);
			updateStatement.setInt(1, table.GetNotCorrectedAnswers() - 1);
			updateStatement.setInt(2, table.GetTrueAnswers() + (result ? 1 : 0));
			updateStatement.setInt(3, table.GetFalseAnswers() + (result ? 0 : 1));
			updateStatement.setInt(4, table.GetQuiz().GetID());
			updateStatement.setInt(5, table.GetAnswerer().GetID());
			updateStatement.execute();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to update open answer correction for quizID: " + table.GetQuiz().GetID() +
				"  for userID: " + table.GetAnswerer().GetID() + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Method to save not corrected open question
	 * @param question  Question that is answered
	 * @param answer    Answer of the question that is not corrected
	 */
	public void SaveNotCorrected(OpenQuestion question, OpenAnswer answer) {
		String sqlQuery = "insert into NOT_CORRECTED_OPEN(QUESTION_ID, QUIZ_ID, USER_ID, ANSWER) values(?, ?, ?, ?)";
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, question.GetID());
			statement.setInt(2, answer.GetQuizID());
			statement.setInt(3, answer.GetAnswerer().GetID());
			statement.setString(4, answer.GetAnswer());
			statement.execute();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to save open answer for quizID: " + answer.GetQuizID() +
				"  for userID: " + answer.GetAnswerer().GetID() + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Method to get all public quizzes which can be solved
	 * @return  List of public quizzes
	 */
	public List<Quiz> GetAllPublicQuizzes() {
		String sqlQuery = "select ID from QUIZ where PUBLICITY = TRUE";
		List<Quiz> quizzes = new ArrayList<>();
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				quizzes.add(GetQuizByID(results.getInt("ID")));
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to get public quizzes: " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return quizzes;
	}
	
	
	
	/**
	 * Method to update question
	 * @param oldQuestion   Old question to update
	 * @param newQuestion   New question to update with
	 */
	public void ChangeQuestion(Question oldQuestion, Question newQuestion) {
		DeleteQuestionByID(oldQuestion.GetID(), false);
		SaveQuestion(oldQuestion.GetID(), newQuestion);
		UpdateAllQuizzes();
	}
	
	
	
	/**
	 * Method to delete question by ID
	 * @param questionID    ID of question to delete
	 * @param safeDelete    Flag to safe delete or normal delete the question
	 */
	public void DeleteQuestionByID(int questionID, boolean safeDelete) {
		String[] queries = {"delete from RESOURCE where QUESTION_ID = ?",
							"delete from TOPIC where QUESTION_ID = ?",
							"delete from MCQ_ANSWERS where QUESTION_ID = ?",
							"delete from ASSOCIATIVE_CHOICES where QUESTION_ID = ?",
							"delete from OPEN_TIPS where QUESTION_ID = ?",
							"delete from NOT_CORRECTED_OPEN where QUESTION_ID = ?",
							"delete from QUESTION where ID = ?"};
		
		try {
			for (String query : queries) {
				PreparedStatement statement = connection.prepareStatement(query);
				statement.setInt(1, questionID);
				statement.execute();
			}
			
			
			if (safeDelete) {
				SafeQuestionDeletion(questionID);
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to remove question ID: " + questionID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Helper method for quiz deletion to update quiz table aswell
	 * @param questionID    ID of question to delete
	 */
	private void SafeQuestionDeletion(int questionID) {
		String sqlQuery = "select QUIZ_ID from QUIZ_QUESTION_ASSOCIATION where QUESTION_ID = ?";
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, questionID);
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				DeleteQuestionFromQuiz(results.getInt("QUIZ_ID"), questionID);
			}
			UpdateAllQuizzes();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to safely delete question ID: " + questionID + " from quiz: " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		UpdateAllQuizzes();
	}
	
	
	
	/**
	 * Method to delete question from given quiz
	 * @param quizID        ID of quiz to delete from
	 * @param questionID    ID of question to delete
	 */
	public void DeleteQuestionFromQuiz(int quizID, int questionID) {
		String sqlDeleteQuery = "delete from QUIZ_QUESTION_ASSOCIATION where QUIZ_ID = ? and QUESTION_ID = ?";
		
		
		try {
			PreparedStatement deleteStatement = connection.prepareStatement(sqlDeleteQuery);
			deleteStatement.setInt(1, quizID);
			deleteStatement.setInt(2, questionID);
			deleteStatement.execute();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to remove question ID: " + questionID + " from quiz ID: " + quizID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		Quiz quiz = GetQuizByID(quizID);
		List<Question> questions = quiz.GetQuestions();
		if (questions.isEmpty()) {
			DeleteQuizByID(quizID);
		}
	}
	
	
	
	/**
	 * Method to recalculate question statistics of quizzes
	 */
	public void UpdateAllQuizzes() {
		String sqlQuery = "select ID from QUIZ";
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				Quiz quiz = GetQuizByID(results.getInt("ID"));
				quiz.RecalculateDifficulties();
				UpdateQuizByID(quiz.GetID(), quiz);
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to update all quizzes: " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Method to update quiz
	 * @param quizID    ID of quiz to update
	 * @param newData   Object with new quiz attributes to update with
	 */
	public void UpdateQuizByID(int quizID, Quiz newData) {
		String sqlQuery = "update QUIZ set TITLE = ?, QUESTION_COUNT = ?, CUSTOM_DIFFICULTY = ?, AVERAGE_DIFFICULTY = ?, TRUE_DIFFICULTY = ?, PUBLICITY = ? where ID = ?";
		
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, newData.GetQuizTitle());
			statement.setInt(2, newData.GetQuestions().size());
			statement.setInt(3, newData.GetCustomDifficulty());
			statement.setDouble(4, newData.GetAverageDifficulty());
			statement.setDouble(5, newData.GetTrueDifficulty());
			statement.setBoolean(6, newData.GetPublicity());
			statement.setInt(7, quizID);
			statement.execute();
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to update quiz ID: " + quizID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Method to delete given quiz ID
	 * @param quizID    ID of quiz to delete
	 */
	public void DeleteQuizByID(int quizID) {
		String[] queries = {"delete from ANSWER_TABLE where QUIZ_ID = ?",
							"delete from NOT_CORRECTED_OPEN where QUIZ_ID = ?",
							"delete from QUIZ where ID = ?"};
		
		try {
			for (String query : queries) {
				PreparedStatement answerTableStatement = connection.prepareStatement(query);
				answerTableStatement.setInt(1, quizID);
				answerTableStatement.execute();
			}
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to remove quiz ID: " + quizID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	
	/**
	 * Method to get all quizzes owned by currently logged in user
	 * @return  List of quiz created by currently logged in user
	 */
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
	
	
	
	/**
	 * Method to get given quiz from database
	 * @param quizID    ID of quiz to fetch
	 * @return          Quiz with given ID
	 */
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
	
	
	
	/**
	 * Method to get questions associated to quiz
	 * @param quizID    ID of quiz to get questions of
	 * @return          List of questions
	 */
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
			Logger.Log("Fatal Error: Failed to get question with ID: " + questionID + " " + e.getMessage(),
				Logger.LogType.ERROR);
			e.printStackTrace();
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
			Logger.Log("Fatal Error: SQL exception caught while trying to get resource for question ID: " + questionID + ": " + e.getMessage(), Logger.LogType.WARNING);
			System.exit(1);
		} catch (IOException e) {
			Logger.Log("Fatal Error: IO exception caught while trying to get resource for question ID: " + questionID + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
	}
	
	
	/**
	 * Predefined database insert method for quiz creation
	 * @param quiz  Quiz to save to database
	 * @return      Returns the ID of new quiz
	 */
	public int CreateQuiz(Quiz quiz) {
		String sqlQuery = "insert into QUIZ(TITLE, QUESTION_COUNT, CUSTOM_DIFFICULTY, AVERAGE_DIFFICULTY, TRUE_DIFFICULTY, OWNER_ID, PUBLICITY) values(?, ?, ?, ?, ?, ?, ?)";
		int quizID = -1;
		
		
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
				AssociateQuizAndQuestion(SaveQuestion(-1, q), quizID);
			}
			Logger.Log("All questions of quiz (ID: " + quizID + ") successfully inserted", Logger.LogType.INFO);
		} catch (SQLException e) {
			Logger.Log("Fatal Error: Failed to insert new quiz: " + quiz.GetQuizTitle() + ": " + e.getMessage(), Logger.LogType.ERROR);
			System.exit(1);
		}
		
		return quizID;
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
	public int SaveQuestion(int customID, Question question) {
		String sqlQuery = customID == -1 ?
			"insert into QUESTION(QUESTION, RESOURCE, TYPE, PUBLICITY, DIFFICULTY, CORRECT_ANSWERS, FALSE_ANSWERS, OWNER_ID) values(?, ?, ?, ?, ?, ?, ?, ?)" :
			"insert into QUESTION(QUESTION, RESOURCE, TYPE, PUBLICITY, DIFFICULTY, CORRECT_ANSWERS, FALSE_ANSWERS, OWNER_ID, ID) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int questionID = 0;
		
		try {
			PreparedStatement statement = customID == -1 ?
				connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS) :
				connection.prepareStatement(sqlQuery);
			
			statement.setString(1, question.GetQuestion());
			statement.setString(2, Boolean.toString(!question.GetResource().equals("")));
			statement.setString(3, question.GetType().name());
			statement.setString(4, Boolean.toString(question.GetPublicity()));
			statement.setString(5, Integer.toString(question.GetDifficulty()));
			statement.setString(6, Integer.toString(question.GetCorrectAnswers()));
			statement.setString(7, Integer.toString(question.GetFalseAnswers()));
			statement.setString(8, Integer.toString(question.GetOwner().GetID()));
			
			if (customID != -1) {
				statement.setInt(9, customID);
				statement.execute();
				questionID = customID;
			} else {
				statement.execute();
				ResultSet result = statement.getGeneratedKeys();
				result.next();
				questionID = result.getInt("ID");
			}
			
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
			e.printStackTrace();
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
	public void AssociateQuizAndQuestion(int questionID, int quizID) {
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
				user = new User(userID, results.getString("USERNAME"), results.getBoolean("AUTHORITY"));
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
		List<User> userList = GetAllUsers();
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setBoolean(3, userList.isEmpty());
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
					user = new User(results.getInt("ID"), results.getString("USERNAME"), results.getBoolean("AUTHORITY"));
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
	
	
	
	public void SchemaCheck() {
		String userTable = "CREATE TABLE USER(ID INT PRIMARY KEY auto_increment, USERNAMAE VARCHAR(255), PASSWORD VARCHAR(255), AUTHORITY BOOLEAN)";
		String questionTable = "CREATE TABLE QUESTION(ID INT PRIMARY KEY auto_increment, QUESTION VARCHAR(255), RESOURCE BOOLEAN, TYPE VARCHAR(255), PUBLICITY BOOLEAN, DIFFICULTY INT, CORRECT_ANSWERS INT, FALSE_ANSWERS INT, OWNER_ID INT, foreign key (OWNER_ID) references USER(ID))";
		String resourceTable = "CREATE TABLE RESOURCE(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), RESOURCE BLOB, SIZE INT)";
		String topicTable = "CREATE TABLE TOPIC(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), TOPIC VARCHAR(255))";
		String mcqChoicesTable = "CREATE TABLE MCQ_ANSWERS(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), FIRST_ANSWER VARCHAR(255), SECOND_ANSWER VARCHAR(255), THIRD_ANSWER VARCHAR(255), CORRECT_ANSWER VARCHAR(255))";
		String associativeChoicesTable = "CREATE TABLE ASSOCIATIVE_CHOICES(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), FIRST_CHOICE VARCHAR(255), SECOND_CHOICE VARCHAR(255))";
		String openTipsTable = "CREATE TABLE OPEN_TIPS(ID INT PRIMARY KEY auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), TIP VARCHAR(255))";
		String quizTable = "CREATE TABLE QUIZ(ID INT PRIMARY KEY auto_increment, TITLE VARCHAR(255), QUESTION_COUNT INT, CUSTOM_DIFFICULTY INT, AVERAGE_DIFFICULTY DOUBLE, TRUE_DIFFICULTY DOUBLE, OWNER_ID INT, foreign key (OWNER_ID) references USER(ID), PUBLICITY BOOLEAN)";
		String quizQuestionAsssociationTable = "CREATE TABLE QUIZ_QUESTION_ASSOCIATION(ID INT PRIMARY KEY auto_increment, QUIZ_ID INT, foreign key (QUIZ_ID) references QUIZ(ID), QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID))";
		String answerTableTable = "CREATE TABLE ANSWER_TABLE(ID INT PRIMARY KEY auto_increment, QUIZ_ID INT, foreign key (QUIZ_ID) references QUIZ(ID), USER_ID INT, foreign key (USER_ID) references USER(ID), NOT_CORRECTED_ANSWERS INT, TRUE_ANSWERS INT, FALSE_ANSWERS INT)";
		String notCorrectedOpenTable = "CREATE TABLE NOT_CORRECTED_OPEN(ID INT primary key auto_increment, QUESTION_ID INT, foreign key (QUESTION_ID) references QUESTION(ID), QUIZ_ID INT, foreign key (QUIZ_ID) references QUIZ(ID), USER_ID INT, foreign key (USER_ID) references USER(ID), ANSWER VARCHAR(255))";
		
	}
}
