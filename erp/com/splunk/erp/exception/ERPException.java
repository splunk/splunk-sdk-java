package com.splunk.erp.exception;

/**
 * ERP specific exception class
 * @author smetkar
 *
 */
public class ERPException extends Exception{

	private static final long serialVersionUID = 1L;

	public ERPException(String message) {
		super(message);
	}
}
