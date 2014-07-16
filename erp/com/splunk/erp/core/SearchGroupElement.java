package com.splunk.erp.core;

import java.util.ArrayList;

public class SearchGroupElement extends SearchElement{

	private ArrayList<SearchElement> children;
	
	public SearchGroupElement(String operator, ArrayList<SearchElement> children) {
		super("group",operator);
		this.setChildren(children);
	}
	
	public ArrayList<SearchElement> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<SearchElement> children) {
		this.children = children;
	}

}
