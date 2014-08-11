package com.splunk.erp.core;

import org.codehaus.jackson.JsonNode;

public class SearchElement {

	protected String type;
	protected String operator;
	
	public SearchElement(){
	}
	
	public SearchElement(String type, String operator) {
		this.operator = operator;
		this.type = type;
	}

	public String getOperator() {
		return operator;
	}
	
	public String getType() {
		return type;
	}
	
	public void initFrom(JsonNode node) {
		this.operator = node.get("op").getTextValue();
		this.type = node.get("type").getTextValue();
	}
	
	public static SearchElement getByType(String type) throws IllegalArgumentException{
		if(type.equalsIgnoreCase("cmp")) 
			return new SearchCompareElement();
		else if (type.equalsIgnoreCase("group")) 
			return new SearchGroupElement();
		//TODO Implement sublclasses of SearchElement for type 'term' and 'phrase'
		else if(type.equalsIgnoreCase("term"))
			return null;
		else if(type.equalsIgnoreCase("phrase"))
			return null;
		throw new IllegalArgumentException("Unknown node type = "+type);
	}
}
