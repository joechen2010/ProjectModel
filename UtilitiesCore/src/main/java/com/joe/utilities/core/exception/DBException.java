package com.joe.utilities.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DBException extends Exception {
	
	private static final long serialVersionUID = 6665535437618262561L;
	protected static Log log = LogFactory.getLog(DBException.class);
	private String errorCode = "UNKNOW_ERROR";
	
	private String errorMessage = null;
	
	public DBException() {
		super();
	}
	
	public DBException(String message, Throwable ex) {
		super(message, ex);
		this.errorMessage=message;
		log.info(message);
	}
	
	public DBException(String message) {
		super(message);
		this.errorMessage=message;
	}
	
	public DBException(Throwable ex) {
		super(ex);
	}
	
	 public String getErrorCode() {
	        return errorCode;
	 }
	
}
