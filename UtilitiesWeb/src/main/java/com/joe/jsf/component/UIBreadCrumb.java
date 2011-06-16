/**
 * 
 */
 package com.joe.jsf.component;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joe.jsf.web.view.BreadCrumbInfo;


/**
 * This is the component class for the breadCrumb tag.
 * 
 * @author       John J. Jones III
 * 
 * Creation date: Jun 8, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */

public class UIBreadCrumb extends UICommand {

	private static Log log = LogFactory.getLog(UIBreadCrumb.class);

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		List<BreadCrumbInfo> breadCrumbList = (List)getAttributes().get("breadCrumbList");
		String styleClass = (String)getAttributes().get("styleClass");
		
		writer.startElement("div", null);
		writer.writeAttribute("id", "bcr", null);
		// write class attribute for header
		writer.writeAttribute("class", styleClass, null);
		
		// write end cap div
		writer.startElement("div", null);
		writer.writeAttribute("class", "bcr_right_end_cap", null);
		writer.endElement("div");
		
		writer.startElement("ul", null);
		
		final String liClassFirst = "first";
		String liClass = "";
		
		// encode bread crumb
		int iternum = 0;
		for (BreadCrumbInfo crumb : breadCrumbList) {
		
			if (iternum==0) {
				liClass=liClassFirst;
			}
			else {
				liClass="";
			}
			// if this breadcrumb is not the last in the list
			if (iternum+1 < breadCrumbList.size()) {
				encodeListItem(writer, context, liClass, true, crumb);
			}
			// if the crumb is the last one (represents active crumb)
			else {
				
				liClass = new String("on");
				if (iternum==0) {
					liClass = liClass.concat(" first");
				}
				encodeListItem(writer, context, liClass, false, crumb);
				
			}
			iternum++;
		}
			
		writer.endElement("ul");
	}

	@Override
	public void encodeChildren(FacesContext arg0) throws IOException {
		//In SUN RI JSF 1.2: Override the parents encodeChildren method to prevent duplicate encode of CommandLink 
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponentBase#encodeEnd(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.endElement("div");
	}
	
	
	/**
	 * Method encodes a list item for the unordered breadcrumb list
	 * 
	 * @param writer
	 * @param context
	 * @param liClass
	 * @param linkable
	 * @param crumb
	 * @throws IOException
	 */
	private void encodeListItem(ResponseWriter writer,FacesContext context,
				String liClass,boolean linkable, BreadCrumbInfo crumb) throws IOException {
		writer.startElement("li", null);
		
		if (liClass!=null && !liClass.equals("")) {
			writer.writeAttribute("class", liClass, null);
		}
		
		if (linkable) {
			
			if (crumb.getBindingMethod()==null || crumb.getBindingMethod().equals("")) {
				/*
				UIOutput uiOutput = new UIOutput();
				uiOutput.setValue(crumb.getDisplayText());
				uiOutput.encodeBegin(context);
				uiOutput.encodeChildren(context);
				uiOutput.encodeEnd(context);
				*/
				writer.startElement("a", null);
				writer.writeText(crumb.getDisplayText(), null);
				writer.endElement("a");
				writer.startElement("div", null);
				writer.endElement("div");
			}
			else {
				UICommand uiCommandLink = generateCommandLinkFromBreadCrumb(context, crumb);
				// encode the commandlink
				uiCommandLink.encodeBegin(context);
				uiCommandLink.encodeChildren(context);
				uiCommandLink.encodeEnd(context);
				
			}
						
			writer.startElement("div", null);
			writer.writeAttribute("class", "off", null);
			writer.endElement("div");
			
			writer.startElement("div", null);
			writer.writeAttribute("class", "over", null);
			writer.endElement("div");
		}
		else {
			writer.startElement("a", null);
			writer.writeText(crumb.getDisplayText(), null);
			writer.endElement("a");
			writer.startElement("div", null);
			writer.endElement("div");
		}
		
		
		writer.endElement("li");
		
	}
	
	
	/**
	 * Method generates and returns an HTMLCommandLink object based on the passed in BreadCrumbInfo object.
	 * 
	 * @param context
	 * @param crumb
	 * @return
	 */
	private UICommand generateCommandLinkFromBreadCrumb(FacesContext context,BreadCrumbInfo crumb) {
		
		UICommand uiCommandLink = new HtmlCommandLink();
		
		// set display value of command link
		uiCommandLink.setValue(crumb.getDisplayText());
		// set action attribute (this is a method binding attribute based on the string 
		// equivilent within the breadcrumbinfo object)
		uiCommandLink.setAction(context.getApplication().createMethodBinding(crumb.getBindingMethod(), null));
		// set immediate to true for now to bypass jsf validation phase
		uiCommandLink.setImmediate(true);
		// set this (the UIBreadCrumb) as the parent of this commandlink object since needs 
		// to have context of form or parent to render correctly
		this.getChildren().add(uiCommandLink);
		
		if (crumb.getParams()!=null) {
			
			// iterate through all map keys
			Iterator keyIter = crumb.getParams().keySet().iterator();
			while (keyIter.hasNext()) {
				// Create UIParameter object and set name and value attributes 
				String key = (String)keyIter.next();
				UIParameter uiParam = new UIParameter();
				uiParam.setName(key);
				uiParam.setValue(crumb.getParams().get(key));
				// add parameter as child to commandlink object
				uiCommandLink.getChildren().add(uiParam);
			}		
			
		}
		// create unique id in tree for command link
		uiCommandLink.setId(context.getViewRoot().createUniqueId());
		
		return uiCommandLink;
	}
	
	
}
