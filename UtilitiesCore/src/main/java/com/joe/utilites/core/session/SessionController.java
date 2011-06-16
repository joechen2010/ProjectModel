package com.joe.utilites.core.session;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class contains static methods that aid in session management.  
 * 
 * @author       John J. Jones III
 * @version      1.0
 * 
 * Creation date: Apr 20, 2007
 * Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
 */

public class SessionController {
	private static Log log = LogFactory.getLog(SessionController.class);
    private static Collection<String> restrictedList = createRestrictedList();


    /**
	 * Constructor
	 */
	private SessionController() {
		
	}
	
	/**
	 * Method removes object from session map based on provided key
	 * 
	 * @param beanName
	 */
	public static void removeSessionAttribute(String key) {
		removeAttributeFromSession(key);
	}
	
	/**
	 * Method cleans (nullifies and removes the reference) all objects from the session 
	 * 	except for those which are restricted (hardcoded in this class) or passed via 
	 * 	variable argument parameters.
	 * 
	 * @param sessionKeysToKeep is a variable String argument which represents the key strings for
	 * 	objects that should be left in session.
	 */
	public static void cleanSession(String... sessionKeysToKeep) {
		// obtain instance of session map
		if (FacesContext.getCurrentInstance()==null || FacesContext.getCurrentInstance().getExternalContext()==null) 
			return;
		
		List<String> sessionKeysToKeepList = new ArrayList<String>(Arrays.asList(sessionKeysToKeep));
		sessionKeysToKeepList.add(SessionConstants.SESSION_BEAN_HOMEPAGE_TASKS_SETTINGS);
		sessionKeysToKeepList.add(SessionConstants.SESSION_BEAN_HOMEPAGE_REQUESTS_SETTINGS);
		sessionKeysToKeepList.add(SessionConstants.SESSION_BEAN_HOMEPAGE_TOPICS_SETTINGS);
		sessionKeysToKeepList.add(SessionConstants.SESSION_BEAN_HOMEPAGE_PROGRAMS_SETTINGS);
		sessionKeysToKeepList.add(SessionConstants.SESSION_BEAN_FONT_SETTINGS);
		sessionKeysToKeepList.add(SessionConstants.IM_UTILITY_BEAN);
		sessionKeysToKeepList.add(SessionConstants.CONTACT_INFO_BEAN);
		
		Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

		Iterator mapIter = sessionMap.keySet().iterator();		
		while (mapIter.hasNext()) {
			String key = (String)mapIter.next();
			// if the key is not in the restricted key list and is not specified to be kept
			if (!isRestricted(key,restrictedList)&&!containsKeyInStringArray(key, sessionKeysToKeepList)) {
				removeAttributeFromSession(key);
			}
		}				
	}
	
	private static void removeAttributeFromSession(String key) {
		Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		// remove object from session
		sessionMap.remove(key);
		
		log.debug("removing object from session with key='"+key+"'");
	}
	
	/**
	 * Method simply iterates through session map and prints to console the referenced 
	 * 	values of the keys and objects.
	 * 
	 * !! SHOULD ONLY BE USED IN TESTING - DO NOT LEAVE FOR PRODUCTION DEPLOYMENT !!
	 * @deprecated
	 */
	@Deprecated
	public static void printSessionEntriesToConsole() {
		
		// btain instance of session map
		if (FacesContext.getCurrentInstance()==null) {
			log.fatal("------------>faces context is null");
			return;
		}
		if (FacesContext.getCurrentInstance().getExternalContext()==null) {
			log.fatal("------------>external context is null");
			return;
		}
		Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		// get iterator of sess
		Iterator mapIter = sessionMap.keySet().iterator();
		
		while (mapIter.hasNext()) {
			String key = (String)mapIter.next();
			Object value = sessionMap.get(key);
			log.fatal("session: key='" +key+ "' value='"+value+"'");
			
		}
		
	}
	
	public static void printRequestParameterMapToConsole() {
		Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		Iterator mapIter = requestMap.keySet().iterator();
		
		while (mapIter.hasNext()) {
			String key = (String)mapIter.next();
			Object value = requestMap.get(key);
			log.fatal("request: key='" +key+ "' value='"+value+"'");
		}
	}
	
