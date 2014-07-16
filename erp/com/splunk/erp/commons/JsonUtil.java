package com.splunk.erp.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.PatternSyntaxException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.splunk.erp.core.SearchCompareElement;
import com.splunk.erp.core.SearchElement;
import com.splunk.erp.core.SearchGroupElement;
import com.splunk.erp.exception.ERPException;

public class JsonUtil {

	public static final int FIRST_ELEMENT = 0;
	public static final String CLASS_NAME_PROPERTY = "class.name";
	
	/**
	 * Read System.in input stream for JSON data and convert into JsonNode object
	 * @param System.in (InputStream)
	 * @return
	 * @throws IOException
	 */
	public static JsonNode createJsonNodeFromStream(InputStream streamRead)	throws ERPException {
		JsonNode argsInJson = null;
		String argsLine = null;
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(streamRead));
			argsLine = reader.readLine();
			reader.close();
	
			ObjectMapper objectMapper = new ObjectMapper();
			argsInJson = objectMapper.readTree(argsLine);
			return argsInJson;
		} catch(IOException ioe) {
			throw new ERPException("Error while reading System input stream");
		}
	}

	/**
	 * Read value of JSON element as String 
	 * @param JsonNode node
	 * @param Field to be read
	 * @return
	 */
	public static String getStringParameter(JsonNode node, String param) {
		JsonNode indexNameJsonNode = node.get(param);
		if (indexNameJsonNode != null) {
			return indexNameJsonNode.getValueAsText();
		}
		return null;
	}
	
	/**
	 * Get virtual indexes config node from JSON data 
	 * @param argsAsJson
	 * @return
	 */
	public static JsonNode getVixesConfigNode(JsonNode argsAsJson) {
		JsonNode indexNode = null;
        if (argsAsJson != null) {
        	indexNode = argsAsJson.get("conf").get("indexes");
        }
        return indexNode;
    }
	
	/**
	 * Get provider family name
	 * @param JsonNode
	 * @return
	 */
	public static String getFamilyName(JsonNode providerConfigNode) throws ERPException
	{
		String familyName = "";
		try{
			familyName = getStringParameter(providerConfigNode, "family").split("_")[FIRST_ELEMENT]; 
		}catch(PatternSyntaxException pse){
			throw new ERPException("Unable to determine provider family name");
		}
		return familyName;
	}
	
	/**
	 * Get provider config node from JSON data
	 * @param argsAsJson
	 * @return
	 */
	public static JsonNode getProviderConfigNode(JsonNode argsAsJson)
	{
		JsonNode providerNode = null;
		if(argsAsJson != null)
		{
			providerNode = argsAsJson.get("conf").get("provider");
		}
		return providerNode;
	}
	
	/**
	 * Create SearchElement object from search_expr element of JSON data
	 */
	public static SearchElement getSearchExpression(JsonNode searchExprNode)
	{
		SearchElement element = null;
		String type =  getStringParameter(searchExprNode, "type");
		String operator = getStringParameter(searchExprNode, "op");
		
		switch(type)
		{
			case "group":
						ArrayList<SearchElement> elementChildren = new ArrayList<SearchElement>();
						JsonNode children = searchExprNode.get("children");
						Iterator<JsonNode> childIterator = children.getElements();
						while(childIterator.hasNext())
						{
							SearchElement child = getSearchExpression(childIterator.next());
							if(child != null)
								elementChildren.add(child);
						}
						element = new SearchGroupElement(operator, elementChildren);
						break;
			case "cmp":
						String rhsValue = searchExprNode.get("rhs").getTextValue();
						Object rhs = searchExprNode.get("is_numeric").getBooleanValue() ? Double.parseDouble(rhsValue) : rhsValue; 
						element = new SearchCompareElement( operator, searchExprNode.get("lhs").getTextValue(), 
							rhs,
							searchExprNode.get("is_negated").getBooleanValue(), 
							searchExprNode.get("is_numeric").getBooleanValue(), 
							searchExprNode.get("is_literal_term").getBooleanValue(),
							searchExprNode.get("is_case_sensitive").getBooleanValue(), 
							searchExprNode.get("is_cidr_match").getBooleanValue());
						break;
		}
		return element;
	}
}
