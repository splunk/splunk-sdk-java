package com.splunk.erp.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.splunk.erp.commons.Constants.*;

import com.splunk.erp.commons.ERPLogger;
import com.splunk.erp.core.StreamHeader;
import com.splunk.erp.exception.ERPException;
import com.splunk.io.ChunkedOutputStream;
import com.splunk.io.SearchOutputStream;

public class ResultWriter {
	
	private ByteArrayOutputStream eventsCollector;
	private int bufferFlushSize;
	private StreamHeader streamHeader;
	private SearchOutputStream outputStream;
	
	public ResultWriter(StreamHeader streamHeader) {
		eventsCollector = new ByteArrayOutputStream(OUTPUT_BUFFER_LIMIT);
		bufferFlushSize = INITIAL_BUFFER_FLUSH_SIZE;
		ChunkedOutputStream cos = new ChunkedOutputStream(System.out);
		outputStream = new SearchOutputStream(cos); 
		outputStream.setStreamType("raw");
		this.streamHeader = streamHeader;
	}
	
	private void stream()
	{
		try {
			outputStream.write(eventsCollector);
			eventsCollector.reset();
		} catch (IOException e) {
			ERPLogger.logError("Error while streaming results to Splunk");
		}
	}
	
	public void append(String result) 
	{
		try {
			eventsCollector.write(result.getBytes());
			eventsCollector.write(NEWLINE.getBytes());
			if(eventsCollector.size() > bufferFlushSize)
			{
				stream();
				bufferFlushSize *= 2;
				if(bufferFlushSize > MAX_BUFFER_FLUSH_SIZE)
					bufferFlushSize = MAX_BUFFER_FLUSH_SIZE;
				
			}
		} catch (IOException e) {
			ERPLogger.logError("Error writing to event collector");
		}
	}
	
	public void appendBatch(List<String> batchOfResults) {
		for(String result : batchOfResults) {
			append(result);
		}
	}
	
	public boolean updateHeader(Map<String,Object> headerFields)
	{
		boolean isNewHeader = false;
		Iterator mapIterator = headerFields.entrySet().iterator();
		while(mapIterator.hasNext())
		{
			Entry<String,Object> entry = (Map.Entry<String, Object>) mapIterator.next();
			if(streamHeader.updateHeaderField(entry.getKey(),entry.getValue()))
			{
				isNewHeader = true;
			}
		}
		return isNewHeader;
	}

	public void close() throws ERPException
	{
		if(eventsCollector.size() > 0)
		{
			stream();
			try
			{
				outputStream.flush();
			}catch(IOException ioe)
			{
				ERPLogger.logError("Error flushing while closing stream");
				throw new ERPException("Error flushing while closing stream");
			}
		}
	}
	
	public int getCurrentBufferSize() {
		return this.bufferFlushSize;
	}
}
