 package com.joe.jsf.web.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * This class TODO <enter description of class here>
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: Jul 11, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */

public class BreadCrumbList extends ArrayList<BreadCrumbInfo> {

	private static final long serialVersionUID = 1L;
	
	public BreadCrumbList() {
	}

	public BreadCrumbList(int arg0) {
		super(arg0);
	}

	public BreadCrumbList(Collection<BreadCrumbInfo> c) {
		super(c);
	}
	
	/**
	 * Method adds a bread crumb to the bread crumb history list.  If the bread crumb is already 
	 * in the list, then all bread crumbs after that will be removed.  If it is not present in the 
	 * list, it will be added to the list.
	 * 
	 * @param breadCrumb
	 */
	public void addBreadCrumb(String breadCrumbDisplayText, String methodBindingName, Map<String,String> params) {
		//if (this.==null || breadCrumbHistory.isEmpty()) breadCrumbHistory = new ArrayList<BreadCrumbInfo>();
		
		if (breadCrumbDisplayText==null || breadCrumbDisplayText.equals("")) throw new RuntimeException("Bread Crumb Display text must contain a value");
		
		// create bread crumb object
		BreadCrumbInfo breadCrumb = new BreadCrumbInfo(breadCrumbDisplayText,methodBindingName,params);
		
		boolean found = false;

		for (int i=0; i<size();) {
			// if the bread crumb has already been found in the list, remove all other breadcrumbs
			if (found) remove(i);
			else {
				// check to see if the current breadcrumb in the list is the same as the one being past
				// ... note that display text is the asssumed unique identifier
				if (((BreadCrumbInfo)get(i)).getDisplayText().equals(breadCrumb.getDisplayText())) {
					found=true;
				}
				i++;
			}
		}
		// add the bread crumb to the list if it was not present already
		if (!found) add(breadCrumb);
	}
	
	/**
	 * Return last bread crumb.
	 */
	public BreadCrumbInfo getLastBreadCrumb()
	{
		if (size() > 0)
		{
			return get(size()-1);
		}
		else
			return null;
	}
}
