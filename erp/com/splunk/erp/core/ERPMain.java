package com.splunk.erp.core;

import org.codehaus.jackson.JsonNode;

import com.splunk.erp.commons.ERPUtils;
import com.splunk.erp.commons.ERPLogger;
import com.splunk.util.WildcardList;

/**
 * This class defines the method for initiating the search from ERP. 
 * Script <em> erp_script.sh </em> executes the java process with {@link ERPMain} 
 * as the main class. 
 * @author smetkar
 */

public class ERPMain {
		
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
		ResultWriter resultWriter = new ResultWriter(ERPUtils.getHostName());
		
		try{
			/* S-2-ERP protocol JSON is passed to ERPMain process using System console
			 * Create JsonNode object from JSON string and create instances of ProviderConfig, VixConfig,
			 * SearchInfo and WildcardList			
			 */
			JsonNode argsForERP = ERPUtils.readArgsForERP(System.in);
			IProvider provider = ERPUtils.getProviderInstance(className);
			
			JsonNode providerConfigNode = ERPUtils.getProviderConfigNode(argsForERP);	
			JsonNode vixesConfigNode = ERPUtils.getVixesConfigNode(argsForERP);		
			
			ProviderConfig providerConf = ProviderConfig.getProviderConfigInstance(providerConfigNode);
			VixConfig[] vixesConf = VixConfig.getVixesConfig(vixesConfigNode);
			
			SearchInfo searchInfo = SearchInfo.getSearchInfoInstance(argsForERP);
			WildcardList requiredFieldList = ERPUtils.getRequiredFieldList(argsForERP);
			
			//Pass down the proxy for ResultWriter to IProvider implementing class
			ResultWriterProxy resultWriterProxy = resultWriter;
			
			//Make the ERP ready for fetching results
			provider.init(providerConf, vixesConf, searchInfo, requiredFieldList);
			//Start fetching the results/records
			provider.run(resultWriterProxy);
			//Close the ERP when results/record fetching is completed. 
			provider.close();
			
		} catch (Exception ex) {
			ERPLogger.logError("Error while executing ERPMain process - " + ex.getMessage());
		} finally {
			resultWriter.close();
		}
	}
}
