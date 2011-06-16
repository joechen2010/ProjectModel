 package com.joe.jsf.helper;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import com.joe.utilities.core.util.MessageHandler;
import com.joe.utilities.core.util.ReturnStatus;
import com.joe.utilities.core.util.ReturnStatusItem;

/**
 * This class is used by the presentation tier to facilitate message handling specifically for 
 * and within the JSF environment.
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: Mar 8, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */

public class FacesMessageHandler {
	
	
	/**
	 * This method is used to process Return Status Items that when the result object returned 
	 * from the facade was not a success.  The faces messages are built by using the 
	 * messagehandler to obtain the message detail and summary values from the application message 
	 * bundle.
	 * 
	 * @param returnStatus
	 * @param componentString
	 * @throws RuntimeException
	 */
	public static void processReturnStatusMessages(ReturnStatus returnStatus, String componentString) throws RuntimeException{
		
        // Do nothing with empty status object
        if (returnStatus == null)
            return;
        
		// instantiate array of Return status items
		ReturnStatusItem statusItems[] = returnStatus.getResultStatusItems();
		// iterate through result items and add s to faces context
		for (int i=0; i<statusItems.length;i++) {
			// add faces message
			Severity messageSeverity = null;
			if (statusItems[i].getStatus().equals(ReturnStatus.Status.INFORMATIONAL)) {
			      messageSeverity = FacesMessage.SEVERITY_INFO;
			}
			else if (statusItems[i].getStatus().equals(ReturnStatus.Status.WARNING)) {
			      messageSeverity = FacesMessage.SEVERITY_WARN;
			}
			else {
			      messageSeverity = FacesMessage.SEVERITY_ERROR;
			}
			if(componentString == null)
				addMessage(messageSeverity,statusItems[i].getDefaultMessage());
			else
				addMessage(componentString, statusItems[i].getCode(), statusItems[i].getParameterMap(), messageSeverity);
		}
	}
	
	/**
	 * This method is used to add a single faces message (with severity of WARNING) to the 
	 * faces context.
	 * 
	 * @param messageComponentString
	 * @param messageCode
	 */
	public static void addWarningMessage(String messageComponentString, String messageCode) {
		addMessage(messageComponentString, messageCode, null, FacesMessage.SEVERITY_WARN);

	}
	
	/**
	 * This method is used to add a single faces message (with severity of WARNING) to the 
	 * faces context with parameters.
	 * 
	 * @param messageComponentString
	 * @param messageCode
	 * @param paramMap
	 */
	public static void addWarningMessageWithParams(String messageComponentString, String messageCode, Map<String, String> paramMap) {
		addMessage(messageComponentString, messageCode, paramMap, FacesMessage.SEVERITY_WARN);

	}
	
	/**
	 * This method is used to add a single faces message (with severity of ERROR) to the 
	 * faces context with parameters.
	 * 
	 * @param messageComponentString
	 * @param messageCode
	 * @param paramMap
	 */
	public static void addErrorMessageWithParams(String messageComponentString, String messageCode, Map<String, String> paramMap)
	{
		addMessage(messageComponentString, messageCode, paramMap, FacesMessage.SEVERITY_ERROR);
	}
	
	/**
	 * This method is used to add a single faces message (with severity of INFORMATION) to the 
	 * faces context.
	 * 
	 * @param messageComponentString
	 * @param messageCode
	 */
	public static void addInformationMessage(String messageComponentString, String messageCode) {
		addMessage(messageComponentString, messageCode, null, FacesMessage.SEVERITY_INFO);
	}
	
	/**
	 * This method is used to add a single faces message (with severity of INFORMATION) to the 
	 * faces context with parameters.
	 * 
	 * @param messageComponentString
	 * @param messageCode
	 * @param paramMap
	 */
	public static void addInformationMessageWithParams(String messageComponentString, String messageCode, Map<String, String> paramMap) {
		addMessage(messageComponentString, messageCode, paramMap, FacesMessage.SEVERITY_INFO);
	}
	
	/**
	 * This method is used to add a single faces message (with severity of ERROR) to the 
	 * faces context.
	 * 
	 * @param messageComponentString
	 * @param messageCode
	 */
	public static void addErrorMessage(String messageComponentString, String messageCode) {
		addMessage(messageComponentString, messageCode, null, FacesMessage.SEVERITY_ERROR);
	}
	
	/**
	 * @deprecated DEVELOPERS SHOULD NOT ALLOW CALLS TO THIS METHOD TO REACH PRODUCTION.
	 * 				USE addErrorMessage(String,String) INSTEAD.
	 * 
	 * This method provides a temporary way of adding a faces message to the context.  This method 
	 * merely uses the passed "message" directly as both the message detail and summary, and does 
	 * not obtain anything from the message bundle via the messagehandler.
	 * 
	 * @param message
	 */
	@Deprecated public static void addErrorMessage(String message) {
		// get faces context instance
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// create faces message based on component string and message code
		FacesMessage facesMessage = new FacesMessage(message,message);
		facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		/* TODO figure out what first parameter should be (CLIENT IDENTIFIER) */
		facesContext.addMessage(null, facesMessage);
	}
	
	/**
	 * Returns if the Faces Context getMessages has any messages defined.
	 * @return boolean
	 */
	public static boolean hasMessages()
	{	// if a message exists in the faces messages iterator return true 
		if (FacesContext.getCurrentInstance().getMessages().hasNext()) {
			return true;
		}
		else return false;
	}
	
	
	/**
	 * This method obtains the faces context, creates the faces message, obtains the message 
	 * detail and summary values from MessageHandler, sets the severity, and adds the faces
	 * message to the faces context.
	 * 
	 * @param messageComponentString
	 * @param messageCode
	 * @param paramMap
	 * @param messageSeverity
	 */
	private static void addMessage(String messageComponentString, String messageCode, Map<String, String> paramMap, Severity messageSeverity) {
		// get faces context instance
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// create faces message based on component string and message code
		FacesMessage facesMessage = new FacesMessage();
		
		if (paramMap==null || paramMap.isEmpty()) {
			// if paramMap is empty, get detail message without params
			facesMessage.setDetail(MessageHandler.getMessageValueDetail(messageComponentString, messageCode));
		}
		else {
			// get message with params
			facesMessage.setDetail(MessageHandler.getMessageValueDetail(messageComponentString,messageCode,paramMap));
		}
		facesMessage.setSummary(MessageHandler.getMessageValueSummary(messageComponentString, messageCode));
		facesMessage.setSeverity(messageSeverity);
		/* TODO figure out what first parameter should be (CLIENT IDENTIFIER) */
		facesContext.addMessage(null, facesMessage);
	}
	private static void addMessage(Severity messageSeverity, String defaultMessage) {
		// get faces context instance
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// create faces message based on component string and message code
		FacesMessage facesMessage = new FacesMessage();
		facesMessage.setDetail(defaultMessage);
		facesMessage.setSeverity(messageSeverity);
		/* TODO figure out what first parameter should be (CLIENT IDENTIFIER) */
		facesContext.addMessage(null, facesMessage);
	}

}
