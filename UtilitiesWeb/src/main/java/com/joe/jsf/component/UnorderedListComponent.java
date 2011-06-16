package com.joe.jsf.component;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * UI Component for writing out an unordered list element.
 * @author swilcher
 * Creation Date: 7/13/2009
 */
public class UnorderedListComponent extends UIComponentBase
{
	private static Log log = LogFactory.getLog(ListElementComponent.class);
	private String styleClass;
	
	public UnorderedListComponent(String styleClass)
	{
		this.styleClass = styleClass;
	}
		
	public String getStyleClass()
	{
		return styleClass;
	}

	public void setStyleClass(String styleClass)
	{
		this.styleClass = styleClass;
	}

	@Override
	public String getFamily()
	{
		return null;
	}
	
	public void encodeBegin(FacesContext context) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("ul", this);
		
		if(getStyleClass() != null && !StringUtils.isBlank(getStyleClass()))
			writer.writeAttribute("class", getStyleClass(), null);
	}
	
	public void encodeEnd(FacesContext context)
	{
		ResponseWriter writer = context.getResponseWriter();
		try
		{
			writer.endElement("ul");
		}
		catch (IOException e)
		{
			log.error(e);
		}
	}
}