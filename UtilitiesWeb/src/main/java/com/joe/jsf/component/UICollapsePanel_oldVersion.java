/**
 * 
 */
 package com.joe.jsf.component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * This is the component class for the collapsePanel tag.
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: May 25, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */

public class UICollapsePanel_oldVersion extends UICommand {

	private static Log log = LogFactory.getLog(UICollapsePanel_oldVersion.class);

	
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		String panelSectionDescription = (String)getAttributes().get("panelSectionDescription");
		// check for appropriateness of check here... might be better to have required check in tag instead
		if (panelSectionDescription==null) throw new RuntimeException("panelSectionDescription is a required attribute of collapsePanel");
		
		String panelFilterButtonText = (String)getAttributes().get("panelFilterButtonText");
		
		List theList = (List)getAttributes().get("dataList");		
		
		////////////////////////////
		// write surrounding div
		//
		writer.startElement("div", this);
		writer.writeAttribute("id", "activities", null);
		writer.writeAttribute("class", "binders", null);
		
		//////////////////////////////////////
		// table containing content of header
		//
		writer.startElement("table", null);
		writer.writeAttribute("class", "binders_content", null);
		
		//////////////////////////
		// write top edge of panel
		/////////////////////////
		encodePanelTopEdge(writer);
		
		
		//////////////////////////
		// start content of header
		//
		
		
		writer.startElement("td", null);
		writer.writeAttribute("class", "left", null);
		writer.endElement("td");
		writer.startElement("td", null);
		writer.endElement("td");
		
		writer.startElement("td", null);
		writer.writeAttribute("class", "content", null);
		
		
		encodePanelHeaderContent(writer,panelFilterButtonText,panelSectionDescription);
		
		/////////////////////////////
		// write data grid
		/////////////////////////////
		encodeDataGrid(writer,theList);
				
		
		writer.endElement("td");
		
		writer.startElement("td", null);
		writer.endElement("td");
		writer.startElement("td", null);
		writer.writeAttribute("class", "right", null);
		writer.endElement("td");
	
		
		//
		// end content of header
		/////////////////////////
		
		//////////////////////////////
		// write bottom edge of panel
		/////////////////////////////
		encodePanelBottomEdge(writer);
		
		
		
		//
		// end table
		////////////////////
		writer.endElement("table");
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeEnd(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
		
		

	}
	
	private void encodePanelTopEdge(ResponseWriter writer) throws IOException {
		writer.startElement("tr",null);
		
		writer.startElement("td", null);
		writer.writeAttribute("colspan", "2", null);
		writer.startElement("div", null);
		writer.writeAttribute("class", "top_left", null);
		writer.endElement("div");
		writer.endElement("td");

		writer.startElement("td", null);
		writer.startElement("div", null);
		writer.writeAttribute("class","top", null);
		writer.endElement("div");
		writer.endElement("td");
		
		writer.startElement("td", null);
		writer.writeAttribute("colspan", "2", null);
		writer.startElement("div", null);
		writer.writeAttribute("class", "top_right", null);
		writer.endElement("div");
		writer.endElement("td");
		
		writer.endElement("tr");
		// start of next row
		writer.startElement("tr", null);
	}
	
	private void encodePanelBottomEdge(ResponseWriter writer) throws IOException {
		
		writer.endElement("tr");  // leftover from top edge
		
		writer.startElement("tr",null);
		
		writer.startElement("td", null);
		writer.writeAttribute("colspan", "2", null);
		writer.startElement("div", null);
		writer.writeAttribute("class", "bot_left_odd", null);
		writer.endElement("div");
		writer.endElement("td");

		writer.startElement("td", null);
		writer.startElement("div", null);
		writer.writeAttribute("class","bottom_odd", null);
		writer.endElement("div");
		writer.endElement("td");
		
		writer.startElement("td", null);
		writer.writeAttribute("colspan", "2", null);
		writer.startElement("div", null);
		writer.writeAttribute("class", "bot_right_odd", null);
		writer.endElement("div");
		writer.endElement("td");
		
		writer.endElement("tr");
	}
	
	private void encodeDataGrid(ResponseWriter writer,List dataList) throws IOException {
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "data", null);
		
		writer.startElement("table", null);
		
		Iterator listIter = dataList.iterator();
		while (listIter.hasNext()) {
			Object nextObj = listIter.next();
			
				writer.startElement("tr", null);
				writer.writeAttribute("class", "odd", null);
				writer.startElement("td", null);
				writer.writeAttribute("class", "table_item", null);
				writer.writeText("DID NOT RECOGNIZE OBJECT IN ENCODEDATA METHOD", null);
				writer.endElement("td");
				writer.startElement("td", null);
				writer.writeAttribute("class", "table_date", null);
				writer.writeText("", null);
				writer.endElement("td");
				writer.endElement("tr");
			
			
		}
		
		
		writer.endElement("table");
		writer.endElement("div");
		
	}
	
	private void encodePanelHeaderContent(ResponseWriter writer, String panelFilterButtonText, String panelSectionDescription) throws IOException {
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "binder", "null");
		writer.writeAttribute("id", "title_bar_active", null);

		//	<!-- input type="hidden" value="form:activities=add" -->
		writer.startElement("button", null);
		writer.writeAttribute("class", "active_binder", null);
		writer.writeText("Open/Close", null);
		writer.endElement("button");
		
		writer.startElement("h3", null);
		writer.writeText(panelSectionDescription+" (3)", null);
		writer.endElement("h3");
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "activity_filters", null);

		// <!-- input type="hidden" value="filter:activities=my" -->
		
		if (panelFilterButtonText!=null) {
			writer.startElement("button", null);
			writer.writeAttribute("class", "active", null);
			writer.writeAttribute("id", "activity_filter", null);
			writer.writeText(panelFilterButtonText, null);
			writer.endElement("button");
		}

		writer.endElement("div");

		// 	<!-- input type="hidden" value="form:activities=add" -->
		writer.startElement("button", null);
		writer.writeAttribute("class", "add", null);
		writer.writeText("Add New", null);
		writer.endElement("button");
									
		writer.endElement("div");
	}

	public boolean isCollapsed()
    {
        //return isCollapsed(getValue());
		return true;
    }

    public static boolean isCollapsed(Object collapsedValue)
    {
        Object value = collapsedValue;

        if(value instanceof Boolean)
        {
            return ((Boolean) value).booleanValue();
        }
        else if (value instanceof String)
        {
            return Boolean.valueOf((String) value).booleanValue();
        }

        return true;
    }
    
    

}
