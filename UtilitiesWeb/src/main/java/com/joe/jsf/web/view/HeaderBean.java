package com.joe.jsf.web.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.joe.jsf.helper.ManagedBeanUtility;
import com.joe.jsf.view.HeaderDisplayComponent;
import com.joe.utilities.core.util.Utils;

/**
 * This class is the managed bean representing the state of the header information. Class should be
 * session scoped and never removed from the session for the duration of the session.
 * 
 * @author John J. Jones III
 * @version 1.0
 * 
 * Creation date: Feb 8, 2007 Copyright (c) 2007 MEDecision, Inc. All rights reserved.
 */
@ManagedBean(name="HeaderBean")
@SessionScoped
public class HeaderBean
{
	private String alertsTitle;
	private String alertStyleClass;
	private HeaderDisplayView headerDisplayComponents;
	private String mainTitle;
	private boolean actionAvailable;
	private String actionTitle;
	private String actionBinding;
	private Map<String, String> actionProperties = new HashMap<String, String>(3);
	private String action2StyleClass;
	private String action2OnClickEvent;
	private boolean breadCrumbsRendered;
	private boolean displayDataRendered;
	private boolean homepage;
	private List<HeaderDisplayComponent> displayComponents;
	
	/*
	 * This change involves removing the bindings from Template.xhtml to HeaderBean's 
	 * "enabledContactButton" and "disableContactButton" components.
	 * 
	 * Design intent: 
	 * These bindings only exist to support dynamic changes to the "render" state from 
	 * the HomepageTabBean. 
	 * 
	 * Solution:
	 * To replace these, we introduced Boolean flags to track the render state instead of 
	 * maintaining a reference to the entire component binding. HeaderBean therefore has
	 * render flags that are referenced from the page and maintained by HomepageTabBean.
	 * This was done to reduce the session footprint.
	 * 
	 * Problem:
	 * 
	 * Maintaining a component binding on a session bean such as HeaderBean is dangerous
	 * in that we are holding onto view state of not just the UI components but also to
	 * the parent and grand-parent components that the given binding also reference 
	 * (which adds up ). 
	 * 
	 * Before changing back to HtmlCommandLink or HtmlGraphicImage objects,  one should
	 * check with architecture.
	 */
	//private HtmlCommandLink enableContactButton;
	//private HtmlGraphicImage disableContactButton;
	
	private boolean renderEnableContactButton;
	private boolean renderDisableContactButton;
	
	private Map<String,String> userSetting;

	public HeaderBean()
	{
		setDisplayComponents(new ArrayList<HeaderDisplayComponent>(0));
		setHomepage(false);
		setBreadCrumbsRendered(true);
		setDisplayDataRendered(false);
		userSetting = new HashMap<String, String>();
	}
	
	/**
	 * Gets the main title.
	 * 
	 * @return the mainTitle
	 */
	public String getMainTitle()
	{
		return mainTitle;
	}

	/**
	 * Sets the main title.
	 * 
	 * @param mainTitle the mainTitle to set
	 */
	public void setMainTitle(String mainTitle)
	{
		this.mainTitle = mainTitle;
	}
	
	public boolean isBreadCrumbsRendered()
	{
		return breadCrumbsRendered;
	}

	public void setBreadCrumbsRendered(boolean breadCrumbsRendered)
	{
		this.breadCrumbsRendered = breadCrumbsRendered;
	}
	
	public boolean isDisplayDataRendered()
	{
		return displayDataRendered;
	}

	public void setDisplayDataRendered(boolean displayDataRendered)
	{
		this.displayDataRendered = displayDataRendered;
	}
	
	public boolean isHomepage()
	{
		return homepage;
	}

	public void setHomepage(boolean homepage)
	{
		this.homepage = homepage;
	}
	
	public List<HeaderDisplayComponent> getDisplayComponents()
	{
		return displayComponents;
	}

	public void setDisplayComponents(List<HeaderDisplayComponent> displayComponents)
	{
		this.displayComponents = displayComponents;
	}

