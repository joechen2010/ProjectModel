package com.joe.jsf.view;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.joe.utilities.core.facade.CacheFlushFacade;
import com.joe.utilities.core.serviceLocator.ServiceLocator;
import com.joe.utilities.core.util.Utils;


/**
* This backing bean supports the "cacheManagement.jsp" from which a user can manually flush the CacheManager cache.
* @author Dave Ousey
* 
* Creation date: 11/30/2007 3:40 PM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
@ManagedBean(name="CacheManagementBean")
@RequestScoped
public class CacheManagementView
{
    private String message;
    
    /**
     * Default constructor
     */
    public CacheManagementView()
    {
        super();
        message = "";
    }

    /**
     * flushCache
     * @return String
     */
    public String flushCache()
    {
    	// Flush all (in all app server instances)
    	CacheFlushFacade cacheFlushFacade = (CacheFlushFacade) ServiceLocator.getInstance().getBean("cacheFlushFacade");
    	cacheFlushFacade.broadcastCacheFlush();
    	
    	message = "Tables and options settings in the database will now take effect: " + Utils.formatDate(new Date(), true);
    	return null;
    }
    
    /**
     * Method getMessage. 
     * @return String
     */
    public String getMessage()
    {
    	return message;
    }

}
