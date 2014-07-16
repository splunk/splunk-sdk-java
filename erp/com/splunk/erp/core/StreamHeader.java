package com.splunk.erp.core;

import java.util.Map;

import static com.splunk.erp.commons.HEADER_FIELDS.*;

public class StreamHeader {

	private Map<String,Object> headerFields;

	public StreamHeader(){
		headerFields.put(KV_MODE.getFieldName(), "json");
		headerFields.put(TRUNCATE.getFieldName(), "1000000");
	}
	
	public Map<String, Object> getHeaderFields() {
		return headerFields;
	}

	public void setHeaderFields(Map<String, Object> headerFields) {
		this.headerFields = headerFields;
	}
	
	public void addHeaderField(String key, Object value){
		headerFields.put(key, value);
	}

	/**
	 * Adding new header fields or updating header fields
	 * @param field
	 * @param new value for field
	 */
	public boolean updateHeaderField(String key, Object newVal)
	{
		boolean isUpdated = false;
		Object prevVal = headerFields.get(key);
		
		if(prevVal == null || (!newVal.toString().isEmpty()  && newVal != null && !newVal.equals(prevVal))){
			headerFields.put(key,newVal);
			isUpdated = true;
		}
		return isUpdated;	
	}
	
	/**
	 * Get specific stream header field
	 * @param key
	 * @return value for field
	 */
	public String getHeaderField(String key)
	{
		return headerFields.get(key).toString();
	}
	
	/**
	 * Check if a specific field is present
	 */
	public boolean isFieldPresent(String key)
	{
		boolean isPresent = false;
		if(headerFields.get(key)!= null)
			isPresent = true;
		return isPresent;
	}
}
