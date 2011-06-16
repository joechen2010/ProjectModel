package com.joe.jsf.web.view;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joe.jsf.helper.FacesMessageHandler;
import com.joe.utilites.core.session.SessionController;

@ManagedBean(name="MessagesSupportBean")
@SessionScoped
public class MessagesSupportView {
	private final static String MESSAGES_SUPPORT_DO_NOT_TOGGLE_KEY = "MESSAGES_SUPPORT_DO_NOT_TOGGLE";
	private static Log log = LogFactory.getLog(MessagesSupportView.class);
	private boolean windowMaximized = true;
	
	private boolean error = false;
	private boolean warning = false;
	private boolean info = false;

	private String message;
	
	Iterator<FacesMessage> facesMessages;
	
	private ArrayList<String> errorList=null;
	private ArrayList<String> warningList=null;
	private ArrayList<String> infoList=null;

	public MessagesSupportView() {

	}

	public String simpleMaximizeWindow() {
	    this.windowMaximized  = true;
	    addOldMessagesToFacesContext();
        return null;
	}
	
	public String simpleMinimizeWindow() {
	    this.windowMaximized = false;
        addOldMessagesToFacesContext();
	    return null;
	}
	
	public void clearMessages() {
	    this.facesMessages = new ArrayList<FacesMessage>(0).iterator();
	}


	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(boolean error) {
		this.error = error;
	}

	/**
	 * @return the warning
	 */
	public boolean isWarning() {
		return warning;
	}

	/**
	 * @param warning the warning to set
	 */
	public void setWarning(boolean warning) {
		this.warning = warning;
	}
	

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	/**
	 * Action method used for minimizing the message popup window
	 * @return
	 */
	public String minimizeWindow() {
		windowMaximized=false;
		
		log.debug("MessagesSupportView->minimizeWindow() called");
        
        // add old messages to faces context
		addOldMessagesToFacesContext();
		
		// add session attribute so that windowMaximized attribute is not set to true 
		// 	when faces messages are updated
		SessionController.addSessionAttribute(MESSAGES_SUPPORT_DO_NOT_TOGGLE_KEY, new Boolean(true));
		
		return null;
	}
	
	/**
	 * Action method called to maximize message popup window
	 * @return
	 */
	public String maximizeWindow() {
		windowMaximized=true;
		// add old messages to faces context
		addOldMessagesToFacesContext();
		
		return null;
	}

	/**
	 * Getter method to determine if message popup window should be visible in its 
	 * maximized state
	 * @return
	 */
	public boolean getDisplayWindowMaximized() {
		
		if (windowMaximized && FacesMessageHandler.hasMessages()) {
			getMessagesAndClassify();
			return true;
		}
		else return false;
	
	}
	
	/**
	 * Getter method to determine if message popup window should be visible in its 
	 * minimized state
	 * @return
	 */
	public boolean getDisplayWindowMinimized() {
		if (!windowMaximized && FacesMessageHandler.hasMessages()) {
			return true;
		}
		else return false;
	}
	
	public boolean getIsPopUpRendered()
	{
		System.out.println("this is ridicuolous");
		if(facesMessages != null && facesMessages.hasNext())
			return true;
		else
			return false;
	}
	
	/**
	 * This method will update the local Iterator facesMessages attribute.  The windowMaximized 
	 * attribute will also be toggled to true so long as the DO NOT TOGGLE attribute wasn't set 
	 * in the session. (the attribute is set when the message popup window is minimized so we 
	 * don't want it to be overwritten).  
	 * 
	 * NOTE: This method should only be called by the FacesMessagesMultiPageSupport Phaselistener 
	 * when messages are restored to the faces context before the redner phase completes.
	 * @param facesMessages
	 */
	public void updateFacesMessages(Iterator<FacesMessage> facesMessages) {
		// set the local facesMessages attribute
		this.facesMessages = facesMessages; 
		Boolean toggleWindow = (Boolean)SessionController.retrieveSessionAttributeAndRemoveFromSession(MESSAGES_SUPPORT_DO_NOT_TOGGLE_KEY);
        if (toggleWindow==null) {
        	windowMaximized=true;
        }
	}
	
