package com.joe.jsf.web.view;

import com.joe.jsf.helper.ManagedBeanUtility;

/**
 * This class provides static helper methods to provide simplified access to RestoreViewBean.
 * @author GRT
 *
 * Creation date: May 9, 2008 12:40:00 PM
 * Copyright (c) 2008 MEDecision, Inc.  All rights reserved.
 * 
*/

public class RestoreViewBeanHelper {

	/**
	 * updates Map with viewState for given viewName key
	 * @param viewName
	 * @param viewState
	 */
	public static void setRestoreViewState(String viewName, Object viewState) {
		RestoreViewBean restoreViewBean = (RestoreViewBean) ManagedBeanUtility.getBindingObject("#{RestoreViewBean}");
		restoreViewBean.setRestoreViewState(viewName, viewState);
	}
	
	/**
	 * clears entry in Map for given viewName key
	 * @param viewName
	 */
	public static void clearRestoreViewState(String viewName){
		RestoreViewBean restoreViewBean = (RestoreViewBean) ManagedBeanUtility.getBindingObject("#{RestoreViewBean}");
		restoreViewBean.clearRestoreViewState(viewName);
	}
	
	/**
	 * retrieve viewState for given viewName key
	 * @param viewName
	 * @return the viewState
	 */
	public static Object getRestoreViewState(String viewName){
		RestoreViewBean restoreViewBean = (RestoreViewBean) ManagedBeanUtility.getBindingObject("#{RestoreViewBean}");
		return restoreViewBean.getRestoreViewState(viewName);
	}
	
}
