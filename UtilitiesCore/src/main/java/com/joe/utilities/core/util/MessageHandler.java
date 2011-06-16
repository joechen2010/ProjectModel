package com.joe.utilities.core.util;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.NoSuchMessageException;

import com.joe.utilities.core.serviceLocator.ServiceLocator;


/**
 * This class is used to translate exception and error codes from the business
 * layer.  
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: Feb 5, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */


public class MessageHandler {

	static Log log = LogFactory.getLog(MessageHandler.class);
	
	/**
	 * Method will obtain the summary message value from the bundle
	 * 
	 * @param componentName
	 * @param errorCode
	 * @return
	 */
	public static String getMessageValueSummary(String componentName, String errorCode) {
		
		return getMessageValue(componentName, errorCode,null);
		
	}
	
	/**
	 * Method will obtain the summary message value from the bundle
	 * 
	 * @param componentName
	 * @param errorCode
	 * @return
	 */
	public static String getMessageValueDetail(String componentName, String errorCode) {
		
		return getMessageValue(componentName, errorCode+"_detail",null);
		
	}
	
	/**
	 * Method will concatenate the component name and the error code in order 
	 * to generate the name part of a name-value pair.  The service locator 
	 * will be called using this name to obtain the appropriate value from 
	 * the message bundle.  The param keys will be ordered appropriately via 
	 * a call to the message-translator bundle and used to pass the appropriate 
	 * parameters into the parameterized message value.
	 * 
	 * @param componentName
	 * @param errorCode
	 * @param paramMap
	 * @return
	 */
	public static String getMessageValueDetail(String componentName,String errorCode, Map<String, String> paramMap) throws RuntimeException{

		if (paramMap.isEmpty()) throw new RuntimeException("message parameter map cannot be empty");
		// build full error key (combination of page origination and errorcode)	
		String[] orderedParms = translateMessageCodeParametersIntoStringArray(errorCode, paramMap);
		return getMessageValue(componentName, errorCode+"_detail", orderedParms);
	}
	
	/**
	 * Method will concatenate the component name and the error code in order 
	 * to generate the name part of a name-value pair.  The service locator 
	 * will be called using this name to obtain the appropriate value from 
	 * the message bundle 
	 * 
	 * @param componentName
	 * @param errorCode
	 * @param paramMap
	 * @return
	 */
	private static String getMessageValue(String componentName, String errorCode, String[] orderedParms) {
			// build full error key (combination of page origination and errorcode)
			String fullErrorKey = componentName.concat(".").concat(errorCode);
			String messageReturned = null;
			try {
				// use servicelocator to get messagevalue
				if (orderedParms!=null && orderedParms.length>0) {
					
					messageReturned = ServiceLocator.getInstance().getMessage(fullErrorKey, orderedParms);
				}
				else {

					messageReturned = ServiceLocator.getInstance().getMessage(fullErrorKey);
				}
			}
			catch (NoSuchMessageException noMessEx) {
				messageReturned = "No Message found in bundle for code '" + fullErrorKey + "'";
			}
			return messageReturned;
			
	}
		

	private static String[] translateMessageCodeParametersIntoStringArray(String errorCode, Map<String, String> paramMap) {
		ResourceBundle bundle = ResourceBundle.getBundle("message-translator");
		// get ordered string value for given error code key
		String value = bundle.getString(errorCode);
		// tokenize the ordered key value string
		StringTokenizer strToken = new StringTokenizer(value,",");
		int nextArrayNum = 0;
		// instantiate string array of parameters that will be used to build parameterized message
		String[] orderedParms = new String[strToken.countTokens()];
		// iterate through tokens and build string array 
		while (strToken.hasMoreTokens()) {
			orderedParms[nextArrayNum] = paramMap.get(strToken.nextToken());
			nextArrayNum++;
		}
		return orderedParms;
	}
	
	
}
