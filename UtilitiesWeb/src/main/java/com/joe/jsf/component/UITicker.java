/**
 * 
 */
 package com.joe.jsf.component;

import java.io.IOException;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * This class TODO <enter description of class here>
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: May 25, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */

public class UITicker extends UIOutput {

	public void encodeBegin(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", this);
		
		writer.writeAttribute("id", getClientId(context), null);
		
		String width = (String)getAttributes().get("width");
	    String height = (String)getAttributes().get("height");
	    
	    String style = (String)getAttributes().get("style");
	    
	    style= (style!=null) ? style + ";" : "";
	    
	    if (width  != null) style += "width:" + width + ";";
	    if (height != null) style += "height:" + height+ ";";
	    
	    writer.writeAttribute("style", style, null);
	    
	    
	    String styleClass = (String)getAttributes().get("styleClass");
	    if (style!=null) {
	    	writer.writeAttribute("class", styleClass, null);
	    }
	    
	    String title = (String)getAttributes().get("title");
		if (title!=null) {
		  	writer.writeAttribute("title", title, null);
		}
		
		writer.write("\r\n");
		writer.startElement("tr", this);
		writer.endElement("tr");
	}
	public void encodeEnd(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
	}
}
