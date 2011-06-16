package com.joe.jsf.web.view;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * Lightweight session bean to hold onto search criteria for pages that have requirements to restore their state upon return (like home
 * page)
 * 
 * @author GRT Creation date: May 9, 2008 12:36:00 PM Copyright (c) 2008 MEDecision, Inc. All rights reserved.
 */
@ManagedBean(name="RestoreViewBean")
@SessionScoped
public class RestoreViewBean {

    /**
     * updates Map with viewState for given viewName key
     * 
     * @param viewName
     * @param viewState
     */
    public void setRestoreViewState(String viewName, Object viewState) {
        getViewSettings().put(viewName, viewState);
    }

    /**
     * clears entry in Map for given viewName key
     * 
     * @param viewName
     */
    public void clearRestoreViewState(String viewName) {
        getViewSettings().remove(viewName);
    }

    /**
     * retrieve viewState for given viewName key
     * 
     * @param viewName
     * @return the viewState
     */
    public Object getRestoreViewState(String viewName) {
        return getViewSettings().get(viewName);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getViewSettings() {
        Map session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        if (!session.containsKey("VIEW_STATE")) {
            session.put("VIEW_STATE", new HashMap<String, Object>());
        }
        return (Map<String, Object>) session.get("VIEW_STATE");
    }
}
