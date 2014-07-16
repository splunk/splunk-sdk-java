package com.splunk.erp.core;

import java.util.Map;

public class VixConfig {
 	
	private String indexName;
	private Map<String, Object> configMap;
	private SearchElement searchExpression;
	
	public VixConfig(String indexName, Map<String,Object> configMap,SearchElement searchExpression) {
		this.setIndexName(indexName);
		this.configMap = configMap;
		this.searchExpression = searchExpression;
	}
	
	public SearchElement getSearchExpression() {
		return searchExpression;
	}

	public void setSearchExpression(SearchElement searchExpression) {
		this.searchExpression = searchExpression;
	}

	public Map<String, Object> getConfigMap() {
		return configMap;
	}

	public void setConfigMap(Map<String, Object> configMap) {
		this.configMap = configMap;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
}
