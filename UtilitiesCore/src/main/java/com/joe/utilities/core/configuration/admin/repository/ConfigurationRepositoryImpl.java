/*
 * MEDecision, Inc. Software Development Infrastructure, Version 1.0
 *
 * Copyright (c) 2007 MEDecision, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of 
 * MEDecision, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with MEDecision, Inc.
 *
 * MEDecision, Inc MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. MEDecision, Inc SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
  * Created on Apr 9, 2007
 *
 */
package com.joe.utilities.core.configuration.admin.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.joe.utilities.core.configuration.GlobalConfigurationException;
import com.joe.utilities.core.configuration.Globals;
import com.joe.utilities.core.configuration.admin.ApplicationConfiguration;
import com.joe.utilities.core.configuration.admin.IPropertyValue;
import com.joe.utilities.core.configuration.admin.PropertyValue;
import com.joe.utilities.core.listhandler.SearchResult;
import com.joe.utilities.core.serviceLocator.ServiceLocator;

/**
 * @author rrichard
 *
 */
public class ConfigurationRepositoryImpl  extends HibernateDaoSupport implements ConfigurationRepository, ApplicationContextAware{
    
    private Log log = LogFactory.getLog(ConfigurationRepositoryImpl.class);
    private PropertiesConfiguration defaultProperty = null;
    private PropertiesConfiguration overrideProperty = null;
    private Configuration systemProperty = null;
	private ApplicationContext applicationContext;
    
    /**
    *
    */
   public ConfigurationRepositoryImpl() throws GlobalConfigurationException {

       this.systemProperty = new SystemConfiguration();
       this.defaultProperty = Globals.loadDefaultProperties();

       PropertiesConfiguration customerGlobalProperties = Globals.loadCustomerGlobalProperties();
       if (customerGlobalProperties != null) {
       	this.overrideProperty = customerGlobalProperties;
       } else {
       	this.overrideProperty = new PropertiesConfiguration();
       }
       this.overrideProperty.setAutoSave(true);
   }
    
    /**
     * Contructor with dependent Session object
     * @param session
     */
    public ConfigurationRepositoryImpl(HibernateTemplate hibernateTemplate) throws GlobalConfigurationException {
        this();
        setHibernateTemplate(hibernateTemplate);
    }
        
    public IPropertyValue getProperty(String key) {
        
        if (key == null) {
            return null;
        }
        
        PropertyValue property = null;
        if (defaultProperty.containsKey(key)) {
            property = new PropertyValue(key, getOverrideValue(key));
            property.setSystemValue(getSystemValue(key));
            property.setDefaultValue(getDefaultValue(key));
        }
        return property;
        
    }

    /* (non-Javadoc)
     * @see com.med.configuration.admin.repository.ConfigurationRepository#getProperties(java.lang.String, java.lang.Long, java.lang.Long)
     */
    public SearchResult<IPropertyValue> getProperties(String filter, Long startIndex, Long maxResults) {
        SearchResult<IPropertyValue> result = new SearchResult<IPropertyValue>(20);
        Iterator iKeys = getDefaultKeys();
        int iSize = 0;
        while (iKeys.hasNext()) {
            String key = (String) iKeys.next();
            if (filter == null || (filter != null && key.contains(filter))) {
                PropertyValue property = new PropertyValue(key, getOverrideValue(key));
                property.setSystemValue(getSystemValue(key));
                property.setDefaultValue(getDefaultValue(key));
                result.add(property);
                iSize++;
            }
        }
        result.setQryCount(iSize);
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see com.med.configuration.admin.repository.ConfigurationRepository#saveProperty(com.med.configuration.admin.IPropertyValue)
     */
    public void saveProperty(IPropertyValue property) {
        log.debug(".saveProperty called property:=" + property);
        if (property != null) {
            this.overrideProperty.setProperty(property.getKey(), property.getValue());
        }
    }

    /* (non-Javadoc)
     * @see com.med.configuration.admin.repository.ConfigurationRepository#deleteOverride(com.med.configuration.admin.IPropertyValue)
     */
    public void deleteOverride(IPropertyValue property) {
        log.debug(".deleteOverride called property:=" + property);
        if (property != null) {
            this.overrideProperty.clearProperty(property.getKey());
        }
        
    }

    public IPropertyValue getSystemAssignedID(String key) {
        
    	final List<String> interactionSysIds = new ArrayList<String>();
    	interactionSysIds.add("SYSGENINTERACTIONID");
    	interactionSysIds.add("SYSGENTOPICID");

        log.debug("ConfigurationRepositoryImpl.getSystemAssignedID() called");
        if (key == null) {
            return null;
        }
        
        // Create pessimistic lock to guard against multi-threaded access to the increment logic below
        
        PropertyValue property = null;
        ApplicationConfiguration appConfig = (ApplicationConfiguration)getHibernateTemplate().get(ApplicationConfiguration.class, key, LockMode.UPGRADE);
        if (appConfig != null)
        {
        	int updatedValue = Integer.parseInt(appConfig.getValue());
        	appConfig.setValue(String.valueOf(++updatedValue));
        	getHibernateTemplate().saveOrUpdate(appConfig);
        	if(interactionSysIds.contains(key)) {
        		property = new PropertyValue(key,appConfig.getValue());
        	} else {
        		property = new PropertyValue(key, "T"+appConfig.getValue());
        	}
        }

        return property;
        
    }


    /**
     * @return
     */
    public Iterator getDefaultKeys() {
        return this.defaultProperty.getKeys();
    }
    
    /**
     * @param key
     * @return
     */
    public String getSystemValue(String key) {
        Object obj = this.systemProperty.getProperty(key);
        String returnValue = null;
        if (obj != null) {
            returnValue = obj.toString();
        }
        return returnValue;
    }
    
    /**
     * @param key
     * @return
     */
    public String getOverrideValue(String key) {
        Object obj = this.overrideProperty.getProperty(key);
        String returnValue = null;
        if (obj != null) {
            returnValue = obj.toString();
        }
        return returnValue;
    }
    
    /**
     * @param key
     * @return
     */
    public String getDefaultValue(String key) {
        Object obj = this.defaultProperty.getProperty(key);
        String returnValue = null;
        if (obj != null) {
            returnValue = obj.toString();
        }
        return returnValue;
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}


}
