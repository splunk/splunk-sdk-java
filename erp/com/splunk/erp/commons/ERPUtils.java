package com.splunk.erp.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.splunk.erp.core.IProvider;
import com.splunk.util.WildcardList;

/**
 * Helper class for ERP framework. Contains method for parsing JsonNode and extract particular nodes for 
 * ProviderConfig, VixConfig, SearchInfo and WildcardList. <br> 
 * get localhost information and create IProvider implementation class object.
 * @author smetkar
 *
 */
public class ERPUtils {

	/**
	 * Read System.in input stream for JSON data and convert into JsonNode object
	 * @param System.in (InputStream)
	 * @return JsonNode 
	 * @throws IOException
	 */
	public static JsonNode createJsonNodeFromStream(InputStream streamToRead) throws Exception{
		JsonNode argsAsJson = null;
		String argsLine = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(streamToRead));
		argsLine = reader.readLine();
		reader.close();

		ObjectMapper objectMapper = new ObjectMapper();
		argsAsJson = objectMapper.readTree(argsLine);
		
		if(argsAsJson == null)
			throw new Exception("S-2-ERP protocol JSON was found to be null");
		
		return argsAsJson;
	}

	/**
	 * Read value of JSON element as String 
	 * @param JsonNode node
	 * @param String field to be read
	 * @return String field value
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
	 * @param JsonNode : S-2-ERP protocol JSON string
	 * @return JsonNode : JSON element containing Virtual Index/Indexes configuration
	 */
	public static JsonNode getVixesConfigNode(JsonNode argsAsJson) throws IllegalArgumentException {
		JsonNode indexNode = argsAsJson.get("conf").get("indexes");
		if(indexNode == null || !(indexNode.size() > 0)) {
			throw new IllegalArgumentException("Unable to find indexes element in S-2-ERP protocol JSON");
		}
        return indexNode;
    }
	
	/**
	 * Get provider family name
	 * @param JsonNode : JSON element containing family name
	 * @return Provider family name as String
	 */
	public static String getFamilyName(JsonNode providerConfigNode){
		String familyName = getStringParameter(providerConfigNode, "family"); 
		return familyName;
	}
	
	/**
	 * Get provider config node from JSON 
	 * @param JsonNode : S-2-ERP protocol JSON string
	 * @return JsonNode : JSON element containing Provider configuration
	 * @throws IllegalArgumentException
	 */
	public static JsonNode getProviderConfigNode(JsonNode argsAsJson) throws IllegalArgumentException
	{
		JsonNode providerNode = argsAsJson.get("conf").get("provider");
		if(providerNode == null) {
			throw new IllegalArgumentException("Unable to find provider element in S-2-ERP protocol JSON");
		}
		return providerNode;
	}
	
//	/**
//	 * Create SearchElement object from search_expr element of S-2-ERP protocol JSON String
//	 * @param JsonNode : search_expr element of JSON
//	 * @return SearchElement object
//	 */
//	public static SearchElement getSearchExpression(JsonNode searchExprNode)
//	{
//		SearchElement element = null;
//		String type =  getStringParameter(searchExprNode, "type");
//		String operator = getStringParameter(searchExprNode, "op");
//		
//		if(type.equalsIgnoreCase("group")) {
//			ArrayList<SearchElement> elementChildren = new ArrayList<SearchElement>();
//			JsonNode children = searchExprNode.get("children");
//			Iterator<JsonNode> childIterator = children.getElements();
//			while(childIterator.hasNext())
//			{
//				SearchElement child = getSearchExpression(childIterator.next());
//				if(child != null)
//					elementChildren.add(child);
//			}
//			element = new SearchGroupElement(operator, elementChildren);
//		} else if(type.equalsIgnoreCase("cmp")) {
//			String rhsValue = searchExprNode.get("rhs").getTextValue();
//			Object rhs = searchExprNode.get("is_numeric").getBooleanValue() ? Double.parseDouble(rhsValue) : rhsValue; 
//			element = new SearchCompareElement( operator, searchExprNode.get("lhs").getTextValue(), 
//				rhs,
//				searchExprNode.get("is_negated").getBooleanValue(), 
//				searchExprNode.get("is_numeric").getBooleanValue(), 
//				searchExprNode.get("is_literal_term").getBooleanValue(),
//				searchExprNode.get("is_case_sensitive").getBooleanValue(), 
//				searchExprNode.get("is_cidr_match").getBooleanValue());
//		}
//		return element;
//	}
	
	/**
	 * Creates an object of WildcardList from S-2-ERP protocol JSON string
	 * @param JsonNode : Json element containing required fields information
	 * @return Required fields list as {@link WildcardList} object
	 */
	public static WildcardList getRequiredFieldList(JsonNode argsAsJson) {
		JsonNode requiredFieldNode = argsAsJson.get("args").get("search").get("required_fields");
		WildcardList list = null;
		if(requiredFieldNode != null) {
			List<String> requiredFieldList = new ArrayList<String>();
			if(requiredFieldNode.isArray()) {
				requiredFieldList = new ArrayList<String>();
				Iterator<JsonNode> it = requiredFieldNode.getElements();
				while(it.hasNext()) {
					requiredFieldList.add(it.next().getTextValue());
				}
			}
			
			if(requiredFieldList.size() > 0) 
				list = new WildcardList(requiredFieldList);
		}
		return list;
	}
	
	/**
	 * Determine host name 
	 * @return String
	 */
	public static String getHostName()
	{
		String tempHostName = "localhost";
		try
		{
			tempHostName = InetAddress.getLocalHost().getHostName();
		}catch(UnknownHostException uhe)
		{
			ERPLogger.logInfo("Could not get host name, defaulting to IP address");
			try {
				tempHostName = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException unknownhHost) {
				ERPLogger.logInfo("Could not get host name, defaulting localhost");
			}
		}
		return tempHostName;
	}
	
	
	/**
	 * Create an instance of class implementing IProvider interface
	 * @param String (class to be instantiated)
	 * @return <? implements IProvider>
	 * @throws Exception
	 */
	public static IProvider getProviderInstance(String implClassName) throws Exception{
		try {
			Class<IProvider> providerImplClass = (Class<IProvider>)Class.forName(implClassName);
			IProvider providerImpl = providerImplClass.newInstance();
			return providerImpl;
		} catch (ClassNotFoundException cnfe) {
			ERPLogger.logError(cnfe.getMessage());
			throw new Exception("Error while instantiating provider object, Message : " + cnfe.getMessage());
		} catch (InstantiationException ine) {
			ERPLogger.logError(ine.getMessage());
			throw new Exception("Error while instantiating provider object, Message : " + ine.getMessage());
		} catch (IllegalArgumentException iae) {
			ERPLogger.logError(iae.getMessage());
			throw new Exception("Error while instantiating provider object, Message : " + iae.getMessage());
		}
	}
}
