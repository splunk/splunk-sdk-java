package com.splunk.erp.commons;

public class Constants {
	
	public static int OUTPUT_BUFFER_LIMIT = 32 * 1024;
	public static int MAX_BUFFER_FLUSH_SIZE = (int) (OUTPUT_BUFFER_LIMIT * 0.9);
	public static int INITIAL_BUFFER_FLUSH_SIZE = 1024;
	public static String NEWLINE = System.getProperty("line.separator");
	
	public enum STRING_CONSTANTS{
		JSON("json"),
		ONE_MB("1000000");
		
		private String literal;
		
		private STRING_CONSTANTS(String literal ){
			this.literal = literal;
		}
		
		public String getLiteral(){
			return this.literal;
		}
	}
	
	public enum Delimiters{
		COMMA(",");
		
		private String delimiter;
		
		private Delimiters(String delimiter)
		{
			this.delimiter = delimiter;
		}
		
		public String getDelimiter()
		{
			return this.delimiter;
		}
	}
}
