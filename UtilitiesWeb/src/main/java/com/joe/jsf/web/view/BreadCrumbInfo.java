 package com.joe.jsf.web.view;

import java.util.Map;

import com.joe.jsf.helper.ManagedBeanUtility;

/**
 * Presentation tier bean that hold information for displaying a bread crumb.  
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: Mar 19, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */

public class BreadCrumbInfo {
	
	private String displayText;
	private String bindingMethod;
	private Map<String, String> params;
	
	public BreadCrumbInfo(String displayText, String bindingMethod,Map<String, String> params) {
		super();
		this.displayText = displayText;
		this.bindingMethod = bindingMethod;
		this.params = params;
	}
	/**
	 * @return
	 */
	public String getBindingMethod() {
		return bindingMethod;
	}
	/**
	 * @param bindingMethod
	 */
	public void setBindingMethod(String bindingMethod) {
		this.bindingMethod = bindingMethod;
	}
	/**
	 * @return
	 */
	public String getDisplayText() {
		return displayText;
	}
	/**
	 * @param displayText
	 */
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
	/**
	 * @return the params
	 */
	public Map<String, String> getParams() {
		return params;
	}
	/**
	 * @param params the params to set
	 */
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	/** 
	 * Will return true if this breadcrumb is the first in the list
	 * of breadcrumbs.
	 * @return	boolean
	 */
	public boolean getIsFirst()
	{
		BreadCrumbBean breadCrumbBean = (BreadCrumbBean)ManagedBeanUtility.getBindingObject("#{BreadCrumbBean}");
		BreadCrumbInfo first = breadCrumbBean.getFirstBreadCrumb();
		
		if(breadCrumbBean.getBreadCrumbHistory() != null && breadCrumbBean.getSize() > 1)
		{
			if((this.getBindingMethod() != null && this.getBindingMethod().equals(first.getBindingMethod()))
					&& (this.getDisplayText() != null && this.getDisplayText().equals(first.getDisplayText())))
			{
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	/** 
	 * Will return true if there are bread crumbs and there are more than 1.  
	 * Prevents 2 breadcrumbs appearing if there are only 1 to display, which
	 * will be considered the first.
	 * @return	boolean
	 */
	public boolean getIsLast()
	{
		BreadCrumbBean breadCrumbBean = (BreadCrumbBean)ManagedBeanUtility.getBindingObject("#{BreadCrumbBean}");
		BreadCrumbInfo last = breadCrumbBean.getLastBreadCrumb();
		
		if(breadCrumbBean.getBreadCrumbHistory() != null && breadCrumbBean.getSize() > 1)
		{
			if((this.getBindingMethod() != null && this.getBindingMethod().equals(last.getBindingMethod()))
					&& (this.getDisplayText() != null && this.getDisplayText().equals(last.getDisplayText())))
			{
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	/**
	 * Will return true if this is the only bread crumb in the list.
	 * @return	boolean
	 */
	public boolean getIsOnly()
	{
		BreadCrumbBean breadCrumbBean = (BreadCrumbBean)ManagedBeanUtility.getBindingObject("#{BreadCrumbBean}");
		
		if(breadCrumbBean.getBreadCrumbHistory() != null && breadCrumbBean.getSize() == 1)
		{
			BreadCrumbInfo onlyCrumb = breadCrumbBean.getBreadCrumbHistory().get(0);
			if((this.getBindingMethod() != null && this.getBindingMethod().equals(onlyCrumb.getBindingMethod()))
					&& (this.getDisplayText() != null && this.getDisplayText().equals(onlyCrumb.getDisplayText())))
			{
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	/**
	 * Will return true if this crumbs sits in between 2 other crumbs.
	 * @return	boolean
	 */
	public boolean getIsMiddleCrumb()
	{
		if(!getIsFirst() && !getIsLast() && !getIsOnly())
			return true;
		else
			return false;
	}
}