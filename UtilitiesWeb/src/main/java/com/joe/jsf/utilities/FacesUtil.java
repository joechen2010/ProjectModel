package com.joe.jsf.utilities;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Madhava.G This class will mostly have the static final methods that
 *         deal with the ExternalContext associated with FacesContext
 * @deprecated - Use SessionController and ManagedBeanUtility instead
 */
@Deprecated
public class FacesUtil
{
    public static final String SEPERATOR = "~";

    /**
     * This method will return the String value of a "request parameter"
     *
     * @param requestParameter
     * @return
     */
    public static final String getRequestParameterValue(String requestParameter)
    {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(
                requestParameter);
    }

    /**
     * This method will return multiple String values as a String Array of a
     * "request parameter"
     *
     * @param requestParameter
     * @return
     */
    public static final String[] getRequestParameterValues(String requestParameter)
    {
        return (String[]) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap().get(requestParameter);
    }

    /**
     * A handy method to print all the Http-Request parameter key and the
     * associated value. Where in a parameter can have multiple values, only the
     * first selected value is printed.
     *
     * @param fc
     */
    public static void printHttpRequestParameters(FacesContext fc)
    {
        Map map = fc.getExternalContext().getRequestParameterMap();
        Set keys = map.keySet();
        Iterator it = keys.iterator();
        StringBuffer sbuf = new StringBuffer();
        String key = null;
        while (it.hasNext())
        {
            key = (String) it.next();
            sbuf.append("REQUEST PARAMETER KEY ==> [" + key + "] VALUE ==> ["
                    + map.get(key) + "]\n");
        }
        System.out
                .println("======  FacesUtil.getRequestParameterValue() ===== SIZE "
                        + map.size() + "\n" + sbuf.toString());
    }

    /**
     * Return the value of the session parameter associated with the facesContext.
     *
     * @param fc
     * @param parameter
     * @return
     */
    public static Object getSessionParameter(FacesContext fc, String parameter)
    {
        return fc.getExternalContext().getSessionMap().get(parameter);
    }

    /**
     * Return the value of the session parameter associated with the facesContext.
     *
     * @param sessionParamaterKey
     * @return
     */
    public static Object getSessionParameter(String sessionParamaterKey)
    {
        return getSessionParameter(FacesContext.getCurrentInstance(), sessionParamaterKey);
    }

    /**
     * Set the parameter key and its value in the session associated with the facesContext.
     *
     * @param parameter
     * @param value
     */
    public static void setSessionParameter(Object parameter, Object value)
    {
        setSessionParameter(FacesContext.getCurrentInstance(), parameter, value);
    }

    /**
     * Updates the parameter key and its value in the session associated with the facesContext.
     *
     * @param parameter
     * @param value
     */
    public static void updateStringSessionParameter(String parameter, Object value)
    {
        ((StringBuffer) getSessionParameter(parameter)).append(value).append(SEPERATOR);
    }

    /**
     * Updates the parameter key and its value in the session associated with the facesContext.
     *
     * @param parameter
     */
    public static String[] getParsedIndividualParametersFromSession(String parameter)
    {
        String[] splitParamValues = new String((getSessionParameter(parameter)).toString()).split(SEPERATOR);

        /* for (int i = 0; i < splitParamValues.length; i++)
        {
            System.out.println("splitParamValues [" + i + "] " + splitParamValues[i]);
        }  */
        return splitParamValues;
    }

    /**
     * Set the parameter key and its value in the session associated with the facesContext.
     *
     * @param fc
     * @param parameter
     * @param value
     */
    public static void setSessionParameter(FacesContext fc, Object parameter, Object value)
    {
        fc.getExternalContext().getSessionMap().put((String) parameter, value);
    }

    /**
     * This method retrieves a bean stored in one of the application's scopes.
     *
     * @param managedBeanName
     * @return an JSF managed object.
     */
    public static Object getJSFManagedBeanInstance(String managedBeanName)
    {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        Application facesApplication = facesCtx.getApplication();
        Object managedBeanInstance = facesApplication.createValueBinding("#{" + managedBeanName.trim() + "}").getValue(facesCtx);
        return managedBeanInstance;
    }


    /**
     * This method returns the "value binding expression" value.
     *
     * @param methodBindingExpression
     * @return JSF evaluated binding value.
     */
    public static Object evalJSFMethodBinding(String methodBindingExpression)
    {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        Application facesApplication = facesCtx.getApplication();
        MethodBinding methodBinding = facesApplication.createMethodBinding(methodBindingExpression.trim(), null);
        Object methodInvokedResult = methodBinding.invoke(facesCtx, new Object[0]);

        return methodInvokedResult;
    }

