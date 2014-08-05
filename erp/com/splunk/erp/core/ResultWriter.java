package com.splunk.erp.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.splunk.erp.commons.ERPLogger;
import com.splunk.io.ChunkedOutputStream;
import com.splunk.io.SearchOutputStream;

/**
 * This class has methods by which ERP developers push the list of records/documents to Splunkd process.<br>
 * It takes control of batching the results and streaming the results to Splunkd process.<br>
 * It implements {@link ResultWriterProxy} interface which defines method to push results, add header fields 
 * and send metrics and messages for search process using {@link SearchOutputStream}.
 * @author smetkar
 *
 */
public class ResultWriter implements ResultWriterProxy{
	
	private static int OUTPUT_BUFFER_LIMIT = 32 * 1024;
	private static int MAX_BUFFER_FLUSH_SIZE = (int) (OUTPUT_BUFFER_LIMIT * 0.9);
	private static int INITIAL_BUFFER_FLUSH_SIZE = 1024;
	private static String NEWLINE = System.getProperty("line.separator");
	private static int MAX_BATCH_SIZE = 5000;
	private static int INITIAL_BATCH_SIZE = 10;

	private boolean isHeaderUpdated;
	private ByteArrayOutputStream eventsCollector;
	private int bufferFlushSize;
	private Map<String,Object> headerFields;
	private SearchOutputStream outputStream;
	private int count;
	private int currentBatchSize;
	
	
	public ResultWriter(String hostName) {
		currentBatchSize = INITIAL_BATCH_SIZE;

		//Some default header fields but can be updated using initializeWriter method
		headerFields.put("field.host", hostName);
		headerFields.put("props.KV_MODE", "json");
		headerFields.put("props.TRUNCATE", "1000000");
		isHeaderUpdated = true;
		
		eventsCollector = new ByteArrayOutputStream(OUTPUT_BUFFER_LIMIT);
		bufferFlushSize = INITIAL_BUFFER_FLUSH_SIZE;
		
		outputStream = new SearchOutputStream(new ChunkedOutputStream(System.out)); 
		outputStream.setStreamType("raw");
	}
	
	/**
	 * Writes content of {@link ByteArrayOutputStream} to {@link SearchOutputStream} and stream header 
	 * if header fields are updated.  
	 * @throws Exception
	 */
	private void stream() throws Exception{
		try {
			if(isHeaderUpdated) {
				outputStream.addHeader(headerFields);
				isHeaderUpdated = false;
			}
			outputStream.write(eventsCollector);
			eventsCollector.reset();
		} catch (IOException e) {
			ERPLogger.logError( e.getMessage());
			throw new Exception("Error while streaming results to Splunk");
		}
	}
	
	/**
	 * Close SearchOutputStream
	 * @throws IOException
	 */
	public void close() throws Exception
	{
		if(eventsCollector.size() > 0)
		{
			stream();
			outputStream.flush();
		}
	}
	
	/**
	 * Get current batch size
	 * @return int : current batch size
	 */
	public int getCurrentBatchSize() {
		return currentBatchSize;
	}
	
	@Override
	public void initializeWriter(String index, String source, String sourcetype, Map<String, String> streamHeaderFields) {
		headerFields.clear();
		headerFields.put("field.index", index);
		headerFields.put("field.source", source);
		headerFields.put("field.sourcetype", sourcetype);
		headerFields.putAll(streamHeaderFields);
		isHeaderUpdated = true;
	}
	
	@Override 
	public void append(List<?> recordBatch) throws Exception {
		ObjectMapper mapper =  new ObjectMapper();
		for(Object record : recordBatch)
		{
			try {
				append(mapper.writeValueAsString(record));
			}catch (JsonGenerationException jge) {
				ERPLogger.logError(jge.getMessage());
				throw new Exception("Error while serializing to JSON string");
			}catch(JsonMappingException jme) {
				ERPLogger.logError(jme.getMessage());
				throw new Exception("Error while serializing to JSON string");
			}catch(IOException ioe) {
				ERPLogger.logError(ioe.getMessage());
				throw new Exception("Error while serializing to JSON string");
			}
		}	
	}
	
