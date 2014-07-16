package com.splunk.erp.core;

public class SearchElement {

	private String type;
	private String operator;
	
	public SearchElement(){
	}
	
	public SearchElement(String type,String operator) {
		this.type = type;
		this.operator = operator;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
