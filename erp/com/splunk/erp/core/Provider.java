package com.splunk.erp.core;

import java.io.Closeable;
import java.lang.reflect.Constructor;

import org.codehaus.jackson.JsonNode;

import com.splunk.erp.commons.ERPLogger;
import com.splunk.erp.commons.ERPUtils;
import com.splunk.util.WildcardList;

//TODO Keeping this class as we have not decided whether Provider should be an interface or an abstract class

public abstract class Provider implements Closeable{
	
	private ProviderConfig providerConfig;
	private VixConfig[] vixesConfig;

	public Provider(ProviderConfig providerConf, VixConfig[] vixesConf) {
		this.providerConfig = providerConf;
		this.vixesConfig = vixesConf;
	}
	
	public ProviderConfig getProviderConfig() {
		return providerConfig;
	}

	public VixConfig[] getVixesConfig() {
		return vixesConfig;
	}
	
	protected void setProviderConfig(ProviderConfig providerConf) {
		this.providerConfig = providerConf;
	}
	
	protected void setVixesConfig(VixConfig[] vixesConf) {
		this.vixesConfig = vixesConf;
	}

	public abstract void init(SearchInfo searchInfo, WildcardList requiredFieldList) throws Exception;
	public abstract String getNextRecord(FieldAppender fieldAppenderImpl) throws Exception;

	public static Provider getProviderInstance(JsonNode argsForERP) {
		try
		{
			JsonNode providerConfigNode = ERPUtils.getProviderConfigNode(argsForERP);
			JsonNode vixesConfigNode = ERPUtils.getVixesConfigNode(argsForERP);		
			
			ProviderConfig providerConf = ProviderConfig.getProviderConfigInstance(providerConfigNode);
			VixConfig[] vixesConf = VixConfig.getVixesConfig(vixesConfigNode);
			
			String className = providerConf.getProviderImplClassName();
			Class<? extends Provider> providerClass = (Class<? extends Provider>)Class.forName(className);
			Constructor<? extends Provider> constructor = providerClass.getConstructor(new Class[] {ProviderConfig.class, VixConfig.class});
			Provider provider = constructor.newInstance(new Object[] {providerConf,vixesConf});
			return provider;
			
		} catch (Exception ex) {
			ERPLogger.logError("Error while instantiating Provider implementation class, Message :" + ex.getMessage());
			throw new RuntimeException("Error while instantiating Provider implementation class, Message :" + ex.getMessage());
		}
	}
	
	public void run() throws Exception 
	{
		ResultWriter resultWriter = new ResultWriter(ERPUtils.getHostName());
		try{
			JsonNode argsForERP = ERPUtils.createJsonNodeFromStream(System.in);
			JsonNode providerConfigNode = ERPUtils.getProviderConfigNode(argsForERP);	
			JsonNode vixesConfigNode = ERPUtils.getVixesConfigNode(argsForERP);		
			
			ProviderConfig providerConf = ProviderConfig.getProviderConfigInstance(providerConfigNode);
			VixConfig[] vixesConf = VixConfig.getVixesConfig(vixesConfigNode);
			
			this.setProviderConfig(providerConf);
			this.setVixesConfig(vixesConf);
			
			SearchInfo searchInfo = SearchInfo.getSearchInfoInstance(argsForERP);
			WildcardList requiredFieldList = ERPUtils.getRequiredFieldList(argsForERP);
			
			ResultWriterProxy resultWriterProxy = resultWriter;
			
			this.init(searchInfo,requiredFieldList);

			String result = null;
			while((result = this.getNextRecord(resultWriterProxy)) != null){
				resultWriter.append(result);
			}
			
			this.close();
			
		} catch (Exception ex) {
			ERPLogger.logError("Error while executing ERPMain process - " + ex.getMessage());
		} finally {
			resultWriter.close();
		}
	}
}