    /**
     * This method creates an Output Message.
     *
     * @param msgString
     * @return
     */
    public static UIOutput createMessage(String msgString)
    {
        UIOutput message = new UIOutput();
        message.setValue(msgString);
        return message;
    }

    /**
     * This method adds a message to a Panel.
     *
     * @param msgString
     * @return
     */
    public static UIPanel createMessagePanel(String panelId, String msgString)
    {
        UIPanel uiPanel = new UIPanel();
        uiPanel.setId(panelId);
        UIOutput message = new UIOutput();
        message.setValue(msgString);
        uiPanel.getChildren().add(message);
        return uiPanel;
    }


    public static int indent = 0;
    public static final int INDENTSIZE = 2;

    /**
     * This method prints the tree structure of the component.
     *
     * @param comp
     */
    public static void printComponentTree(UIComponent comp)
    {
        printComponentInfo(comp);

        List complist = comp.getChildren();
        if (complist.size() > 0)
        {
            indent++;
        }
        for (int i = 0; i < complist.size(); i++)
        {
            UIComponent uicom = (UIComponent) complist.get(i);
            printComponentTree(uicom);
            if (i + 1 == complist.size())
            {
                indent--;
            }
        }
    }

    /**
     * This method prints the information of a component.
     *
     * @param comp
     */
    public static void printComponentInfo(UIComponent comp)
    {
        if (comp.getId() == null)
        {
            System.out.println("UIViewRoot" + " " + "(" + comp.getClass().getName() + ")");
        } else
        {
            printIndent();
            System.out.println("|");
            printIndent();
            System.out.println(comp.getId() + " " + "(" + comp.getClass().getName() + ")");
        }
    }

    /**
     * Copied directly from PageCode generated by IBM's JSF Implementation.
     * <p/>
     * <p>Return the {@link UIComponent} (if any) with the specified
     * <code>id</code>, searching recursively starting at the specified
     * <code>base</code>, and examining the base component itself, followed
     * by examining all the base component's facets and children.
     * Unlike findComponent method of {@link UIComponentBase}, which
     * skips recursive scan each time it finds a {@link NamingContainer},
     * this method examines all components, regardless of their namespace
     * (assuming IDs are unique).
     *
     * @param base Base {@link UIComponent} from which to search
     * @param id   Component identifier to be matched
     */
    public static UIComponent findComponent(UIComponent base, String id)
    {
        // Is the "base" component itself the match we are looking for?
        if (id.equals(base.getId()))
        {
            return base;
        }
        // Search through our facets and children
        UIComponent kid = null;
        UIComponent result = null;
        Iterator kids = base.getFacetsAndChildren();
        while (kids.hasNext() && (result == null))
        {
            kid = (UIComponent) kids.next();
            if (id.equals(kid.getId()))
            {
                result = kid;
                break;
            }
            result = findComponent(kid, id);
            if (result != null)
            {
                break;
            }
        }
        return result;
    }

    /**
     * Copied directly from PageCode generated by IBM's JSF Implementation.
     * This method returns the UIComponent present in the view root that has a ID equal to the
     * passed to this method.
     */
    public static UIComponent findComponentInRoot(String id)
    {
        UIComponent ret = null;
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null)
        {
            UIComponent root = context.getViewRoot();
            ret = findComponent(root, id);
        }
        return ret;
    }

    /**
     * This method deletes a component from a UIViewRoot.
     *
     * @param componentId
     * @return
     */
    public static boolean deleteComponentInRoot(String componentId)
    {
        boolean deletedComponentSuccessfully = false;
        UIComponent component = findComponentInRoot(componentId);
        synchronized (component)
        {
            if (component != null)
            {
                deletedComponentSuccessfully = component.getParent().getChildren().remove(component);
            } else
            {
                deletedComponentSuccessfully = true;
            }
        }
        return deletedComponentSuccessfully;
    }

    /**
     * Remove an Object from the Session with the "sessionParameter" name
     *
     * @param sessionParameter
     */
    public static void removeSessionParameter(String sessionParameter)
    {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(sessionParameter);
    }

    /**
     * This method is used to indent the way how a component structure is placed.
     */
    public static void printIndent()
    {
        for (int i = 0; i < indent; i++)
        {
            for (int j = 0; j < INDENTSIZE; j++)
            {
                System.out.print(" ");
            }
        }
    }

}