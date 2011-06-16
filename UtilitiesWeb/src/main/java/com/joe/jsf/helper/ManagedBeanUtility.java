package com.joe.jsf.helper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.application.Application;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.joe.utilities.core.configuration.admin.domain.IApplicationConfiguration;
import com.joe.utilities.core.configuration.admin.facade.IApplicationConfigurationFacade;
import com.joe.utilities.core.facade.LookupFacade;
import com.joe.utilities.core.facade.LookupFacadeResult;
import com.joe.utilities.core.serviceLocator.ServiceLocator;
import com.joe.utilities.core.util.ILookupProfile;
import com.joe.utilities.core.util.IStandardFieldLookupProfile;

/**
 * This class contains static utility methods commonly used by managed beans.
 *
 * @author John J. Jones III
 * @version 1.0
 *          <p/>
 *          Creation date: Mar 14, 2007 Copyright (c) 2007 MEDecision, Inc. All
 *          rights reserved.
 */

public class ManagedBeanUtility {

	private final static String CONTACT_INFO_BEAN = "#{ContactInfoBean}";
	
	/**
	 * Comparator for selectItem lists
	 */
	private static Comparator<SelectItem> selectItemListComparator = new Comparator<SelectItem>() {
		public int compare(SelectItem o1, SelectItem o2) {
			if (o1 == null && o2 == null)
				return 0;
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			int nameComparator = o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase());
			return nameComparator;
		}
	};

	/**
	 * Method retrieves collection of values from Lookup Facade and returns JSF
	 * HtmlSelectOneMenu object that will be bound to the jsp dropdown
	 * component.
	 *
	 * @param className
	 * @return
	 */
	public static HtmlSelectOneMenu getLookupList(String className) {

		// set up jsf components
		HtmlSelectOneMenu dropDown = new HtmlSelectOneMenu();
		UISelectItems items = new UISelectItems();

		List<SelectItem> lookupList = retrieveLookupList(className, true);

		// apply collection to JSF menu component
		items.setValue(lookupList);
		dropDown.getChildren().add(items);

		return dropDown;
	}

	/**
	 * Method retrieveLookupList.
	 *
	 * @param className
	 * @return List<ILookupProfile>
	 */
	public static List<ILookupProfile> retrieveLookupList(String className) {
		return retrieveLookupListObjects(className);
	}

	/**
	 * Method retrieveLookupList. Method retrieves collection of values from
	 * Lookup Facade and returns List of SelectItems
	 *
	 * @param className
	 * @param insertBlank
	 * @return List<SelectItem>
	 */
	private static List<SelectItem> retrieveLookupList(String className, boolean insertBlank) {
		List<SelectItem> lookupList = new ArrayList<SelectItem>();

		// get ServiceLocator instance
		ServiceLocator svcLocator = ServiceLocator.getInstance();
		// get instance of lookup facade from service locator
		LookupFacade lookupFacade = (LookupFacade) svcLocator.getBean("lookupFacade");
		// retrieve result by calling lookupfacade
		LookupFacadeResult lookupResult = lookupFacade.getLookupList(className);

		// List<SelectItem> lookupList = new ArrayList<SelectItem>();

		if (insertBlank) {
			// insert blank select item first
			lookupList.add(new SelectItem("", ""));
		}

		// iterate through result list and create collection of SelectItem
		// components
		for (ILookupProfile element : lookupResult.getLookupList()) {
			lookupList.add(new SelectItem(element.getCode(), element.getDescription()));
		}

		return lookupList;
	}

	/**
	 * Method retrieveLookupListObjects.
	 *
	 * @param className
	 * @return List<ILookupProfile>
	 */
	private static List<ILookupProfile> retrieveLookupListObjects(String className) {
		// get ServiceLocator instance
		ServiceLocator svcLocator = ServiceLocator.getInstance();
		// get instance of lookup facade from service locator
		LookupFacade lookupFacade = (LookupFacade) svcLocator.getBean("lookupFacade");
		// retrieve result by calling lookupfacade
		LookupFacadeResult lookupResult = lookupFacade.getLookupList(className);

		return lookupResult.getLookupList();
	}

	/**
	 * Method getLookupSelectItemList. Method returns list of lookup values
	 * based on passed in classname.
	 *
	 * @param className
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getLookupSelectItemList(String className) {

		return retrieveLookupList(className, false);
	}

	public static List<SelectItem> getLookupSelectItemList(String className, boolean addBlank) {
		return retrieveLookupList(className, addBlank);
	}

	/**
	 * Method getRequestParameter. Retrieves a request parameter corresponding
	 * to the given key
	 *
	 * @param key
	 * @return String
	 */
	public static String getRequestParameter(String key) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
	}


	/**
	 * Returns a record from a domain table for a specified table name and code.
	 *
	 * @param className
	 *            the domain table name.
	 * @param code
	 *            the specified code to find in the domain table.
	 * @return An ILookupProfile object representing a code and description for
	 *         a given domain table.
	 */
	public static ILookupProfile getLookupItem(String className, String code) {
		if (code == null) {
			return null;
		}

		// Instantiate a ServiceLocator instance.
		ServiceLocator svcLocator = ServiceLocator.getInstance();

		// Get an instance of lookup facade from service locator.
		LookupFacade lookupFacade = (LookupFacade) svcLocator.getBean("lookupFacade");

		// Retrieve the result by calling the lookupfacade's lookup list.
		LookupFacadeResult lookupResult = lookupFacade.getLookupList(className);
		List<ILookupProfile> lookupItemList = lookupResult.getLookupList();
		for (ILookupProfile lookupItem : lookupItemList) {
			if (code.equalsIgnoreCase(lookupItem.getCode())) {
				return lookupItem;
			}
		}
		return null;
	}

	/**
	 * Method returns object of specified binding from session
	 *
	 * @param bindingValue
	 * @return
	 */
	public static Object getBindingObject(String bindingValue) {

		FacesContext ctx = FacesContext.getCurrentInstance();
		Application app = ctx.getApplication();
		return app.createValueBinding(bindingValue).getValue(ctx);
	}

	/**
	 * Method invokeMethodBinding. Handles method invocation of a JSF EL method
	 * expression.
	 *
	 * @param methodExpression
	 * @return Object : Return value.
	 */
	public static Object invokeMethodBinding(String methodExpression) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return facesContext.getApplication().createMethodBinding(methodExpression, null).invoke(facesContext, null);
	}

	/**
	 * Method getApplicationConfiguration. Return the applicaiton configuration
	 * object associated with the given name.
	 *
	 * @param configurationName
	 * @return IApplicationConfiguration
	 */
	public static IApplicationConfiguration getApplicationConfiguration(String configurationName) {
		// Validate required parameter
		if (configurationName == null || configurationName.length() == 0) {
			throw new IllegalArgumentException(
					"configurationName is a required parameter in call to ManagedBeanUtility.getApplicationConfiguration");
		}

		// Call configuration facade to retrieve application configuration
		// object.
		IApplicationConfigurationFacade appConfigFacade = (IApplicationConfigurationFacade) ServiceLocator.getInstance()
				.getBean("applicationConfigFacade");
		IApplicationConfiguration config = appConfigFacade.getApplicationConfiguration(configurationName);
		return config;
	}

	/**
	 * Method getApplicationConfigurationValue. Return the applicaiton
	 * configuration object's value associated with the given name.
	 *
	 * @param configurationName
	 * @return String
	 */
	public static String getApplicationConfigurationValue(String configurationName) {
		IApplicationConfiguration configuration = getApplicationConfiguration(configurationName);
		if (configuration != null) {
			return configuration.getValue();
		} else {
			return null;
		}
	}

	public static HtmlSelectOneMenu getLookupList(String className, boolean insertBlank) {

		// set up jsf components
		HtmlSelectOneMenu dropDown = new HtmlSelectOneMenu();
		UISelectItems items = new UISelectItems();

		List<SelectItem> lookupList = retrieveLookupList(className, insertBlank);

		// apply collection to JSF menu component
		items.setValue(lookupList);
		dropDown.getChildren().add(items);

		return dropDown;
	}


	public static String getApplicationServerHostName() {
		String machineName = null;

		try {
			machineName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}

		return machineName;
	}

	public static String getApplicationServerIPAddress() {
		String ipAddr = null;

		try {
			ipAddr = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}

		return ipAddr;
	}

	/**
	 * Method retrieves collection of values from Lookup Facade of
	 * StandardFieldLookupProfile and returns JSF HtmlSelectOneMenu object that
	 * will be bound to the jsp dropdown component.
	 *
	 * @param className
	 * @return
	 */
	public static HtmlSelectOneMenu getStandardFieldLookupList(String className, boolean activeOnly) {
		// set up jsf components
		HtmlSelectOneMenu dropDown = new HtmlSelectOneMenu();
		UISelectItems items = new UISelectItems();

		List<SelectItem> lookupList = retrieveStandardFieldLookupList(className, true, activeOnly);

		// apply collection to JSF menu component
		items.setValue(lookupList);
		dropDown.getChildren().add(items);

		return dropDown;
	}

	/**
	 * Method retrieves collection of values from Lookup Facade of
	 * StandardFieldLookupProfile and returns list of JSF SelectItem object that
	 * will be bound to the jsp dropdown component.
	 *
	 * @param className
	 * @return
	 */
	public static List<SelectItem> getStandardFieldLookupSelectList(String className, boolean activeOnly) {
		List<SelectItem> lookupList = retrieveStandardFieldLookupList(className, false, activeOnly);
		return lookupList;
	}

	/**
	 * Method retrieveLookupList. Method retrieves collection of values from
	 * Lookup Facade and returns List of SelectItems
	 *
	 * @param className
	 * @param insertBlank
	 * @return List<SelectItem>
	 */
	private static List<SelectItem> retrieveStandardFieldLookupList(String className, boolean insertBlank, boolean activeOnly) {
		List<SelectItem> lookupList = new ArrayList<SelectItem>(16);

		// get ServiceLocator instance
		ServiceLocator svcLocator = ServiceLocator.getInstance();
		// get instance of lookup facade from service locator
		LookupFacade lookupFacade = (LookupFacade) svcLocator.getBean("lookupFacade");
		// retrieve result by calling lookupfacade
		List<IStandardFieldLookupProfile> lookupResult = lookupFacade.getStandardFieldLookupList(className);

		if (insertBlank) {
			// insert blank select item first
			lookupList.add(new SelectItem("", ""));
		}

		// loop through result list and create collection of SelectItem
		// components
		if (activeOnly) {
			for (IStandardFieldLookupProfile lookup : lookupResult) {
				if (lookup.isActive())
					lookupList.add(new SelectItem(lookup.getCode(), lookup.getDescription()));

			}
		} else {
			for (IStandardFieldLookupProfile lookup : lookupResult) {

				lookupList.add(new SelectItem(lookup.getCode(), lookup.getDescription()));

			}
		}

		Collections.sort(lookupList, selectItemListComparator);

		return lookupList;
	}

	/**
	 * Returns a StandardFieldLookupProfile from a domain table for a specified
	 * table name and code.
	 *
	 * @param className
	 *            the domain table name.
	 * @param code
	 *            the specified code to find in the domain table.
	 * @return An IStandardFieldLookupProfile object representing a code,
	 *         description and active for a given domain table.
	 */
	public static IStandardFieldLookupProfile getStandardFieldLookupItem(String className, String code) {
		if (code == null) {
			return null;
		}

		// Instantiate a ServiceLocator instance.
		ServiceLocator svcLocator = ServiceLocator.getInstance();

		// Get an instance of lookup facade from service locator.
		LookupFacade lookupFacade = (LookupFacade) svcLocator.getBean("lookupFacade");

		// Retrieve the result by calling the lookupfacade's lookup list.
		List<IStandardFieldLookupProfile> lookupResult = lookupFacade.getStandardFieldLookupList(className);
		for (IStandardFieldLookupProfile lookupItem : lookupResult) {
			if (code.equalsIgnoreCase(lookupItem.getCode())) {
				return lookupItem;
			}
		}
		return null;
	}

	/**
	 * Method retrieves collection of values from Lookup Facade of
	 * StandardFieldLookupProfile and returns List SelectItem object.
	 *
	 * @param className
	 * @param activeOnly
	 * @param selectedCodes
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getStandardFieldLookupList(String className, boolean activeOnly, String[] selectedCodes) {
		List<SelectItem> lookupList = null;
		if (selectedCodes == null || selectedCodes.length <= 0) {
			lookupList = retrieveStandardFieldLookupList(className, true, activeOnly);
		} else {
			lookupList = retrieveStandardFieldLookupList(className, true, activeOnly, selectedCodes);
		}
		return lookupList;
	}

	/**
	 * Method retrieveLookupList. Method retrieves collection of values from
	 * Lookup Facade and returns List of SelectItems
	 *
	 * @param className
	 * @param insertBlank
	 * @param activeOnly
	 * @param selectedCodes
	 * @return List<SelectItem>
	 */
	private static List<SelectItem> retrieveStandardFieldLookupList(String className, boolean insertBlank,
			boolean activeOnly, String[] selectedCodes) {
		List<SelectItem> lookupList = new ArrayList<SelectItem>(16);

		// get ServiceLocator instance
		ServiceLocator svcLocator = ServiceLocator.getInstance();
		// get instance of lookup facade from service locator
		LookupFacade lookupFacade = (LookupFacade) svcLocator.getBean("lookupFacade");
		// retrieve result by calling lookupfacade
		List<IStandardFieldLookupProfile> lookupResult = lookupFacade.getStandardFieldLookupList(className);

		if (insertBlank) {
			// insert blank select item first
			lookupList.add(new SelectItem("", ""));
		}

		// loop through result list and create collection of SelectItem
		// components
		if (activeOnly) {
			for (IStandardFieldLookupProfile lookup : lookupResult) {
				if (lookup.isActive())
					lookupList.add(new SelectItem(lookup.getCode(), lookup.getDescription()));
				else {
					for (int i = 0; i < selectedCodes.length; i++) {
						if (lookup.getCode().equalsIgnoreCase(selectedCodes[i])) {
							lookupList.add(new SelectItem(lookup.getCode(), lookup.getDescription()));
							break;
						}
					}
				}
			}
		} else {
			for (IStandardFieldLookupProfile lookup : lookupResult) {

				lookupList.add(new SelectItem(lookup.getCode(), lookup.getDescription()));

			}
		}

		Collections.sort(lookupList, selectItemListComparator);

		return lookupList;
	}

	/**
	 * looks up standard field list with a filter. The query's "where" clause is
	 * "like 'filterValue%'". Only active records will be returned.
	 *
	 * @param domainClassName
	 * @param sortColumn
	 *            The column to sort by
	 * @param sortAscending
	 *            Should we sort ascending
	 * @param startRow
	 * @param endRow
	 * @param filterFieldName
	 * @param filterValue
	 * @param activeOnly
	 * @return
	 */
	public static List<SelectItem> getStandardFieldLookupListWithPartialFilter(String domainClassName, String sortColumn,
			boolean sortAscending, int startRow, int endRow, String filterFieldName, String filterValue, boolean activeOnly) {
		List<SelectItem> lookupList = new ArrayList();

		// get ServiceLocator instance
		ServiceLocator svcLocator = ServiceLocator.getInstance();
		// get instance of lookup facade from service locator
		LookupFacade lookupFacade = (LookupFacade) svcLocator.getBean("lookupFacade");
		// retrieve result by calling lookupfacade
		List<IStandardFieldLookupProfile> lookupResult = lookupFacade.getStandardFieldLookupListWithPartialFilter(
				domainClassName, filterFieldName, true, startRow, endRow, filterFieldName, filterValue, activeOnly);

		for (IStandardFieldLookupProfile lookup : lookupResult) {
			lookupList.add(new SelectItem(lookup.getCode(), lookup.getDescription()));
		}

		return lookupList;
	}

	/**
	 * Method retrieves collection of values from Lookup Facade of
	 * StandardFieldLookupProfile and returns list of JSF SelectItem object that
	 * will be bound to the jsp dropdown component.
	 *
	 * @param className
	 * @param insertBlank
	 * @param activeOnly
	 * @param selectedCodes
	 * @return
	 */
	public static List<SelectItem> getStandardFieldLookupSelectList(String className, boolean insertBlank,
			boolean activeOnly, String[] selectedCodes) {
		List<SelectItem> lookupList = null;
		if (selectedCodes == null || selectedCodes.length <= 0) {
			lookupList = retrieveStandardFieldLookupList(className, insertBlank, activeOnly);
		} else {
			lookupList = retrieveStandardFieldLookupList(className, insertBlank, activeOnly, selectedCodes);
		}
		return lookupList;
	}

	/**
	 * Method retrieveStandardFieldLookupFilteredList. Method retrieves
	 * collection of values from Lookup Facade and returns List of SelectItems
	 * based on variable name and filter specified
	 *
	 * @param className
	 * @param variableName
	 * @param filter
	 * @param insertBlank
	 * @param activeOnly
	 * @param selectedCodes
	 * @return List<SelectItem>
	 */
	private static List<SelectItem> retrieveStandardFieldLookupFilteredList(String className, String variableName,
			String filter, boolean insertBlank, boolean activeOnly, String[] selectedCodes) {
		List<SelectItem> lookupList = new ArrayList<SelectItem>(16);

		// get ServiceLocator instance
		ServiceLocator svcLocator = ServiceLocator.getInstance();
		// get instance of lookup facade from service locator
		LookupFacade lookupFacade = (LookupFacade) svcLocator.getBean("lookupFacade");
		// retrieve result by calling lookupfacade
		List<IStandardFieldLookupProfile> lookupResult = lookupFacade.getStandardFieldLookupFilteredList(className,
				variableName, filter);

		if (insertBlank) {
			// insert blank select item first
			lookupList.add(new SelectItem("", ""));
		}

		// loop through result list and create collection of SelectItem
		// components
		if (activeOnly) {
			for (IStandardFieldLookupProfile lookup : lookupResult) {
				if (lookup.isActive())
					lookupList.add(new SelectItem(lookup.getCode(), lookup.getDescription()));
				else {
					for (int i = 0; i < selectedCodes.length; i++) {
						if (lookup.getCode().equalsIgnoreCase(selectedCodes[i])) {
							lookupList.add(new SelectItem(lookup.getCode(), lookup.getDescription()));
							break;
						}
					}
				}
			}
		} else {
			for (IStandardFieldLookupProfile lookup : lookupResult) {

				lookupList.add(new SelectItem(lookup.getCode(), lookup.getDescription()));

			}
		}

		Collections.sort(lookupList, selectItemListComparator);

		return lookupList;
	}

	/**
	 * Method retrieveStandardFieldLookupFilteredList. Method retrieves
	 * collection of values from Lookup Facade and returns List of SelectItems
	 * based on variable name and filter specified
	 *
	 * @param className
	 * @param variableName
	 * @param filter
	 * @param insertBlank
	 * @return List<SelectItem>
	 */
	private static List<SelectItem> retrieveStandardFieldLookupFilteredList(String className, String variableName,
			String filter, boolean insertBlank, boolean activeOnly) {
		List<SelectItem> lookupList = new ArrayList<SelectItem>(16);

		// get ServiceLocator instance
		ServiceLocator svcLocator = ServiceLocator.getInstance();
		// get instance of lookup facade from service locator
		LookupFacade lookupFacade = (LookupFacade) svcLocator.getBean("lookupFacade");
		// retrieve result by calling lookupfacade
		List<IStandardFieldLookupProfile> lookupResult = lookupFacade.getStandardFieldLookupFilteredList(className,
				variableName, filter);

		if (insertBlank) {
			// insert blank select item first
			lookupList.add(new SelectItem("", ""));
		}

		// loop through result list and create collection of SelectItem
		// components
		if (activeOnly) {
			for (IStandardFieldLookupProfile lookup : lookupResult) {
				if (lookup.isActive())
					lookupList.add(new SelectItem(lookup.getCode(), lookup.getDescription()));

			}
		} else {
			for (IStandardFieldLookupProfile lookup : lookupResult) {

				lookupList.add(new SelectItem(lookup.getCode(), lookup.getDescription()));

			}
		}

		Collections.sort(lookupList, selectItemListComparator);

		return lookupList;
	}

	/**
	 * Method retrieves collection of values from Lookup Facade of
	 * StandardFieldLookupProfile and returns List SelectItem object based on
	 * variable name and filter specified.
	 *
	 * @param className
	 * @param variableName
	 * @param filter
	 * @param insertBlank
	 * @param activeOnly
	 * @param selectedCodes
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getStandardFieldLookupFilteredList(String className, String variableName, String filter,
			boolean activeOnly, String[] selectedCodes) {
		List<SelectItem> lookupList = null;
		if (selectedCodes == null || selectedCodes.length <= 0) {
			lookupList = retrieveStandardFieldLookupFilteredList(className, variableName, filter, true, activeOnly);
		} else {
			lookupList = retrieveStandardFieldLookupFilteredList(className, variableName, filter, true, activeOnly,
					selectedCodes);
		}
		return lookupList;
	}

	/**
	 * Method retrieves collection of values from Lookup Facade of
	 * StandardFieldLookupProfile and returns list of JSF SelectItem object that
	 * will be bound to the jsp dropdown component based on filter.
	 *
	 * @param className
	 * @param variableName
	 * @param filter
	 * @param insertBlank
	 * @param activeOnly
	 * @param selectedCodes
	 * @return
	 */
	public static List<SelectItem> getStandardFieldLookupFilteredSelectList(String className, String variableName,
			String filter, boolean insertBlank, boolean activeOnly, String[] selectedCodes) {
		List<SelectItem> lookupList = null;
		if (selectedCodes == null || selectedCodes.length <= 0) {
			lookupList = retrieveStandardFieldLookupFilteredList(className, variableName, filter, insertBlank, activeOnly);
		} else {
			lookupList = retrieveStandardFieldLookupFilteredList(className, variableName, filter, insertBlank, activeOnly,
					selectedCodes);
		}
		return lookupList;
	}

	/**
	 * Method is for a workaround on the homepage in which the select items in
	 * the list will contain desc/desc pairs instead of the normal code/desc.
	 * This is needed for filtering expediency.
	 *
	 * @param className
	 * @return
	 */
	public static List<SelectItem> getStandardFieldLookupSelectItemListForHomepage(String className) {
		return getStandardFieldLookupSelectItemListForHomepage(className, false);
	}

	/**
	 * Method is for a workaround on the homepage in which the select items in
	 * the list will contain desc/desc pairs instead of the normal code/desc.
	 * This is needed for filtering expediency.
	 *
	 * @param className
	 * @param includeInactive
	 *            if true, include standard field values that are inactive
	 * @return
	 */
	public static List<SelectItem> getStandardFieldLookupSelectItemListForHomepage(String className, boolean includeInactive) {
		List<SelectItem> lookupList = new ArrayList<SelectItem>(3);

		// get instance of lookup facade from service locator
		LookupFacade lookupFacade = (LookupFacade) ServiceLocator.getInstance().getBean("lookupFacade");
		// retrieve result by calling lookupfacade
		List<IStandardFieldLookupProfile> lookupResult = lookupFacade.getStandardFieldLookupList(className);

		// loop through result list and create collection of SelectItem
		// components
		for (IStandardFieldLookupProfile lookup : lookupResult) {
			if (includeInactive) {
				lookupList.add(new SelectItem(lookup.getDescription(), lookup.getDescription()));
			} else if (lookup.isActive()) {
				lookupList.add(new SelectItem(lookup.getDescription(), lookup.getDescription()));
			}
		}

		Collections.sort(lookupList, selectItemListComparator);

		return lookupList;
	}


	/***
	 * Get a managed bean by name (automatically adds the expression syntax
	 * markers.
	 *
	 * @param beanName
	 * @return
	 */
	public static Object getManagedBean(String beanName) {
		FacesContext fc = FacesContext.getCurrentInstance();
		ELContext elc = fc.getELContext();
		ExpressionFactory ef = fc.getApplication().getExpressionFactory();
		return ef.createValueExpression(elc, getJsfEl(beanName), Object.class).getValue(elc);
	}

	/***
	 * Clears the value of a managed bean.
	 *
	 * @param beanName
	 * @return
	 */
	public static void clearManagedBean(String beanName) {
		FacesContext fc = FacesContext.getCurrentInstance();
		ELContext elc = fc.getELContext();
		ExpressionFactory ef = fc.getApplication().getExpressionFactory();
		ef.createValueExpression(elc, getJsfEl(beanName), Object.class).setValue(elc, null);

	}

	/***
	 * Gets the EL for a managed bean.
	 *
	 * @param value
	 * @return
	 */
	public static String getJsfEl(String value) {
		return "#{" + value + "}";
	}
	
}
