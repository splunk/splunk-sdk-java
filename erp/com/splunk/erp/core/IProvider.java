package com.splunk.erp.core;

import java.io.Closeable;

import com.splunk.util.WildcardList;

/**
 * Interface that an External Resource Provider(ERP) developer needs to implement 
 * in order to analyse the data stored in external resource (eg. NoSQL database  
 * like MongoDB, Cassandra etc.)  using Hunk.  
 * @author smetkar
 *
 */
public interface IProvider extends Closeable{
	
	/**
	 * An External Resource Provider (ERP) developer implementing this method is expected to 
	 * implement following steps: 
	 * 
	 * 1. Responsible for handling the request from a Splunk/Hunk search, parsing the arguments 
	 *    and setting up any necessary resource (connections, files etc).
	 * 2. Transform the SPL query into a query which is specific to the database/datastore.
	 * 3. Make use of {@link SearchInfo} and {@link WildcardList} while building query
	 * 
	 * The method calling <em> init </em> method assumes that the ERP is ready for returning results after calling this method.
	 *  
	 * @param {@link ProviderConfig} Provider specific properties 
	 * @param {@link VixConfig} Virtual index specific properties
	 * @param {@link SearchInfo} Search specific information 
	 * @param {@link WildcardList} Required field list as WildcardList object
	 * @throws Exception : Any exception raised during initialization 
	 */
	public void init(ProviderConfig providerConf, VixConfig[] vixesConf,
			SearchInfo searchInfo, WildcardList requiredFieldList)
			throws Exception;

	/**
	 *  An External Resource Provider (ERP) implementing this method is expected to 
	 *  implement following steps:
	 *  
	 *  1. If SPL query consist  of multiple virtual indexes, ERP developers should handle the fetching
	 *     of results from each virtual index.
	 *  2. Initialize result writer object of ERP framework by setting index, source and sourcetype and 
	 *     other header fields using proxy.    
	 *  2. Handle the batching of results/records and pushing the batch of records/events as List<?> to the proxy. 
	 *  3. Filter the white listed fields, specified using {@link WildcardList}, post querying database/datastore.   
	 *    
	 * @param {@link ResultWriterProxy} Proxy to ResultWriter object.
	 * @throws Exception : Any exception raised while querying ERP and fetching results.
	 */
	public void run(ResultWriterProxy resultWriterProxy) throws Exception;
}
