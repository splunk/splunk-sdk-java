package com.splunk.erp.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.splunk.erp.commons.JsonUtil;
import com.splunk.erp.exception.ERPException;
import com.splunk.erp.commons.ERPLogger;
import com.splunk.erp.writer.ResultWriter;

public class ERPMain {
	
	private static Provider getProviderInstance(String providerImplClassName) throws ERPException
	{
		try{
			Class<? extends Provider> providerClass = (Class<? extends Provider>)Class.forName(providerImplClassName);
			Provider provider = providerClass.newInstance(); 
			return provider;
		}catch(ClassNotFoundException cnfe){
			ERPLogger.logError("Unable to instantiate Provider object");
			throw new ERPException("Unable to instantiate Provider object");
		}catch(InstantiationException ie){
			ERPLogger.logError("Unable to instantiate Provider object");
			throw new ERPException("Unable to instantiate Provider object");
		}catch(IllegalAccessException iae){
			ERPLogger.logError("Unable to instantiate Provider object due to lack of permission");
			throw new ERPException("Unable to instantiate Provider object due to lack of permission");
		}
	}
	
	private static String getHostName()
	{
		String tempHostName = "localhost";
		try
		{
			tempHostName = InetAddress.getLocalHost().getHostName();
		}catch(UnknownHostException uhe)
		{
			ERPLogger.logInfo("Could not get HostName, defaulting to IP address");
			try {
				tempHostName = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException unknownhHost) {
				ERPLogger.logInfo("Could not get HostName, defaulting localhost");
			}
		}
		return tempHostName;
	}
	
	public static void main(String[] args) {
		try
		{
			JsonNode argsForERP = JsonUtil.createJsonNodeFromStream(System.in);
			JsonNode providerConfigNode = JsonUtil.getProviderConfigNode(argsForERP);
			JsonNode vixesConfigNode = JsonUtil.getVixesConfigNode(argsForERP);
			
			ProviderConfig providerConf = CoreUtil.getProviderConfig(providerConfigNode);
			VixConfig[] vixesConfig = CoreUtil.getVixesConfig(providerConf.getFamilyName(), vixesConfigNode);
			
			String providerImplClassName = providerConf.getProviderImplClassName();
			Provider provider = getProviderInstance(providerImplClassName);
			ResultWriter resultWriter = new ResultWriter(new StreamHeader());
			
			provider.init(providerConf, vixesConfig);
			
			int batchSize = resultWriter.getCurrentBufferSize();
			List<String> batchOfResults = null;
			Map<String,Object> updatedHeaderFields = new HashMap<String,Object>();
			
			while((batchOfResults = provider.getNextBatch(batchSize,updatedHeaderFields)) != null){
				if(updatedHeaderFields.size() > 0) {
					resultWriter.updateHeader(updatedHeaderFields);
				}
				resultWriter.appendBatch(batchOfResults);
				batchSize = resultWriter.getCurrentBufferSize();
			}
			resultWriter.close();
			provider.close();
		} catch (ERPException e){
			ERPLogger.logError(e.getMessage());
		} catch (IOException e) {
			ERPLogger.logError("Error while closing Provider instance");
		}
	}
}
