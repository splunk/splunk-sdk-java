package com.splunk.erp.core;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import com.splunk.erp.commons.ERPUtils;
import com.splunk.util.WildcardList;

/**
 * This class defines the method for initiating the search from ERP. 
 * Script <em> erp_script.sh </em> executes the java process with {@link ERPMain} 
 * as the main class. 
 * @author smetkar
 */

public class ERPMain {
	
	private static Logger logger = ERPUtils.getLogger(ERPMain.class);
	
//	public static void main(String[] args) throws Exception 
//	{
//		ResultWriter resultWriter = new ResultWriter(ERPUtils.getHostName());
//		
//		try{
//			JsonNode argsForERP = ERPUtils.createJsonNodeFromStream(System.in);
//			Provider provider = Provider.getProviderInstance(argsForERP);
//			
//			SearchInfo searchInfo = SearchInfo.getSearchInfoInstance(argsForERP);
//			WildcardList requiredFieldList = ERPUtils.getRequiredFieldList(argsForERP);
//			
//			FieldAppender resultWriterProxy = resultWriter;
//			
//			provider.init(searchInfo,requiredFieldList);
//
//			String record = "";			
//			while((record = provider.getNextRecord(resultWriterProxy)) != null){
//				resultWriter.append(record);
//			}
//			
//			provider.close();
//		} catch (Exception ex) {
//			ERPLogger.logError("Error while executing ERPMain process - " + ex.getMessage());
//		} finally {
//			resultWriter.close();
//		}
//	}
	
	/**
	 * Splunkd process invokes this method to initiate the search process for ERP.
	 *  
	 * 1. It is responsible for creating instances of {@link ProviderConfig}, {@link VixConfig}, 
	 * {@link SearchInfo} and {@link WildcardList} required for further processing of search request.
	 * 2. It controls the flow of search from ERP.
	 * 
	 * The name of the {@link IProvider} implementation class is passed as an argument while instantiating 'ERPMain' process. 
	 * @param 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception 
	{
		String className = args[0];
		ResultWriter resultWriter = null;
		logger.info("Starting search process over external resource provider...");
		try{
			/* S-2-ERP protocol JSON is passed to ERPMain process using System console
			 * Create JsonNode object from JSON string and create instances of ProviderConfig, VixConfig,
			 * SearchInfo and WildcardList			
			 */
			logger.info("Reading arguments for search process");
			JsonNode argsForERP = ERPUtils.readArgsForERP(System.in);
			logger.info("Creating provider instance with class name : " + className);
			IProvider provider = ERPUtils.getProviderInstance(className);
			
			JsonNode providerConfigNode = ERPUtils.getProviderConfigNode(argsForERP);	
			JsonNode vixesConfigNode = ERPUtils.getVixesConfigNode(argsForERP);		
			
			logger.info("Obtained provider config");
			ProviderConfig providerConf = ProviderConfig.getProviderConfigInstance(providerConfigNode);
			
			VixConfig[] vixesConf = VixConfig.getVixesConfig(vixesConfigNode);
			logger.info("Obtained virtual indexes config");
			SearchInfo searchInfo = SearchInfo.getSearchInfoInstance(argsForERP);
			logger.info("Obtained search info");
			WildcardList requiredFieldList = ERPUtils.getRequiredFieldList(argsForERP);
			logger.info("Obtained required field list");
			
			logger.info("Creating result writer for provider");
			resultWriter = new ResultWriter(ERPUtils.getHostName());
			
			//Pass down the proxy for ResultWriter to IProvider implementing class
			logger.info("Creating proxy to result writer");
			ResultWriterProxy resultWriterProxy = resultWriter;
			
			logger.info("Initializing provider...");
			//Make the ERP ready for fetching results
			provider.init(providerConf, vixesConf, searchInfo, requiredFieldList);
			//Start fetching the results/records
			logger.info("Start record fetching");
			provider.run(resultWriterProxy);
			//Close the ERP when results/record fetching is completed.
			logger.info("Record fetching complete, closing provider");
			provider.close();
			
		} catch (Exception ex) {
			logger.error("Error executing search process - " + ex.getMessage(),ex);
		} finally {
			logger.info("Closing result writer");
			if(resultWriter != null)
				resultWriter.close();
		}
	}
}
