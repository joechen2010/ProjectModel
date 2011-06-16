 package com.joe.jsf.helper;

import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import com.joe.jsf.web.view.BreadCrumbBean;
import com.joe.jsf.web.view.BreadCrumbInfo;

/**
 * This class provides utility functionality to populate the breadcrumbview managed bean
 * in session scope.
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: Mar 14, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */

public class BreadCrumbHelper {
	
    public static void popBreadCrumb() {
        BreadCrumbBean breadCrumbBean = getBreadCrumbBeanFromContext();
        List<BreadCrumbInfo> crumbs = breadCrumbBean.getBreadCrumbHistory();
        crumbs.remove(crumbs.size()-1);
    }
	
	/**
	 * This method takes parameters necessary for a bread crumb, creates the bread crumb, 
	 * and adds the object to the bread Crumb bean in session scope.
	 * 
	 * @param breadCrumbDisplayText
	 * @param methodBindingName
	 * @param params
	 * @throws RuntimeException
	 */
	public static void addBreadCrumb(String breadCrumbDisplayText, String methodBindingName, Map<String,String> params) throws RuntimeException{
		
		if (breadCrumbDisplayText==null || breadCrumbDisplayText.equals("")) throw new RuntimeException("Bread Crumb Display text must contain a value");
		BreadCrumbBean breadCrumbBean = getBreadCrumbBeanFromContext();
		// create bread crumb object
		// add bread crumb object to managed bean
		breadCrumbBean.addBreadCrumb(breadCrumbDisplayText,methodBindingName,params);
	}
	
	/**
	 * Return the last bread crumb
	 * @return the last bread crumb
	 */
	public static BreadCrumbInfo getLastBreadCrumb()
	{
		return getBreadCrumbBeanFromContext().getLastBreadCrumb();
	}
	
	/**
	 * This method gets the BreadCrumb managed bean from session scope and returns the 
	 * reference to the bean.
	 * 
	 * @return
	 */
	private static BreadCrumbBean getBreadCrumbBeanFromContext() {
		
		// obtain faces context and neutral application session
		FacesContext ctx = FacesContext.getCurrentInstance();
        Application app = ctx.getApplication();
        // obtain instance of HeaderBean from session context
        BreadCrumbBean breadCrumbBean = (BreadCrumbBean) app.createValueBinding("#{BreadCrumbBean}").getValue(ctx);
		
        return breadCrumbBean;
	}
	
	public static void clearBreadCrumbHistroy() throws RuntimeException{
		BreadCrumbBean breadCrumbBean = getBreadCrumbBeanFromContext();
		if (breadCrumbBean.getBreadCrumbHistory()!=null)
			breadCrumbBean.getBreadCrumbHistory().clear();
	}
}
