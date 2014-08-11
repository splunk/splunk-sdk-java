package com.splunk.erp.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

/**
 * POJO to represent virtual index specific properties.
 * Virtual index specific parameters are stored in Map
 * The 'search_expr' element of S-2-ERP protocol JSON is reprsented by {@link SearchElement} object.
 * 
 * @author smetkar
 */
public class VixConfig {
 	
	private String name;
	private Map<String, String> configParams;
	private String provider;
	//TODO Decide on whether we want to use subclass of ParseNode or subclass of SearchElement
	private SearchElement searchExpression;
	
	public VixConfig(String indexName,String provider, Map<String,String> configParams,SearchElement searchExpression) {
		this.name = indexName;
		this.provider = provider;
		this.configParams = configParams;
		this.searchExpression = searchExpression;
	}
	
	public SearchElement getSearchExpression() {
		return searchExpression;
	}

	public Map<String, String> getConfigParams() {
		return Collections.unmodifiableMap(configParams);
	}

	public String getIndexName() {
		return name;
	}
	
	public String getProviderName() {
		return provider;
	}
	
	/**
	 * Create virtual indexes configs from S-2-ERP protocol JSON 
	 * @param familyName(Provider family name) 
	 * @param indexNode(Json node containing index information)
	 * @return VixConfig array
	 */
	public static VixConfig[] getVixesConfig(JsonNode vixesConfigNode)
	{
		VixConfig[] vixesConfig = null;
		Map<String,String> params = null;
		
		vixesConfig = new VixConfig[vixesConfigNode.size()];
		Iterator<JsonNode> it = vixesConfigNode.getElements();
		int vixCount = 0;
		while (it.hasNext()) {
			JsonNode vixConfigNode = it.next();
			params = new HashMap<String,String>();
			Iterator<String> fieldIterator = vixConfigNode.getFieldNames();
			
			while(fieldIterator.hasNext()){
				String field = fieldIterator.next();
				params.put(field,vixConfigNode.get(field).getTextValue());
			}
			
			//Represent parsed SPL query as SearchElement 
			JsonNode searchExprNode = vixConfigNode.get("search_expr");
			SearchElement searchExpression = SearchElement.getByType(searchExprNode.get("type").getTextValue());
			searchExpression.initFrom(searchExprNode);
			String vixName = vixConfigNode.get("name").getTextValue(); 
			String providerName = vixConfigNode.get("provider").getTextValue();
			vixesConfig[vixCount++] = new VixConfig(vixName, providerName, params, searchExpression);
		}
		return vixesConfig;
	}
}
