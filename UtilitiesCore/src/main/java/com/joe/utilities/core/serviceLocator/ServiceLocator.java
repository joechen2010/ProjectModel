package com.joe.utilities.core.serviceLocator;

import java.sql.Connection;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.joe.utilities.core.util.ReturnStatus;
import com.joe.utilities.core.validation.Validator;


/**
* The ServiceLocator is a singleton class that provides Spring-enabled services such
* as Spring-configured bean instances, messaging services, and database access services.
* @author Lev Shalevich
* 
* Creation date: 12/12/2006 9 AM
* Copyright (c) 2006-2007 MEDecision, Inc.  All rights reserved.
*/
public abstract class ServiceLocator implements Validator
{
    static Log log = LogFactory.getLog(ServiceLocator.class);

    protected static ServiceLocator defaultServiceLocator;
    protected static ServiceLocator mockServiceLocator;
    
    private static boolean mockFlag;

    /**
     * Method clearInstance. Returns the instance of the service locator. 
     * @return ServiceLocator
     */
    public static void clearInstance()
    {
    	if (defaultServiceLocator != null)
    		defaultServiceLocator.closeContext();
    	mockFlag = false;
    	defaultServiceLocator = null;
    }
    
    /**
     * Method clearInstance. Returns the mock instance of the service locator. 
     */
    public static void clearMockInstance()
    {
    	if (mockServiceLocator != null)
    		mockServiceLocator.closeContext();
    	mockFlag = false;
    	mockServiceLocator = null;
    }
    
    /**
     * Method getInstance. Returns the instance of the service locator. 
     * 
     * PLEASE NOTE: This method has been deprecated. Please read the
     * wiki on Bean Management using Spring (instead of ServiceLocator).
     * 
     * @return ServiceLocator
     */
    @Deprecated
    public static ServiceLocator getInstance()
    {
    	if (mockFlag) {
    		if (mockServiceLocator == null)
    		{
    			mockServiceLocator = getServiceLocatorImplInstance("com.joe.utilities.core.serviceLocator.ServiceLocatorMockImpl");
    		}
        	return mockServiceLocator;
    	} else {
    		if (defaultServiceLocator == null)
    		{
    			synchronized (log){
    				if ( defaultServiceLocator == null ){
    					defaultServiceLocator = getServiceLocatorImplInstance("com.joe.utilities.core.serviceLocator.ServiceLocatorDefaultImpl");
    				}
    			}
    		}
    		return defaultServiceLocator;
    	}
    }
    
    /**
     * Method getMockInstance. Return the mock instance of service locator.
     * @return
     */
    public static ServiceLocator getMockInstance()
    {
    	if (mockServiceLocator == null)
		{
			mockServiceLocator = getServiceLocatorImplInstance("com.joe.utilities.core.serviceLocator.ServiceLocatorMockImpl");
		}
    	mockFlag = true;
    	return mockServiceLocator;
    }
    
    /**
     * @param defaultServiceLocatorImpl
     * @return ServiceLocator
     */
    private static ServiceLocator getServiceLocatorImplInstance(String defaultServiceLocatorImpl) {
    	ServiceLocator serviceLocator = null;
    	String serviceLocatorImpl = System.getProperty("com.joe.utilities.core.serviceLocator.ServiceLocator");
		if (serviceLocatorImpl == null || serviceLocatorImpl.length() == 0)
			serviceLocatorImpl = defaultServiceLocatorImpl;
		
		try
		{
			serviceLocator = (ServiceLocator) Class.forName(serviceLocatorImpl).newInstance();
			log.info("Using ServiceLocator implementation class: " + serviceLocatorImpl);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ServiceLocatorException("Error creating ServiceLocator instance. System property 'com.med.utilities.core.serviceLocator.ServiceLocator' currently set to '"+serviceLocatorImpl+"'", e);
		}
		return serviceLocator;
    }
    
    /**
     * Method initialize. Initializes the service locator from specific context file paths on the classpath.
     * By default, the application context is configured in the service locator constructor using all 
     * applicationContext files on the classpath.  
     * @param contextFiles 
     * @return void
     */
    public abstract void initialize(String [] contextFiles);
    
    /**
     * Method getBeanFactory. Returns a reference to the application context bean factory. 
     * @return BeanFactory
     */
    public abstract BeanFactory getBeanFactory();

    /**
     * Method getApplicationContext. 
     * @return ApplicationContext
     */
    public abstract ApplicationContext getApplicationContext();
    
    /**
     * Method getBean. 
     * 
     * PLEASE NOTE: This method has been deprecated. Please read the
     * wiki on Bean Management using Spring (instead of ServiceLocator).
     * 
     * @param beanName
     * @return Object
     */
    @Deprecated
    public abstract Object getBean(String beanName);
    
    /**
     * Method getJdbcTemplate. 
     * @return JdbcTemplate
     */
    public abstract JdbcTemplate getJdbcTemplate();

    /**
     * Method getConnection. 
     * @throws ServiceLocatorException 
     * @return Connection
     */
    public abstract Connection getConnection () throws ServiceLocatorException;
    
    /**
     * Method getHibernateSession. 
     * @return
     * @throws ServiceLocatorException 
     * @return Session
     */
    public abstract Session getHibernateSession () throws ServiceLocatorException;

    /**
     * Method getHibernateTemplate. 
     * @return HibernateTemplate
     */
    public abstract HibernateTemplate getHibernateTemplate();

    /**
     * Method getMessage. 
     * @param messageName
     * @throws ServiceLocatorException 
     * @return String
     */
    public abstract String getMessage(String messageName) throws ServiceLocatorException;

    /**
     * Method getMessage. 
     * @param messageName
     * @param params
     * @throws ServiceLocatorException 
     * @return String
     */
    public abstract String getMessage(String messageName, Object[] params) throws ServiceLocatorException;
    /**
     * Method getMessage. 
     * @param messageName
     * @param params
     * @param locale
     * @throws ServiceLocatorException 
     * @return String
     */
    public abstract String getMessage(String messageName, Object[] params, Locale locale) throws ServiceLocatorException;
    
    /* (non-Javadoc)
     * @see com.med.utilities.core.validation.Validator#validate()
     */
    public abstract ReturnStatus validate();
    
    /**
     * Close context.
     */
    public abstract void closeContext();

}