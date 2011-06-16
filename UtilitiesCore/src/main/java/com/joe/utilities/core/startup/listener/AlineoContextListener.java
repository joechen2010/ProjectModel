/*
 * MEDecision, Inc. Software Development Infrastructure, Version 1.0
 *
 * Copyright (c) 2007 MEDecision, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of 
 * MEDecision, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with MEDecision, Inc.
 *
 * MEDecision, Inc MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. MEDecision, Inc SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
  * Created on Feb 7, 2007
 *
 */
package com.joe.utilities.core.startup.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joe.utilities.core.serviceLocator.ServiceLocator;
import com.joe.utilities.core.startup.StartupValidator;
import com.joe.utilities.core.util.ReturnStatus;



/**
 * @author rrichard
 *
 */
public class AlineoContextListener implements ServletContextListener {

    private static Log logger = LogFactory.getLog(AlineoContextListener.class);
    
    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event) {
    	
    	// Shutdown Spring gracefully
    	ServiceLocator.clearInstance();
    }

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {
        logger.info(".contextInitialized:  Entering");
        StartupValidator validator = new StartupValidator();
        ReturnStatus status =  validator.validate();
        Boolean success = Boolean.TRUE;
        if (!status.isSuccess()) {
            success = Boolean.FALSE;
            event.getServletContext().setAttribute("Messages", status);
            event.getServletContext().setAttribute("Exception", validator.getStartupException());
            
        }
        event.getServletContext().setAttribute("Working", success);
        logger.info(".contextInitialized:  Exiting");
    }

}