	/**
	 * Gets the header display components.
	 * 
	 * @return the headerDisplayComponents
	 */
	public HeaderDisplayView getHeaderDisplayComponents()
	{
		if (headerDisplayComponents != null)
		{
			return headerDisplayComponents;
		}
		else
		{
			return new HeaderDisplayView();
		}
	}

	/**
	 * Sets the header display components.
	 * 
	 * @param headerDisplayComponents the headerDisplayComponents to set
	 */
	public void setHeaderDisplayComponents(HeaderDisplayView headerDisplayComponents)
	{
		this.headerDisplayComponents = headerDisplayComponents;
	}

	/**
	 * Do action.  Dynamically invokes the action
	 * 
	 * @return the string
	 */
	public String doAction()
	{
		Object result = ManagedBeanUtility.invokeMethodBinding(actionBinding);
		if (result != null)
			return result.toString();
		else
			return null;
	}
	
	/**
	 * Checks if is action available.
	 * 
	 * @return true, if is action available
	 */
	public boolean isActionAvailable()
	{
		return actionAvailable;
	}

	/**
	 * Sets the action available.
	 * 
	 * @param actionAvailable the new action available
	 */
	public void setActionAvailable(boolean actionAvailable)
	{
		this.actionAvailable = actionAvailable;
		if (!actionAvailable)
		{
			actionTitle = null;
			actionBinding = null;
			actionProperties.clear();
		}
	}

	/**
	 * Gets the action title.
	 * 
	 * @return the action title
	 */
	public String getActionTitle()
	{
		return actionTitle;
	}

	/**
	 * Sets the action title.
	 * 
	 * @param actionTitle the new action title
	 */
	public void setActionTitle(String actionTitle)
	{
		this.actionTitle = actionTitle;
	}

	/**
	 * Gets the action binding.
	 * 
	 * @return the action binding
	 */
	public String getActionBinding()
	{
		return actionBinding;
	}

	/**
	 * Sets the action binding.
	 * 
	 * @param actionBinding the new action binding
	 */
	public void setActionBinding(String actionBinding)
	{
		this.actionBinding = actionBinding;
	}

	
	/**
	 * Gets the action parameter name1.
	 * 
	 * @return the action parameter name1
	 */
	public String getActionParameterName1()
	{
		return getActionParameter("name", 1);
	}
	
	/**
	 * Gets the action parameter value1.
	 * 
	 * @return the action parameter value1
	 */
	public String getActionParameterValue1()
	{
		return getActionParameter("value", 1);
	}
	
	/**
	 * Gets the action parameter name2.
	 * 
	 * @return the action parameter name2
	 */
	public String getActionParameterName2()
	{
		return getActionParameter("name", 2);
	}
	
	/**
	 * Gets the action parameter value2.
	 * 
	 * @return the action parameter value2
	 */
	public String getActionParameterValue2()
	{
		return getActionParameter("value", 2);
	}
	
	/**
	 * Gets the action parameter name3.
	 * 
	 * @return the action parameter name3
	 */
	public String getActionParameterName3()
	{
		return getActionParameter("name", 3);
	}
	
	/**
	 * Gets the action parameter value3.
	 * 
	 * @return the action parameter value3
	 */
	public String getActionParameterValue3()
	{
		return getActionParameter("value", 3);
	}
	
	/**
	 * getActionParameter.
	 * 
	 * @param nameOrValue the name or value
	 * @param sequenceNumber the sequence number
	 * 
	 * @return the action parameter
	 * 
	 * String
	 */
	private String getActionParameter(String nameOrValue, int sequenceNumber)
	{
		int count = 0;
		for (String name : actionProperties.keySet())
		{
			if (++count == sequenceNumber)
			{
				if ("name".equals(nameOrValue))
					return name;
				else
					return Utils.sameButEmptyIfNull(actionProperties.get(name));
			}
		}
	
		// Return bogus parameter value.
		return ("name".equals(nameOrValue)) ? ("unvaluedName" + sequenceNumber) : ""; 
	}