	/**
	 * Method updates all stale messages back to the faces context.
	 * 
	 * NOTE: This should only be executed when the message window is minimized or maximized. 
	 * Since a managed bean action method is called, the messages need to be copied back to 
	 * the faces context and displayed.  Otherwise all messages will be lost during the JSF lifecycle.
	 */
	private void addOldMessagesToFacesContext() {
		while (facesMessages!=null && facesMessages.hasNext()) {
			FacesContext.getCurrentInstance().addMessage(null, facesMessages.next());
		}
	}
	
	/**
	 * Method to get Error/Warning/Information messages o be displayed via Messages.xhtml
	 * 
	 */
	public String getMessagesFromContext()
	{
		int msgCount=0;
		FacesContext context = FacesContext.getCurrentInstance();
		Iterator iterator = context.getMessages();
		StringBuilder messageString = new StringBuilder();
		messageString.append("<div style=\"line-height:150%;\" >");
		while (iterator.hasNext())
		{ 
			FacesMessage message = (FacesMessage)iterator.next();
			if( message.getSeverity().compareTo(FacesMessage.SEVERITY_ERROR) == 0 )
			{
				msgCount++;
				messageString.append(msgCount+".  " + message.getDetail());
				messageString.append("<br />");
				this.setWarning(false);
				this.setError(true);
			}
			else if(message.getSeverity().compareTo(FacesMessage.SEVERITY_WARN) == 0 )
			{
				msgCount++;
				messageString.append(msgCount+".  " + message.getDetail());
				messageString.append("<br />");
				this.setError(false);
				this.setWarning(true);
			}
			else if(message.getSeverity().compareTo(FacesMessage.SEVERITY_INFO) == 0 )
			{
				msgCount++;
				messageString.append(msgCount+".  " + message.getDetail());
				messageString.append("<br />");
				this.setError(false);
				this.setWarning(false);
			}
		}
		messageString.append("</div>");
		setMessage(messageString.toString());
		return "";
	}
	
	/**
	 * Method to get Error/Warning/Information messages o be displayed via Messages.xhtml
	 * 
	 */
	public String getMessagesAndClassify()
	{
		
		FacesContext context = FacesContext.getCurrentInstance();
		Iterator iterator = context.getMessages();
		
		ArrayList<String> errorList = new ArrayList<String>();
		ArrayList<String> warningList = new ArrayList<String>();
		ArrayList<String> infoList = new ArrayList<String>();
		this.setError(false);
		this.setWarning(false);
		this.setInfo(false);
		while (iterator.hasNext())
		{ 
			FacesMessage message = (FacesMessage)iterator.next();
			
			if( message.getSeverity().compareTo(FacesMessage.SEVERITY_ERROR) == 0)
			{
				errorList.add(message.getDetail());
				this.setError(true);				
			}
			else if(message.getSeverity().compareTo(FacesMessage.SEVERITY_WARN) == 0 )
			{
				warningList.add(message.getDetail());
				this.setWarning(true);				
			}
			//other than error and warn, treating as info
			else
			{
				infoList.add(message.getDetail());
				this.setInfo(true);
			} 
		}
		
		setErrorMessages(errorList);
		setWarningMessages(warningList);
		setInfoMessages(infoList);
		
		return "";
	}
	
	public ArrayList<String> getErrorMessages() {
		return errorList;
	}

	public void setErrorMessages(ArrayList<String> errorMessages) {
		this.errorList = errorMessages;
	}

	public ArrayList<String> getWarningMessages() {
		return warningList;
	}

	public void setWarningMessages(ArrayList<String> warningMessages) {
		this.warningList = warningMessages;
	}

	public ArrayList<String> getInfoMessages() {
		return infoList;
	}

	public void setInfoMessages(ArrayList<String> infoMessages) {
		this.infoList = infoMessages;
	}
	
	public boolean isInfo() {
		return info;
	}

	public void setInfo(boolean info) {
		this.info = info;
	}
}
