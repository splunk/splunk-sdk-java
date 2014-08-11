package com.splunk.erp.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;

/**
 * 'search_expr' element in S-2-ERP protocol JSON represents the parsed SPL query.
 *  POJO to represent 'search_expr' of type 'group'
 *  Example of 'group' type search_expr
 *  {  type":"group",
 *     "op":"AND",
 *     "children":
 *     			  [
 *     				{
 *     				 "type":"cmp",
 *     				 "op":"=",
 *     				 "lhs":"salary",
 *       			 "rhs":"138967.*",
 *                   "is_negated":false,
 *                   "is_numeric":false,
 *                   "is_literal_term":false,
 *                   "is_case_sensitive":false,
 *                   "is_cidr_match":false
 *                   }
 *                 ]
 * }
 * Valid operators are <code> AND </code> , <code> OR </code>
 * A parsed SPL query can contain multiple 'AND'/'OR' condition and children attribute represents the different search condition.
 * They can be either of type 'cmp' or type 'group'.
 * 
 * @author smetkar
 */

public class SearchGroupElement extends SearchElement{

	public static HashSet<String> VALID_OP = new HashSet<String>();
	
	static {
		VALID_OP.add("AND");
		VALID_OP.add("OR");
	}
	
	protected ArrayList<SearchElement> children;
	
	public SearchGroupElement() {}
	
	public SearchGroupElement(String type,String operator, ArrayList<SearchElement> children) {
		super(type,operator);
		this.children = children;
	}
	
	public ArrayList<SearchElement> getChildren() {
		return children;
	}
	
	/**
	 * Initialize SearchGroupElement from JsonNode arguments
	 * @param JsonNode object
	 */
	public void initFrom(JsonNode groupNode) throws IllegalArgumentException {
		
		if(!VALID_OP.contains(groupNode.get("op").getTextValue())) 
			throw new IllegalArgumentException("Not a valid operator for SearchGroupElement");
		
		super.initFrom(groupNode);
		JsonNode childrenNode = groupNode.get("children");
		if(childrenNode.isArray()) {
			Iterator<JsonNode> childIterator = childrenNode.getElements();
			this.children = new ArrayList<SearchElement>();
			while(childIterator.hasNext())
			{
				JsonNode childNode = childIterator.next();
				SearchElement child = SearchElement.getByType(childNode.get("type").getTextValue());
				child.initFrom(childNode);
				this.children.add(child);
			}
		}
	}
}
