package com.joe.utilities.core.serviceLocator;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.joe.utilities.core.util.ReturnStatus;



/**
* Mock implementation of ServiceLocator abstract class.  This class allows test to replace the default Spring-loaded
* ServiceLocatorDefaultImpl class with this class so that Spring and database mechanics can be avoided.
* 
* To use, in your JUnit test, set System.property("com.med.utilities.core.serviceLocator.ServiceLocator")
*  to this class' name "com.med.utilities.core.serviceLocator.ServiceLocatorMockImpl"
* 
* @author Dave Ousey
* 
* Creation date: 04/06/2007 9 AM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public class ServiceLocatorMockImpl extends ServiceLocator
{
    static Log log = LogFactory.getLog(ServiceLocatorMockImpl.class);

    /** Holds mocked references to requested beans */
    private Map<String, Object> beanMap = new HashMap<String, Object>(8);
    private Map<String, String> messages = new HashMap<String, String>(8);
    
    /**
     * Initialize the service locator using all *applicationContext.xml files on the current classpath. 
     */
    protected ServiceLocatorMockImpl ()
    {

    }

    /**
     * Method getBean. 
     * @param beanName
     * @return Object
     */
    public Object getBean(String beanName)
    {
        if (!beanMap.containsKey(beanName))
            throw new RuntimeException("Mock bean map does not contain bean for key: " + beanName);
        
        return beanMap.get(beanName);
    }
    
    /**
     * Method setBean. 
     * @param beanName
     * @param bean
     */
    public void setBean(String beanName, Object bean)
    {
        beanMap.put(beanName, bean);
    }
    
    /**
     * Method initialize. 
     * @param contextFiles 
     * @return void
     */
    public void initialize(String [] contextFiles)
    {   
        nag();
    }
    
    /**
     * Method getBeanFactory. Returns a reference to the application context bean factory. 
     * @return BeanFactory
     */
    public BeanFactory getBeanFactory()
    {
        nag();
        return null;
    }

    /**
     * Method getApplicationContext. 
     * @return ApplicationContext
     */
    public ApplicationContext getApplicationContext()
    {
        nag();
        return null;
    }

    
    /**
     * Method getJdbcTemplate. 
     * @return JdbcTemplate
     */
    public JdbcTemplate getJdbcTemplate()
    {
       nag();
       return null;
    }

    /**
     * Method getConnection. 
     * @throws ServiceLocatorException 
     * @return Connection
     */
    public Connection getConnection () throws ServiceLocatorException
    {
        nag();
        return null;
    }

    /**
     * Method getHibernateSession. 
     * @return
     * @throws ServiceLocatorException 
     * @return Session
     */
    public Session getHibernateSession () throws ServiceLocatorException
    {
        nag();
        return null;
    }

    /**
     * Method getHibernateTemplate. 
     * @return HibernateTemplate
     */
    public HibernateTemplate getHibernateTemplate()
    {
        nag();
        return null;
    }

    /**
     * Method getMessage. 
     * @param messageName
     * @throws ServiceLocatorException 
     * @return String
     */
    public String getMessage(String messageName) throws ServiceLocatorException
    {
        return messages.get(messageName);
    }

    /**
     * Method getMessage. 
     * @param messageName
     * @param params
     * @throws ServiceLocatorException 
     * @return String
     */
    public String getMessage(String messageName, Object[] params) throws ServiceLocatorException
    {
        nag();
        return null;
    }

    /**
     * Method getMessage. 
     * @param messageName
     * @param params
     * @param locale
     * @throws ServiceLocatorException 
     * @return String
     */
    public String getMessage(String messageName, Object[] params, Locale locale) throws ServiceLocatorException
    {
        nag();
        return null;
    }
    
    /**
     * Method nag.  
     * @return void
     */
    private void nag()
    {
        throw new RuntimeException("No obvious mock implementation for getBeanFactory so once is not implemented in this class.  Either create something interesting or subclass this class.");
    }

    /* (non-Javadoc)
     * @see com.med.utilities.core.serviceLocator.ServiceLocator#validate()
     */
    @Override
    public ReturnStatus validate() {
        ReturnStatus status = new ReturnStatus();
        return status;
    }

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}

	@Override
	public void closeContext()
	{
		beanMap.clear();
		messages.clear();
	}
    
	
	
}