	/**
	 * Convenience method to check if the key is in the restricted list of keys
	 * @param key
	 * @param restrictedList
	 * @return
	 */
	private static boolean isRestricted(String key, Collection<String> restrictedList) {
		
		if ( restrictedList.contains(key) || 
		     ( key != null && key.endsWith(":sessionboundserver" ))	
		) {
			return true;
		}

		else return false;
	}
	
	
	/**
	 * Method returns true if the key is contained in the passed string array;
	 * 	otherwise, false.
	 * 
	 * @param key
	 * @param keyArray
	 * @return
	 */
	private static boolean containsKeyInStringArray(String key, List<String> keyList) {
		
		if (keyList!=null) {
			for (String keyString : keyList) {
				if (keyString.equals(key)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Method created static collection of restricted session keys
	 * KEEPING ELEMENTS IN SESSION COULD HAVE PERFORMANCE IMPACT.  Do not keep elements in
	 * session without talking to architecture. 
	 * NOTE: THIS COULD HAVE PERFORMANCE IMPACTS.  
	 * 		 DO NOT ADD TO THIS WITHOUT CONSULTING ARCHITECTS
	 * NOTE: if you get permission to add to this list, you must comment explicitly 
	 * 		 what the item is needed for in the application
	 * @return A collection of keys that should not be removed from session
	 */
	private static Collection<String> createRestrictedList() {
		restrictedList = new ArrayList<String>(12);
		restrictedList.add("jsf_sequence");
		restrictedList.add(SessionConstants.MANAGED_BEAN_HEADER);
		restrictedList.add("medX39z");
		
		// required to retain security credentials
		restrictedList.add("WebUser");
		restrictedList.add("javax.faces.request.charset");
		restrictedList.add(SessionListener.NAME);
		
		// below needed for keeping state of bread crumb
		restrictedList.add(SessionConstants.MANAGED_BEAN_BREAD_CRUMB);
        restrictedList.add(SessionConstants.ATTRIBUTES_IN_SESSION);
        restrictedList.add(SessionConstants.MANAGED_BEAN_RESTORE_VIEW);
        
        // below needed to ensure client letter browser window remains open
        restrictedList.add("ISCLBROWSERINITIALIZED");
		restrictedList.add("VIEW_STATE");

		// don't add this to restrictedList		
		// Adding this to the restricted list causes problems because we are 
		// removing items that object uses.  As a result we gets lots of NullPointerExceptions.
		//restrictedList.add("com.sun.faces.application.StateManagerImpl.SerialId");

		// Below needed for keeping state of messages even during partial submits
		restrictedList.add(SessionConstants.MANAGED_BEAN_MESSAGES_SUPPORT);
		
		//CERMe proprietary notice displayed constant only displayed once per logged on session
		restrictedList.add(SessionConstants.CERMe_PROPRIETARY_NOTICE_DISPLAYED);
		restrictedList.add(SessionConstants.CERMe_PROPRIETARY_NOTICE_DISPLAYED);
		
		
		/* Adding these items to the restricted list blows session memory.
		restrictedList.add("com.icesoft.faces.webapp.http.servlet.SessionDispatcher$Monitor");
		restrictedList.add("com.icesoft.faces.webapp.http.servlet.MainSessionBoundServlet");
		restrictedList.add("icesoft_javascript_required_libs_897241");
		restrictedList.add("positive_session_timeout");
		restrictedList.add("com.sun.faces.logicalViewMap");
		restrictedList.add("javax.servlet.jsp.jstl.fmt.request.charset");
		*/

        return restrictedList;
	}
	
	/**
	 * Method adds attribute to session map using external context.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static void addSessionAttribute(String key, Object value) {
		if (key==null || key.equals("")) {
			throw new RuntimeException("key cannot be null or empty");
		}
		Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.put(key, value);
	}
	
	/**
     * Method getSessionAttribute. Retrieves a session attribute corresponding to the given key
     *
     * @param key
     * @return Object
     */
    public static Object getSessionAttribute(String key)
    {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key);
    }
	
	
	/**
	 * Method retrieves attribute by name from the session and 
	 * subsequently nullifies the object and removes from session.
	 * 
	 * CAUTION: if you are simply attempting to obtain an object from 
	 * session but wish it to remain in the session map, use 
	 * ManagedBeanUtility.getSessionAttribute() instead.
	 * 
	 * @param key
	 * @return
	 */
	public static Object retrieveSessionAttributeAndRemoveFromSession(String key) {
		Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		Object sessionObject = sessionMap.get(key);
		
		if (sessionObject!=null) {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(key);
		}
		return sessionObject;
	}
    
    /**
     * Method getSessionID. Returns the session ID of the HTTP Session.
     * @return String
     */
    public static String getSessionID()
    {
        return ((HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();
    }
	
}
