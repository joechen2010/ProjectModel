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
  * Created on Apr 18, 2007
 *
 */
package com.joe.utilities.core.startup;

import com.joe.utilities.core.configuration.DataSourceFactory;
import com.joe.utilities.core.serviceLocator.ServiceLocator;
import com.joe.utilities.core.util.ReturnStatus;
import com.joe.utilities.core.util.Utils;
import com.joe.utilities.core.validation.CompositValidator;
import com.joe.utilities.core.validation.Validator;




/**
 * StartupValidator contains the list of validators that get processed
 * as part of CarePlannerWeb startup.  It is called by {@link CarePlannerContextListener}
 * to validate determine if that application is configured properly and ready 
 * for execution.
 * 
 * @author rrichard
 *
 */
public class StartupValidator extends CompositValidator {

	private Throwable startupException = null;
	
    /**
     * Default Constructor to add all of the validators to the list to be
     * validated on startup.  If an exception is thrown in the creation of 
     * a validator, it is is captured, and added within a generic validator.
     * 
     */
    public StartupValidator() {
        super();
        try {
            this.add(DataSourceFactory.getInstance());
           // this.add(JMSSourceFactory.getValidator()); 
            this.add(ServiceLocator.getInstance());
           // this.add(PerformanceFrameworkGlobals.getInstance());
        } catch (final Throwable startupException) {
        	
        	// Output exception
        	this.startupException = startupException;
        	String exceptionMessage = Utils.sameButEmptyIfNull(startupException.getMessage());
        	System.err.println("Error during Alineo startup: " + exceptionMessage);
        	startupException.printStackTrace();
        	
            this.add(new Validator() { 
                public ReturnStatus validate() {
                    ReturnStatus status = new ReturnStatus();
                    status.addError("Exception", startupException.getMessage());
                    return status;
                }
            });
        }
    }

	/**
	 * Gets the startup exception.
	 * 
	 * @return the startup exception
	 */
	public Throwable getStartupException()
	{
		return startupException;
	}
    
    
}
