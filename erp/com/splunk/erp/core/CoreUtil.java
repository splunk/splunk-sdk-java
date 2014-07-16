package com.splunk.erp.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.splunk.erp.commons.JsonUtil;
import com.splunk.erp.exception.ERPException;

public class CoreUtil {

	/**
	 * Get provider specific properties from JSON node
	 * @param familyName
	 * @param providerNode
	 * @return
	 */
	public static ProviderConfig getProviderConfig(JsonNode providerConfigNode) throws ERPException{
		
		String familyName = JsonUtil.getFamilyName(providerConfigNode);
		Map<String,Object> providerConfigMap = new HashMap<String, Object>();
		Iterator<String> fieldIterator = providerConfigNode.getFieldNames();
		
		while(fieldIterator.hasNext()){
			String field = fieldIterator.next();
			if(field.matches("^" + familyName + "\\..*"))
			{
				int index = field.indexOf('.');
				providerConfigMap.put(field.substring(index + 1),providerConfigNode.get(field));
			}
		}
		return new ProviderConfig(familyName, providerConfigMap);
	}
	
	/**
	 * Create virtual indexes configs from JSON data
	 * @param familyName(Provider family name) 
	 * @param indexNode(Json node containing index information)
	 * @return
	 */
	public static VixConfig[] getVixesConfig(String familyName, JsonNode vixesConfigNode)
	{
		VixConfig[] vixesConfig = null;
		Map<String,Object> vixConfigMap = null;
		
		if (vixesConfigNode.isArray()) {
			vixesConfig = new VixConfig[vixesConfigNode.size()];
			Iterator<JsonNode> it = vixesConfigNode.getElements();
			int i = 0;
			while (it.hasNext()) {
				JsonNode vixConfigNode = it.next();
				vixConfigMap = new HashMap<String,Object>();
				Iterator<String> fieldIterator = vixConfigNode.getFieldNames();
				
				while(fieldIterator.hasNext()){
					String field = fieldIterator.next();
					if(field.matches("^" + familyName + "\\..*"))
					{
						int index = field.indexOf('.');
						vixConfigMap.put(field.substring(index + 1),vixConfigNode.get(field));
					}
				}
				
				SearchElement  searchExpression = JsonUtil.getSearchExpression(vixConfigNode.get("search_expr"));
				String vixName = vixConfigNode.get("name").getTextValue(); 
				vixesConfig[i++] = new VixConfig(vixName,vixConfigMap,searchExpression);
			}
		}
		return vixesConfig;
	}
}
