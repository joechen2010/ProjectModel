/**
 * 
 */
 package com.joe.jsf.component;

import java.io.IOException;

import javax.faces.component.UICommand;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.MethodBinding;

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

public class UICollapsePanel extends UICommand {

	private static Log log = LogFactory.getLog(UICollapsePanel.class);

	
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		// get all attributes from attribute map and make local variables
		String panelSectionDescription = (String)getAttributes().get("panelSectionDescription");
		// check for appropriateness of check here... might be better to have required check in tag instead
		if (panelSectionDescription==null) throw new RuntimeException("panelSectionDescription is a required attribute of collapsePanel");
		
		String panelFilterButtonText = (String)getAttributes().get("panelFilterButtonText");
		String panelSectionCount = (String)getAttributes().get("panelSectionCount");
		String panelIconStyleClass = (String)getAttributes().get("panelIconStyleClass");
		String styleClass = (String)getAttributes().get("styleClass");
		String togglePaneID = (String)getAttributes().get("togglePaneID");
		MethodBinding rightCommandComponentAction = (MethodBinding) getAttributes().get("rightCommandComponentAction");
		

		// obtaining this list from the map is only necessary if we pass the list in as an attribute to CollapsePanel tag
		// as of now, i'm switching this such that the list is not passed in, but the list is used within a dataTable tag which
		// is a child component of collapsepanel
		// THE FOLLOWING CODE WILL EVENTUALLY BE DELETED
		//List theList = (List)getAttributes().get("dataList");
		// THE ABOVE CODE WILL EVENTUALLY BE DELETED
		
		////////////////////////////
		// write surrounding div
		//
		writer.startElement("table", this);
		
		writer.writeAttribute("id", getClientId(context), null);
		if (styleClass!=null) {
			writer.writeAttribute("class", styleClass, null);
		}
		writer.writeAttribute("cellpadding", "0", null);
		writer.writeAttribute("cellspacing", "0", null);
		
		//////////////////////////
		// write top edge of panel and header
		/////////////////////////
		encodePanelTopEdgeAndHeader(writer, panelSectionDescription, panelFilterButtonText, 
				(panelSectionCount==null)? null:panelSectionCount.toString(),panelIconStyleClass,togglePaneID,rightCommandComponentAction);
		
	
		// the left portion of the data panel needs to be generated before the children of collapsepanel are encoded
		encodeChildrenGridWrapLeft(writer);
		
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeEnd(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		
		ResponseWriter writer = context.getResponseWriter();
		
		
		// the right portion of the data panel needs to be generated after the children are encoded
		encodeChildrenGridWrapRight(writer);
		
		//////////////////////////////
		// write bottom edge of panel
		/////////////////////////////
		encodePanelBottomEdge(writer);
		
		
		writer.endElement("table");	

	}
	
	/**
	 * encodes the top of the panel edge and the header
	 * @param writer
	 * @param sectionDescription
	 * @param filterButtonText
	 * @param sectionCount
	 * @param iconStyleClass
	 * @param togglePaneID
	 * @throws IOException
	 */
	private void encodePanelTopEdgeAndHeader(ResponseWriter writer, 
				String sectionDescription, String filterButtonText, 
				String sectionCount, String iconStyleClass, String togglePaneID,
				MethodBinding rightCommandComponentAction) throws IOException {
		writer.startElement("tr", null);
		writer.writeAttribute("class", "top", null);
			writer.startElement("td", null);
			writer.writeAttribute("class", "middle", null);
			writer.writeAttribute("colspan", "3", null);
			
			writer.startElement("div", null);
			writer.writeAttribute("class", "left", null);
			writer.endElement("div");

			if (togglePaneID!=null) {
				writer.startElement("a", null);
				writer.writeAttribute("href", "#", null);
				writer.writeAttribute("class", "open_closed_arrow", null);
				writer.writeAttribute("onclick", "toggleRightPanel('"+togglePaneID+"'); return false;", null);
				writer.writeText(" ",null);
				writer.endElement("a");
			}
			
			if (iconStyleClass!=null) {
				writer.startElement("span", null);
				writer.writeAttribute("class", iconStyleClass, null);
				writer.writeText(" ", null);
				writer.endElement("span");
			}
			
			writer.startElement("h3", null);
			writer.writeAttribute("class", "right_panel_header", null);
			writer.writeText(sectionDescription, null);
			if (sectionCount!=null) {
				writer.writeText(" ("+sectionCount+")", null);
			}
			writer.endElement("h3");
		
			if (filterButtonText!=null) {
				writer.startElement("a", null);
				writer.writeAttribute("href", "#", null);
				writer.writeAttribute("class", "activities_checkbox", null);
				writer.writeText(filterButtonText, null);
				writer.endElement("a");
			}
			
			writer.startElement("div", null);
			writer.writeAttribute("class", "right", null);
						
				if (rightCommandComponentAction==null) {
					writer.startElement("a", null);
					writer.writeAttribute("href", "#", null);
					writer.writeAttribute("class", "right_add_btn", null);
					writer.writeText("+",null);
					writer.endElement("a");
				}
				else {
					UICommand uiCommandLink = generateCommandLink(FacesContext.getCurrentInstance(), rightCommandComponentAction);
					// encode the commandlink
					uiCommandLink.encodeBegin(FacesContext.getCurrentInstance());
					uiCommandLink.encodeChildren(FacesContext.getCurrentInstance());
					uiCommandLink.encodeEnd(FacesContext.getCurrentInstance());
				}
							
			writer.endElement("div");
			
			writer.endElement("td");
		writer.endElement("tr");
	}
	