	/**
	 * getActionProperties.
	 * 
	 * @return Map<String,String>
	 */
	public Map<String, String> getActionProperties()
	{
		return actionProperties;
	}

	/**
	 * Gets the alerts title.
	 * 
	 * @return the alerts title
	 */
	public String getAlertsTitle()
	{
		return alertsTitle;
	}
	
	/**
	 * Gets the alert style class.
	 * 
	 * @return the alert style class
	 */
	public String getAlertStyleClass()
	{
		return alertStyleClass;
	}
	
	/**
	 * Checks if is render main title.
	 * 
	 * @return true, if is render main title
	 */
	public boolean isRenderMainTitle()
	{
		return mainTitle != null && mainTitle.length() > 0;
	}

	/**
	 * Sets the alerts title.
	 * 
	 * @param alertsTitle the new alerts title
	 */
	public void setAlertsTitle(String alertsTitle)
	{
		this.alertsTitle = alertsTitle;
	}

	/**
	 * Sets the alert style class.
	 * 
	 * @param alertStyleClass the new alert style class
	 */
	public void setAlertStyleClass(String alertStyleClass)
	{
		this.alertStyleClass = alertStyleClass;
	}

	/**
	 * Gets the action2 style class.
	 * 
	 * @return the action2 style class
	 */
	public String getAction2StyleClass()
	{
		return action2StyleClass;
	}

	/**
	 * Sets the action2 style class.
	 * 
	 * @param action2StyleClass the new action2 style class
	 */
	public void setAction2StyleClass(String action2StyleClass)
	{
		this.action2StyleClass = action2StyleClass;
	}

	/**
	 * Gets the action2 on click event.
	 * 
	 * @return the action2 on click event
	 */
	public String getAction2OnClickEvent()
	{
		return action2OnClickEvent;
	}

	/**
	 * Sets the action2 on click event.
	 * 
	 * @param action2OnClickEvent the new action2 on click event
	 */
	public void setAction2OnClickEvent(String action2OnClickEvent)
	{
		this.action2OnClickEvent = action2OnClickEvent;
	}
	
	public void clearHeaderData()
	{
		this.setDisplayComponents(new ArrayList<HeaderDisplayComponent>(0));
	}
	
	public void addHeaderDataComponent(HeaderDisplayComponent headerDisplayComponent)
	{
		if(this.getDisplayComponents() != null)
		{
			this.getDisplayComponents().add(headerDisplayComponent);
		}
	}

	/**
	 * @return the contextPath
	 */
	public String getContextPath() {
		ExternalContext servletExternalContext = (ExternalContext) FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest request = (HttpServletRequest) servletExternalContext.getRequest();
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		return basePath;
	}

	/**
	 * @param contextPath the contextPath to set
	 */
	public void setContextPath(String contextPath) {
		// do nothing. this method is to satisfy JSF bean specification
	}
	public boolean isRenderEnableContactButton() {
		return renderEnableContactButton;
	}

	public void setRenderEnableContactButton(boolean renderEnableContactButton) {
		this.renderEnableContactButton = renderEnableContactButton;
	}

	public boolean isRenderDisableContactButton() {
		return renderDisableContactButton;
	}

	public void setRenderDisableContactButton(boolean renderDisableContactButton) {
		this.renderDisableContactButton = renderDisableContactButton;
	}
	/*
	public void setEnableContactButton(HtmlCommandLink enableContactButton) {
		this.enableContactButton = enableContactButton;
	}
	
	public HtmlCommandLink getEnableContactButton() {
		return enableContactButton;
	}
	
	public void setDisableContactButton(HtmlGraphicImage disableContactButton) {
		this.disableContactButton = disableContactButton;
	}
	
	public HtmlGraphicImage getDisableContactButton() {
		return disableContactButton;
	}
	*/


	/**
	 * @return the userSetting
	 */
	public Map<String, String> getUserSetting() {
		return userSetting;
	}

	/**
	 * @param userSetting the userSetting to set
	 */
	public void setUserSetting(Map<String, String> userSetting) {
		this.userSetting = userSetting;
	}
	
}
