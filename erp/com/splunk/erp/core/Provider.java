package com.splunk.erp.core;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import com.splunk.erp.exception.ERPException;

public interface Provider extends Closeable{
	public void init(ProviderConfig providerConf,VixConfig[] vixesConf) throws ERPException;
	public List<String> getNextBatch(int batchSize,Map<String,Object> updatedFields) throws ERPException;
}
