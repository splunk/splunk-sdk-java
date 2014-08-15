package com.splunk.erp.core;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.splunk.erp.commons.ERPUtils;
import com.splunk.io.ChunkedOutputStream;
import com.splunk.io.SearchOutputStream;

/**
 * This class has methods by which ERP developers push the list of records/documents to Splunkd process.<br>
 * It takes control of batching the results and streaming the results to Splunkd process.<br>
 * It implements {@link ResultWriterProxy} interface which defines method to push results, add header fields <br> 
 * and send metrics and messages for search process using {@link SearchOutputStream}.
 * @author smetkar
 *
 */
public class ResultWriter implements ResultWriterProxy{
	
	private static Logger logger = ERPUtils.getLogger(ResultWriter.class);
	
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

		OutputStream stdout = new FileOutputStream(FileDescriptor.out);
		outputStream = new SearchOutputStream(new ChunkedOutputStream(stdout));
		outputStream.setStreamType("raw");
    	
		System.setOut(System.err);
	}
	
	/**
	 * Writes content of {@link ByteArrayOutputStream} to {@link SearchOutputStream} and stream header 
	 * if header fields are updated.  
	 * @throws IOException
	 */
	private void stream() throws IOException{
		if(isHeaderUpdated) {
			logger.info("Updating stream header");
			logger.debug("Header fields : " + headerFields);
			outputStream.addHeader(headerFields);
			isHeaderUpdated = false;
		}
		outputStream.write(eventsCollector);
		eventsCollector.reset();
	}
	
	/**
	 * Close SearchOutputStream
	 * @throws IOException
	 */
	public void close() {
		try
		{
			if(eventsCollector.size() > 0)
			{
				stream();
				outputStream.flush();
			}
		} catch(IOException ioe) {
			logger.error("Error while closing result writer , Message - " + ioe.getMessage());
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
		logger.info("Initializing result writer");
		headerFields.clear();
		headerFields.put("field.index", index);
		headerFields.put("field.source", source);
		headerFields.put("field.sourcetype", sourcetype);
		headerFields.putAll(streamHeaderFields);
		isHeaderUpdated = true;
	}
	
	@Override 
	public void append(List<Object> recordBatch) throws JsonGenerationException,JsonMappingException,IOException {
		logger.debug("Appending " + recordBatch.size() + " records");
		//Using third-party library for serializing record/event object to JSON string 
		ObjectMapper mapper =  new ObjectMapper();
		for(Object record : recordBatch)
		{
			append(mapper.writeValueAsString(record));
		}	
	}
	
	@Override
	public void append(String result) throws IOException {
		eventsCollector.write(result.getBytes());
		eventsCollector.write(NEWLINE.getBytes());
		
		//Intermittent streaming of results to give a view of continuous result streaming to user
		//Double the buffer flush size for a maximum of MAX_BUFFER_FLUSH_SIZE
		if(eventsCollector.size() > bufferFlushSize)
		{
			stream();
			bufferFlushSize *= 2;
			if(bufferFlushSize > MAX_BUFFER_FLUSH_SIZE)
				bufferFlushSize = MAX_BUFFER_FLUSH_SIZE;
		}
		
		//keep track of records/events streamed so far
		//batchsize is used by ERP developer to understand what is the expected batch size while 
		//pushing records/events to Splunkd process
		count++;
		if(count == currentBatchSize) {
			currentBatchSize *= 2;
			if(currentBatchSize > MAX_BATCH_SIZE)
				currentBatchSize = MAX_BATCH_SIZE;
			count = 0;
			logger.debug("Updating batchsize to " + currentBatchSize);
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
		logger.info("Setting timestamp prefix to " + timestampPrefix);
		headerFields.put("props.TIME_PREFIX", timestampPrefix);
		isHeaderUpdated = true;
	}
	
	@Override
	public void setTimestampFormat(String format) {
		logger.info("Setting timestamp format to " + format);
		headerFields.put("props.TIME_FORMAT", format);
		isHeaderUpdated = true;
	}
	
	/**
	 * Set count metric for search process
	 */
	public void addCountMetric(String name, long input, long output) {
		logger.info("Setting count metric : " + name + " { input : " + input + " output : " + output +"}");
		outputStream.addCountMetric(name, input, output);
		isHeaderUpdated = true;
	}

	public void addLink(String name, String url) {
		logger.info("Adding link : " + name + " url : " + url);
		outputStream.addLink(name, url);
		isHeaderUpdated = true;
	}

	/**
	 * Set message for search process
	 */
	public void addMessage(String message, String value) {
		logger.info("Adding message - " + value );
		outputStream.addMessage(message, value);
		isHeaderUpdated = true;
	}

	/**
	 * Set exception message for search process
	 */
	public void addMessage(String message, Exception exception) {
		logger.info("Adding exception message - " + message);
		outputStream.addMessage(message, exception);
		isHeaderUpdated = true;
		
	}

	/**
	 * Set metric for search process
	 */
	public void addMetric(String metricName, long elapsed_ms, long calls) {
		logger.info("Adding metric - " + metricName);
		outputStream.addMetric(metricName, elapsed_ms, calls);
		isHeaderUpdated = true;
	}

	/**
	 * Set prefix for field from metric should be extracted
	 */
	public void setMetricPrefix(String prefix) {
		logger.info("Setting metric prefix to " + prefix);
		outputStream.setMetricPrefix(prefix);
		isHeaderUpdated = true;
	}
}
