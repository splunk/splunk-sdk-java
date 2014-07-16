package com.splunk.erp.core;

public class SearchCompareElement extends SearchElement{

	private String lhs;
	private Object rhs;
	private boolean is_negated;
	private boolean is_numeric;
	private boolean is_literal_term;
	private boolean is_case_sensitive;
	private boolean is_cidr_match;
	
	public SearchCompareElement(String operator) {
		super("cmp", operator);
	}

	public SearchCompareElement(String operator, String lhs, Object rhs, boolean is_negated,
			boolean is_numeric, boolean is_literal_term,
			boolean is_case_sensitive, boolean is_cidr_match) {
		super("cmp",operator);
		this.lhs = lhs;
		this.rhs = rhs;
		this.is_negated = is_negated;
		this.is_numeric = is_numeric;
		this.is_literal_term = is_literal_term;
		this.is_case_sensitive = is_case_sensitive;
		this.is_cidr_match = is_cidr_match;
	}

	public String getLhs() {
		return lhs;
	}

	public void setLhs(String lhs) {
		this.lhs = lhs;
	}

	public Object getRhs() {
		return rhs;
	}

	public void setRhs(Object rhs) {
		this.rhs = rhs;
	}

	public boolean is_negated() {
		return is_negated;
	}

	public void set_is_negated(boolean is_negated) {
		this.is_negated = is_negated;
	}

	public boolean is_numeric() {
		return is_numeric;
	}

	public void set_is_numeric(boolean is_numeric) {
		this.is_numeric = is_numeric;
	}

	public boolean is_literal_term() {
		return is_literal_term;
	}

	public void set_is_literal_term(boolean is_literal_term) {
		this.is_literal_term = is_literal_term;
	}

	public boolean is_case_sensitive() {
		return is_case_sensitive;
	}

	public void set_is_case_sensitive(boolean is_case_sensitive) {
		this.is_case_sensitive = is_case_sensitive;
	}

	public boolean is_cidr_match() {
		return is_cidr_match;
	}

	public void set_is_cidr_match(boolean is_cidr_match) {
		this.is_cidr_match = is_cidr_match;
	}
}