	/**
	 * encodes bottom edge of panel
	 * @param writer
	 * @throws IOException
	 */
	private void encodePanelBottomEdge(ResponseWriter writer) throws IOException {
		
		writer.startElement("tr",null);
		writer.writeAttribute("class", "bottom", null);
		writer.startElement("td", null);
		writer.writeAttribute("class", "middle", null);
		writer.startElement("div", null);
		writer.writeAttribute("class", "left", null);
		writer.endElement("div");
		writer.startElement("div", null);
		writer.writeAttribute("class", "right", null);
		writer.endElement("div");
		writer.endElement("td");
		writer.endElement("tr");
	}
	
	/**
	 * encodes the left side of the grid where the child components are rendered
	 * @param writer
	 * @throws IOException
	 */
	private void encodeChildrenGridWrapLeft (ResponseWriter writer) throws IOException {
		
		writer.startElement("tr", null);
		writer.writeAttribute("class", "center", null);
		writer.startElement("td", null);
		writer.writeAttribute("colspan", "3", null);
		writer.startElement("table", null);
		writer.writeAttribute("cellpadding", "0", null);
		writer.writeAttribute("cellspacing", "0", null);
		writer.writeAttribute("class", "right_panel_data_container", null);
		writer.startElement("tr", null);
		writer.startElement("td", null);
		writer.writeAttribute("class", "left", null);
		writer.endElement("td");
		writer.startElement("td", null);
		
	}
	
	/**
	 * encodes the right side of the grid where the child components are rendered
	 * @param writer
	 * @throws IOException
	 */
	private void encodeChildrenGridWrapRight (ResponseWriter writer) throws IOException {
		
		writer.endElement("td");
		writer.startElement("td", null);
		writer.writeAttribute("class", "right", null);
		writer.endElement("td");
		
		writer.endElement("tr");
		writer.endElement("table");
		writer.endElement("td");
		writer.endElement("tr");
		
	}


	/**
	 * The is collapsed methods might be used in the future
	 * @return
	 */
	
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

	/* 
	 * Method getRendersChildren must be overridden and return true so that the children 
	 * components of CollapsePanel can be rendered.
	 * 
	 * (non-Javadoc)
	 * @see javax.faces.component.UIComponentBase#getRendersChildren()
	 */
	@Override
	public boolean getRendersChildren() {

		return true;
	}

	/**
	 * Method generates and returns an HTMLCommandLink object based on the passed in BreadCrumbInfo object.
	 * 
	 * @param context
	 * @param crumb
	 * @return
	 */
	private UICommand generateCommandLink(FacesContext context,MethodBinding methodBinding) {
		
		HtmlCommandLink uiCommandLink = new HtmlCommandLink();
		
		// set display value of command link
		uiCommandLink.setValue("add");
		// set action attribute (this is a method binding attribute based on the string 
		// equivilent within the breadcrumbinfo object)
		uiCommandLink.setAction(methodBinding);
		// set immediate to true for now to bypass jsf validation phase
		uiCommandLink.setImmediate(true);
		
		uiCommandLink.setStyleClass("right_add_btn");
		
		// set this (the UIBreadCrumb) as the parent of this commandlink object since needs 
		// to have context of form or parent to render correctly
		this.getChildren().add(uiCommandLink);
				
		return uiCommandLink;
	}
    
    

}
