package com.umbrella.ubsdk.rebuild;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class LogManager {
//	private final String TAG=LogManager.class.getSimpleName();
	private String BASE_PATH = System.getProperty("user.dir");
	private String LOG_PATH=BASE_PATH+File.separator+"out"+File.separator+"log.txt";
	private static LogManager instance=null;
	private LogManager(){}
	public static  LogManager getInstance(){
		if (instance==null) {
			synchronized (LogManager.class) {
				if (instance == null) {
					instance = new LogManager();
				}
			}
		}
		return instance;
	}
	
	public void setLogFile(){
		
		System.out.println(".....is operating.....");
		System.out.println(".....is operating.....");
		System.out.println(".....is operating.....");
		System.out.println();
		System.out.println(".....please see the detailed process in the log file.....");
		System.out.println();
		System.out.println(".....log file path----->"+LOG_PATH);
		
		File logFile = new File(LOG_PATH);
		try {
			if (!logFile.getParentFile().exists()) {
				logFile.getParentFile().mkdirs();
			}
			logFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(logFile);
			PrintStream ps = new PrintStream(fos);
			System.setOut(ps);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
