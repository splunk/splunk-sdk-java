package com.splunk.erp.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;

/**
 * POJO to represent 'info' element of S-2-ERP protocol JSON found under 'search' element.
 * It is a list of key-value pair and stored into Map.
 * 
 * @author smetkar
 */
public class SearchInfo {
	
	private Map<String,String> fields;
	
	public SearchInfo(Map<String,String> fields) {
		this.fields = fields;
	}
	
	/**
	 * Get value for particular field
	 * @param fieldName
	 * @return
	 */
	public String getFieldValue(String fieldName) {
		return fields.get(fieldName);
	}
	
	/**
	 * Get all set of fields available in info element of JSON 
	 * @return
	 */
	public Set<String> getSearchInfoFieldSet() {
		return fields.keySet();
	}
	
	/**
	 * Check if specified field is present
	 * @param fieldName
	 * @return boolean 
	 */
	public boolean hasField(String fieldName) {
		return fields.get(fieldName) != null ? true : false;
	}
	
	/**
	 * Get value for _timestamp field from info element of JSON
	 * @return Double (timestamp)
	 */
	public Double getTimestampInfo() {
		String timestamp = fields.get("_timestamp");
		Double timestampVal = null; 
		if(timestamp != null) {
			timestampVal = Double.parseDouble(timestamp);
		}
		return timestampVal;
	}
	
	/**
	 * Check if it is a Time range query
	 * It is a time range query if the info element of S-2-ERP protocol JSON contains '_search_et' or '_search_lt' element
	 * @return
	 */
	public boolean isTimeRangeQuery() {
		return (fields.get("_search_et") != null || fields.get("_search_lt") != null) ? true : false;
	}
	
	/**
	 * Get earliest time for search
	 * @return Double (timestamp)
	 */
	public Double getEarliestTime() {
		String timestamp = fields.get("_search_et");
		Double timestampVal = null; 
		if(timestamp != null) {
				timestampVal = Double.parseDouble(timestamp);
		}
		return timestampVal;
	}
	
	/**
	 * Get latest time for search
	 * @return Double (timestamp)
	 */
	public Double getLatestTime() {
		String timestamp = fields.get("_search_lt");
		Double timestampVal = null; 
		if(timestamp != null) {
				timestampVal = Double.parseDouble(timestamp);
		}
		return timestampVal;
	}
	
	/**
	 * Get user session authentication token
	 * 
	 * @return String : user session token
	 */
	public String getAuthenticationToken() {
		return fields.get("_auth_token");
	}
	
	/**
	 * Creates a SearchInfo object from S-2-ERP protocol JSON 
	 * @param JsonNode : JSON element containing search info 
	 * @return SearchInfo object
	 */
	public static SearchInfo getSearchInfoInstance(JsonNode searchInfoNode) {
		SearchInfo searchInfo = null;
		JsonNode infoNode = searchInfoNode.get("args").get("search").get("info");
		if(infoNode != null) {
			Map<String,String> fields = new HashMap<String,String>(); 
			if(infoNode.isArray()) {
				Iterator<JsonNode> it = infoNode.getElements();
				while(it.hasNext()){
					JsonNode node = it.next();
					Iterator<String> fieldNames = node.getFieldNames();
					while(fieldNames.hasNext()){
						String fieldName = fieldNames.next();
						//Ignoring '_tz' property because of unnecessary data present in it
						if(!fieldName.equalsIgnoreCase("_tz"))
							fields.put(fieldName,node.get(fieldName).getTextValue());
					}
				}
				if(fields != null) {
					searchInfo = new SearchInfo(fields); 
				}
			}
		}
		return searchInfo;	
	}
}
