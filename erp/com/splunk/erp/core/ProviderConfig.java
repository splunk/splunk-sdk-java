package com.splunk.erp.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.splunk.erp.commons.ERPUtils;

/**
 * A POJO to represent Provider specific properties.
 * The properties/parameters which you define under [provider-family] and [provider] section in indexes.conf 
 * are combined and passes down in S-2-ERP protocol JSON as 'provider' element under 'conf' JSON element.
 * 
 * @author smetkar
 */

public class ProviderConfig {

	protected String familyName;
	protected String name;
	protected String mode;
	//Provider specific properties are stored in a Map
	protected Map<String, String> configParams;
	
	public ProviderConfig(String familyName,String providerName,String mode, Map<String, String> configParams) {
		this.familyName = familyName;
		this.configParams = configParams;
		this.mode = mode;
		this.name = providerName;
	}

	public Map<String, String> getConfigParams() {
		return Collections.unmodifiableMap(configParams);
	}

	public String getFamilyName() {
		return familyName;
	}
	
	public String getProviderName() {
		return name;
	}

	public String getMode() {
		return mode;
	}
	
//	/**
//	 * Get Provider implementation class name from S-2-ERP protocol JSON
//	 * @return String
//	 * @throws IllegalArgumentException
//	 */
//	public String getProviderImplClassName() throws IllegalArgumentException
//	{
//		String className = properties.get("class.name");
//		if(className == null)
//			throw new IllegalArgumentException("Unable to find the implementing class name");
//		return className;
//	}
	
	/**
	 * Create ProviderConfig object from S-2-ERP protocol JSON 
	 * @param JsonNode : JSON element for provider specific properties
	 * @return ProviderConfig object
	 */
	public static ProviderConfig getProviderConfigInstance(JsonNode providerConfigNode) {
		String familyName = ERPUtils.getFamilyName(providerConfigNode);
		String providerName = ERPUtils.getProviderName(providerConfigNode);
		String mode = ERPUtils.getSearchMode(providerConfigNode);
		
		Map<String,String> params = new HashMap<String, String>();
		Iterator<String> fieldIterator = providerConfigNode.getFieldNames();
		while(fieldIterator.hasNext()){
			String field = fieldIterator.next();
			params.put(field,providerConfigNode.get(field).getTextValue());
		}
		return new ProviderConfig(familyName,providerName,mode,params);
	}
}
