package com.splunk.erp.core;

import java.util.HashSet;

import org.codehaus.jackson.JsonNode;

/**
 * 'search_expr' element in S-2-ERP protocol JSON represents the parsed SPL query.
 * POJO to represent search_expr of type 'cmp'. <br>
 * Example of 'cmp' type search_expr
 * 
 * {	
 * 		"type":"cmp",
 * 		"op":"=",
 * 		"lhs":"salary",
 * 		"rhs":"138967.*",
 * 		"is_negated":false,
 * 		"is_numeric":false,
 * 		"is_literal_term":false,
 * 		"is_case_sensitive":false,
 * 		"is_cidr_match":false
 * }
 * <code> is_numeric </code> attribute states if <code> rhs </code> is numeric
 * <code> is_case_sensitive </code> attribute states if the search for string is case_sensitive
 * <code> is_literal_term </code> attribute states if the search query is phrase query
 * 
 *  <br>
 *  Valid operators for SearchCompareElement are '<', '>' , '<=' , '>=' and '!='.
 * @author smetkar
 *
 */
public class SearchCompareElement extends SearchElement {

	protected static final HashSet<String> VALID_OP = new HashSet<String>();
	
	static{
		VALID_OP.add("=");
		VALID_OP.add("!=");
		VALID_OP.add("<");
		VALID_OP.add("<=");
		VALID_OP.add(">");
		VALID_OP.add(">=");
	}
	
	private String lhs;
	private Object rhs;
	private boolean is_negated;
	private boolean is_numeric;
	private boolean is_literal_term;
	private boolean is_case_sensitive;
	private boolean is_cidr_match;
	
	public SearchCompareElement(){}

	public SearchCompareElement(String type, String operator, String lhs, Object rhs, boolean is_negated,
			boolean is_numeric, boolean is_literal_term,
			boolean is_case_sensitive, boolean is_cidr_match) {
		super(type, operator);
		this.lhs = lhs;
		this.rhs = rhs;
		this.is_negated = is_negated;
		this.is_case_sensitive = is_case_sensitive;
		this.is_literal_term = is_literal_term;
		this.is_numeric = is_numeric;
	}
	
	/**
	 * Initialize SearchCompareElement from JsonNode object
	 */
	public void initFrom(JsonNode compareNode) {

		if(!VALID_OP.contains(compareNode.get("op").getTextValue())) 
				throw new IllegalArgumentException("Not a valid operator for SearchCompareElement");
		
		super.initFrom(compareNode);
		lhs = compareNode.get("lhs").getTextValue();
		rhs = compareNode.get("rhs").getTextValue();
		is_numeric = compareNode.get("is_numeric").getBooleanValue();
		is_literal_term = compareNode.get("is_literal_term").getBooleanValue();
		is_case_sensitive = compareNode.get("is_case_sensitive").getBooleanValue();
		is_cidr_match = compareNode.get("is_cidr_match").getBooleanValue();
		
		/*
		 * If rhs is numeric convert it into Double object else check if it is not case-sensitive and lowercase rhs.
		 */
		if(is_numeric) {
			rhs = Double.parseDouble(rhs.toString());
		} else if(!is_case_sensitive) {
				rhs = rhs.toString().toLowerCase();
		}
	}

	public String getLhs() {
		return lhs;
	}

	public Object getRhs() {
		return rhs;
	}

	public boolean is_negated() {
		return is_negated;
	}

	public boolean is_numeric() {
		return is_numeric;
	}

	public boolean is_literal_term() {
		return is_literal_term;
	}

	public boolean is_case_sensitive() {
		return is_case_sensitive;
	}

	public boolean is_cidr_match() {
		return is_cidr_match;
	}
}
