package com.joe.jsf.web.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joe.utilites.core.session.SessionController;
import com.joe.utilities.core.util.ReturnStatus;
import com.joe.utilities.core.util.ReturnStatusItem;


/**
 * This class serves as the backing/managed bean for the UnhandledException and Error503 JavaServerPage.
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: Jan 8, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */
@ManagedBean(name="ExceptionBean")
@SessionScoped
public class ExceptionBean {
	
	private static Log log = LogFactory.getLog(ExceptionBean.class);
	private boolean stackDetailRendered;
	
	public ExceptionBean()
	{
		setStackDetailRendered(false);
	}
	
	public boolean isStackDetailRendered()
	{
		return stackDetailRendered;
	}

	public void setStackDetailRendered(boolean stackDetailRendered)
	{
		this.stackDetailRendered = stackDetailRendered;
	}

	/**
	 * getStackTrace
	 * @return String
	 */
	public String getStackTrace() {
		
		log.fatal("EXCEPTION THROWN");
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext extContext = context.getExternalContext();
		Map requestMap = extContext.getRequestMap();
		Throwable ex = (Throwable) requestMap.get("javax.servlet.error.exception");
		if (ex==null) {
			ex = (Throwable)SessionController.getSessionAttribute("UncheckedException");
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		fillStackTrace(ex, pw);
		return sw.toString();
	}
	
	/**
	 * Not used: Avoids JSF property exceptions on WebSphere
	 */
	public void setStackTrace(String trace)
	{
		// Avoids JSF property exceptions on WebSphere
	}
	
	/**
	 * Gets the startup exception.
	 * 
	 * @return the startup exception
	 */
	public String getStartupException()
	{
		Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		ReturnStatus startupErrorStatus = (ReturnStatus) requestMap.get("Messages");
		Throwable ex = (Throwable) requestMap.get("Exception");

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		if (ex != null)
		{
			fillStackTrace(ex, pw);
		}
		else if (startupErrorStatus != null)
		{
			for (ReturnStatusItem item : startupErrorStatus.getErrorResultStatusItems())
			{
				pw.print("Error: ");
				pw.println(item.getDefaultMessage());
			}
		}
		else
		{
			pw.print("No startup error messages or error traces are available.");  
		}
		return sw.toString();
	}

	/**
	 * Not used: Avoids JSF property exceptions on WebSphere
	 */
	public void setStartupException(String trace)
	{
		// Avoids JSF property exceptions on WebSphere
	}

	
	/**
	 * Fill stack trace.
	 * 
	 * @param t the t
	 * @param w the w
	 */
	private static void fillStackTrace(Throwable t, PrintWriter w) {
		if (t == null)
			return;
		t.printStackTrace(w);
		if (t instanceof ServletException) {
			Throwable cause = ((ServletException) t).getRootCause();
			if (cause != null) {
				w.println("Root cause:");
				fillStackTrace(cause, w);
			}
		} else if (t instanceof SQLException) {
			Throwable cause = ((SQLException) t).getNextException();
			if (cause != null) {
				w.println("Next exception:");
				fillStackTrace(cause, w);
			}
		} else {
			Throwable cause = t.getCause();
			if (cause != null) {
				w.println("Cause:");
				fillStackTrace(cause, w);
			}
		}
	}
	
	public boolean isUncheckedExceptionHandledByActionListener() {
		Throwable ex = (Throwable) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("javax.servlet.error.exception");
		if (ex==null) return true;
		else return false;
		
	}

	public String getTransactionId() {
		return (String)SessionController.getSessionAttribute("transactionId");
		
	}

	public String getApplicationServerHostName() {
		return (String)SessionController.getSessionAttribute("applicationServerHostName");
		
	}

	public String getApplicationServerIPAddress() {
		return (String)SessionController.getSessionAttribute("applicationServerIPAddress");
		
	}
	
	public void closeErrorPopup(ActionEvent event)
	{
		setStackDetailRendered(false);
	}
	
	public String openErrorDetails()
	{
		setStackDetailRendered(true);
		return null;
	}
}
