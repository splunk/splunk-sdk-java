package com.splunk.erp.core;

public class StreamStats {

	private int numberOfEvents;
	private int numberOfBytes;
	
	public StreamStats() {
		numberOfBytes = 0;
		numberOfEvents = 0;
	}

	public int getNumberOfEvents() {
		return numberOfEvents;
	}

	public void setNumberOfEvents(int numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}

	public int getNumberOfBytes() {
		return numberOfBytes;
	}

	public void setNumberOfBytes(int numberOfBytes) {
		this.numberOfBytes = numberOfBytes;
	}
	
	public void addNumberOfBytes(int dataSize){
		
	}
}
