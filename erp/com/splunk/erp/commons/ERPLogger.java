package com.splunk.erp.commons;

/**
 * ERPLogger
 * The ERP Logger logs to <code>STDERR</code>
 * The levels are prepended to the log message
 * <br>
 * <code>ERPLogger.logInfo(" Sample info Message" ); </code>
 * <br> Output on STDERR: 
 * <code>INFO Sample info Message </code>
 * <br> Output on search.log 
 * <code> Timestamp INFO ERP.sample_erp_java -  Sample info Message </code>
 * 
 * @author snaik
 *
 */
public class ERPLogger {
	
	static enum LEVEL  {DEBUG, ERROR , INFO};

	public static void log (LEVEL level, String msg) {
		System.err.println (level + " " + msg);
	}
	
	public static void logDebug(String msg) {
		log(LEVEL.DEBUG, msg);
	}
	
	public static void logInfo(String msg) {
		log(LEVEL.INFO, msg);
	}
	
	public static void logError(String msg) {
		log(LEVEL.ERROR, msg);
	}

}
