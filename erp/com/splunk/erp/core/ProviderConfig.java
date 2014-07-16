package com.splunk.erp.core;

import java.util.Map;

import com.splunk.erp.exception.ERPException;

public class ProviderConfig {

	private Map<String, Object> properties;
	private String familyName;

	public ProviderConfig(String familyName, Map<String, Object> properties) {
		this.familyName = familyName;
		this.properties = properties;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	
	public String getProviderImplClassName() throws ERPException
	{
		String className = (String)properties.get("class.name");
		if(className == null)
			throw new ERPException("Could not find implementing class name");
		return className;
	}
}