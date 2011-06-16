package com.joe.jsf.component;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import java.io.IOException;

public class DivRenderer extends Renderer
{
    /**
     * A no argument default constructor.
     */
    public DivRenderer()
    {

    }

    public void encodeBegin(FacesContext context, UIComponent component) throws IOException
    {
        String renderAttribValue = (String) component.getAttributes().get("render");
        if (renderAttribValue == null || renderAttribValue.trim().equalsIgnoreCase("TRUE"))
        {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement("div", component);
            writer.writeAttribute("id", component.getClientId(context), "clientId");
            writer.writeAttribute("class", component.getAttributes().get("styleclass"), "styleclass");
            //writer.flush();
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component) throws IOException
    {
        String renderAttribValue = (String) component.getAttributes().get("render");
        if (renderAttribValue == null || renderAttribValue.trim().equalsIgnoreCase("TRUE"))
        {
            ResponseWriter writer = context.getResponseWriter();
            writer.endElement("div");
            //writer.flush();
        }
    }

    public void decode(FacesContext context, UIComponent component)
    {
        return;
    }

    /* public boolean getRendersChildren()
    {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component) throws IOException
    {
        String renderAttribValue = (String) component.getAttributes().get("render");
        if (renderAttribValue == null || renderAttribValue.trim().equalsIgnoreCase("TRUE"))
        {
            super.encodeChildren(context, component);
        }
    }  */
}

