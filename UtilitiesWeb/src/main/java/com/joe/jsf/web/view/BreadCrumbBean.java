 package com.joe.jsf.web.view;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.joe.jsf.helper.ManagedBeanUtility;


/**
 * This class will serve as a managed bean for the breadcrumbs in the presentation.
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: Mar 9, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */
@ManagedBean(name="BreadCrumbBean")
@SessionScoped
public class BreadCrumbBean {
	
	private HtmlPanelGrid uiPanel;
	private BreadCrumbList breadCrumbHistory;
	private String actionMethodAttribute;
	private String navigationStringAttribute;
	private Map<String,String> paramsAttribute;
	
	public BreadCrumbBean()
	{
		breadCrumbHistory = new BreadCrumbList();
	}

	/**
	 * @return the breadCrumbHistory
	 */
	public List<BreadCrumbInfo> getBreadCrumbHistory() {
		return breadCrumbHistory;
	}
	
	public String getActionMethodAttribute()
	{
		return actionMethodAttribute;
	}

	public void setActionMethodAttribute(String actionMethodAttribute)
	{
		this.actionMethodAttribute = actionMethodAttribute;
	}

	public String getNavigationStringAttribute()
	{
		return navigationStringAttribute;
	}

	public void setNavigationStringAttribute(String navigationStringAttribute)
	{
		this.navigationStringAttribute = navigationStringAttribute;
	}

	public Map<String, String> getParamsAttribute()
	{
		return paramsAttribute;
	}

	public void setParamsAttribute(Map<String, String> paramsAttribute)
	{
		this.paramsAttribute = paramsAttribute;
	}

	/**
	 * Method adds a bread crumb to the bread crumb history list.  If the bread crumb is already 
	 * in the list, then all bread crumbs after that will be removed.  If it is not present in the 
	 * list, it will be added to the list.
	 * @param breadCrumb
	 */
	public void addBreadCrumb(String breadCrumbDisplayText, String methodBindingName, Map<String,String> params)
	{
		if (breadCrumbHistory==null || breadCrumbHistory.isEmpty()) breadCrumbHistory = new BreadCrumbList();	
			breadCrumbHistory.addBreadCrumb(breadCrumbDisplayText,methodBindingName,params);
	}

	/**
	 * @param uicomponent the uicomponent to set
	 */
	public void setUiPanel(HtmlPanelGrid uiPanel) {
		this.uiPanel = uiPanel;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		if (breadCrumbHistory != null)
			return breadCrumbHistory.size();
		else 
			return 0;
	}
	
	public boolean getHasBreadCrumbs()
	{
		if(getSize() > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Common method to execute a breadcrumb navigation request.
	 * If both the action method and navigation string are specified
	 * the application will invoke the method and return the navigation
	 * string to navigate the app to that page.  If not, nothing will
	 * happen and the user will stay on the current page.
	 * @return	String
	 */
	public String executeBreadCrumbNavigation()
	{
		String actionMethod = getActionMethodAttribute();
		String navigationString = getNavigationStringAttribute();
		Map<String, String> params = getParamsAttribute();
		
		if((actionMethod != null && !StringUtils.isBlank(actionMethod)))
		{
			return (String)ManagedBeanUtility.invokeMethodBinding(actionMethod);
		}
		else
			return null;
	}
	
	/**
	 * action method to set request attributes.
	 * @param	ActionEvent
	 */
	public void setRequestAttributes(ActionEvent event)
	{
		Map attributes = event.getComponent().getAttributes();
		Map<String, String> params = (Map<String, String>)attributes.get("params");
		setParamsAttribute(params);
		String actionMethod = (String)attributes.get("actionMethod");
		setActionMethodAttribute(actionMethod);
		String navigationString = (String)attributes.get("navigationString");
		setNavigationStringAttribute(navigationString);
	}
	
	/**
	 * Return first bread crumb.
	 */
	public BreadCrumbInfo getFirstBreadCrumb()
	{
		if (getBreadCrumbHistory() != null && getBreadCrumbHistory().size() > 0)
		{
			return getBreadCrumbHistory().get(0);
		}
		else
			return null;
	}
	
	/**
	 * Return last bread crumb.
	 */
	public BreadCrumbInfo getLastBreadCrumb()
	{
		if (getBreadCrumbHistory() != null && getBreadCrumbHistory().size() > 0)
		{
			return getBreadCrumbHistory().get(getBreadCrumbHistory().size() - 1);
		}
		else
			return null;
	}
	
}

