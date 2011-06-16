package com.joe.utilities.core.jsf.listeners;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.component.ActionSource;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joe.utilites.core.session.SessionController;
import com.joe.utilities.core.logging.LoggingContextHelper;
import com.joe.utilities.core.serviceLocator.ServiceLocator;
import com.sun.faces.application.ActionListenerImpl;

/**
 * Action listener to trap any unchecked or unhandled
 * exception that comes from the invoked action.
 */
public class UncheckedExceptionListener extends ActionListenerImpl implements ActionListener
{
	private Log log = LogFactory.getLog(UncheckedExceptionListener.class);
	
	public void processAction(ActionEvent event)
	{
        ActionSource actionSource = (ActionSource)event.getComponent();
        MethodBinding methodBinding = actionSource.getAction();
        if (methodBinding == null)
        	methodBinding = actionSource.getActionListener();

        if(methodBinding == null)
        {
        	super.processAction(event);
        	return;
        }
        
        String fromAction = methodBinding.getExpressionString();
        
        LoggingContextHelper.addValueToContent(LoggingContextHelper.TRANSACTION_TYPE, fromAction);
        
        try
        {
			super.processAction(event);
		}
		catch (Exception exception)
		{
            log.fatal("Transaction ID: " + LoggingContextHelper.getValueFromContext(LoggingContextHelper.TRANSACTION_ID));
            log.fatal("Transaction Type: " + LoggingContextHelper.getValueFromContext(LoggingContextHelper.TRANSACTION_TYPE));
            log.fatal("IP Address: " + ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRemoteAddr());			
            log.fatal("Request Parameters: " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap());
            log.fatal("Stack Trace: ");
			exception.printStackTrace();
			
			SessionController.cleanSession();
			SessionController.addSessionAttribute("UncheckedException",exception);
			
			SessionController.addSessionAttribute("transactionId", LoggingContextHelper.getValueFromContext("TransactionId"));
			SessionController.addSessionAttribute("applicationServerHostName",getApplicationServerHostName());
			SessionController.addSessionAttribute("applicationServerIPAddress",
					getApplicationServerIPAddress());
			FacesContext facesContext = FacesContext.getCurrentInstance();
			Application application = facesContext.getApplication();
			NavigationHandler navigationHandler = application.getNavigationHandler();
			navigationHandler.handleNavigation(facesContext,null,"unhandledException");
			facesContext.renderResponse();
		}
		finally
		{}
	}

	public static String getApplicationServerHostName() {
		String machineName = null;

		try {
			machineName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}

		return machineName;
	}
	
	public static String getApplicationServerIPAddress() {
		String ipAddr = null;

		try {
			ipAddr = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}

		return ipAddr;
	}
}