	@Override
	public void append(String result) throws Exception {
		try {
			eventsCollector.write(result.getBytes());
			eventsCollector.write(NEWLINE.getBytes());
			if(eventsCollector.size() > bufferFlushSize)
			{
				stream();
				bufferFlushSize *= 2;
				if(bufferFlushSize > MAX_BUFFER_FLUSH_SIZE)
					bufferFlushSize = MAX_BUFFER_FLUSH_SIZE;
			}
			
			count++;
			if(count == currentBatchSize) {
				currentBatchSize *= 2;
				if(currentBatchSize > MAX_BATCH_SIZE)
					currentBatchSize = MAX_BATCH_SIZE;
				count = 0;
			}
		} catch (IOException ioe) {
			ERPLogger.logError(ioe.getMessage());
			throw new Exception("Error while writing to event collector");
		}
	}
	
//	/**
//	 * Add new header field
//	 * Returns true if a new field that is added else false. 
//	 */
//	public boolean addHeaderField(String field, Object fieldValue) {
//		boolean isNewField = false;
//		
//		if(headerFields.get(field) == null){
//			headerFields.put(field,fieldValue);
//			isNewField = true;
//			this.isHeaderUpdated = true;
//			ERPLogger.logInfo("New header field "+ field+ " added with value as " + fieldValue);
//		}
//		return isNewField;
//	}
//	
//	/**
//	 * Adding new header fields or updating header fields
//	 * @param field
//	 * @param new value for the field
//	 */
//	public boolean updateHeaderField(String field, Object newVal)
//	{
//		boolean isFieldUpdated = false;
//		Object prevVal = headerFields.get(field);
//		
//		if(prevVal == null || (newVal != null && !newVal.toString().isEmpty() && !newVal.equals(prevVal))){
//			headerFields.put(field,newVal);
//			isFieldUpdated = true;
//			isHeaderUpdated = true;
//			ERPLogger.logInfo("Header field " + field + " updated with value : "+ newVal); 
//		}
//		return isFieldUpdated;	
//	}
//	
//	/**
//	 * Get value for specific stream header field. Returns field value if present else null.
//	 * @param key
//	 * @return value for field
//	 * 
//	 */
//	public String getHeaderFieldValue(String field)
//	{
//		if(headerFields.get(field) != null) {
//			return headerFields.get(field).toString();
//		}
//		return null;
//	}
//	
//	/**
//	 * Check if a specific header field is present
//	 */
//	public boolean isHeaderFieldPresent(String field)
//	{
//		boolean isPresent = false;
//		if(headerFields.get(field)!= null)
//			isPresent = true;
//		return isPresent;
//	}
	
	@Override
	public void setTimestampFieldPrefix(String timestampPrefix) {
		headerFields.put("props.TIME_PREFIX", timestampPrefix);
		isHeaderUpdated = true;
	}
	
	@Override
	public void setTimestampFormat(String format) {
		headerFields.put("props.TIME_FORMAT", format);
		isHeaderUpdated = true;
	}
	
	/**
	 * Set count metric for search process
	 */
	public void addCountMetric(String name, long input, long output) {
		outputStream.addCountMetric(name, input, output);
		isHeaderUpdated = true;
	}

	public void addLink(String name, String url) {
		outputStream.addLink(name, url);
		isHeaderUpdated = true;
	}

	/**
	 * Set message for search process
	 */
	public void addMessage(String message, String value) {
		outputStream.addMessage(message, value);
		isHeaderUpdated = true;
	}

	/**
	 * Set exception message for search process
	 */
	public void addMessage(String message, Exception exception) {
		outputStream.addMessage(message, exception);
		isHeaderUpdated = true;
		
	}

	/**
	 * Set metric for search process
	 */
	public void addMetric(String metricName, long elapsed_ms, long calls) {
		outputStream.addMetric(metricName, elapsed_ms, calls);
		isHeaderUpdated = true;
	}

	/**
	 * Set prefix for field from metric should be extracted
	 */
	public void setMetricPrefix(String prefix) {
		outputStream.setMetricPrefix(prefix);
		isHeaderUpdated = true;
	}
}
