package com.splunk.erp.core;

import java.util.List;
import java.util.Map;

import com.splunk.io.SearchMetricsReporter;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public interface ResultWriterProxy extends SearchMetricsReporter {
	
	/**
	 * Initialize {@link ResultWriter} object with index name, source and sourcetype of 
	 * virtual index along with other header fields
	 * @param String index : Virtual index name
	 * @param String source : Source of search results (eg. database/table)
	 * @param String sourcetype : Sourcetype of search results
	 * @param Map of header fields : You can specify values for parameters in props.conf, transform.conf etc.
	 */
	public void initializeWriter(String index, String source, String sourcetype, Map<String, String> headerFields) throws Exception;
	

	/**
	 * Method for ERP developers to push results to {@link ResultWriter}.
	 * Serializes records/documents as JSON string
	 * @param List of records/documents
	 * @throws Exception : Throws exception if any serializing object 
	 */
	public void append(List<?> recordBatch) throws Exception;
	
	/**
	 * This method takes serialized JSON string and keep appending it to {@link ByteOutputStream}. and streams 
	 * when buffer flush size is reached. Controls batchsize and buffer flush size increment.
	 * @param String : serialied JSON String
	 * @throws Exception : Throws exception if any while streaming results
	 */
	public void append(String record) throws Exception;
	
//	public boolean addHeaderField(String field, String fieldValue);
//	
//	public String getHeaderFieldValue(String field);
//	
//	public boolean isHeaderFieldPresent(String field);
//	
//	public boolean updateHeaderField(String field, String newFieldValue);
	
	/**
	 * Set regex of field from which timestamp needs to be extracted
	 * @param field prefix regex (String)
	 */
	public void setTimestampFieldPrefix(String timestampField);
	
	/**
	 * Set expected timestamp field format
	 * @param timestamp format regex (String)
	 */
	public void setTimestampFormat(String timestampFormat);
}
