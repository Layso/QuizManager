package com.layso.logger.test;


import com.layso.logger.datamodel.Logger;


public class LoggerTester {
	public static void main(String[] args) {
		Logger.Setup("log.txt", true, true);
		Logger.Log("Testing", Logger.LogType.INFO);
	}
}