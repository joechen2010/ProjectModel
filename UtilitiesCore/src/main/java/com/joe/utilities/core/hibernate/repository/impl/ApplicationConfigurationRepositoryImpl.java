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
 */
package com.joe.utilities.core.hibernate.repository.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.DbTimestampType;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joe.utilities.core.configuration.admin.ApplicationConfiguration;
import com.joe.utilities.core.configuration.admin.domain.IApplicationConfiguration;
import com.joe.utilities.core.hibernate.repository.ApplicationConfigurationRepository;
import com.joe.utilities.core.util.MedException;

/**
 * This the hibernate implementation to handle
 * application configuration settings held
 * within the database.
 * 
 * @author Dave Bartlett 
 * Creation date: 05/22/2007 2:32 PM 
 * Copyright (c) 2007 MEDecision, Inc. All rights reserved.
 */
@Transactional(readOnly = true)
public class ApplicationConfigurationRepositoryImpl extends HibernateDaoSupport implements ApplicationConfigurationRepository{

	private Log log = LogFactory.getLog(ApplicationConfigurationRepositoryImpl.class);
	
    /**
     * Contructor with Spring Hibernate template
     * @param hibernateTemplate spring application framework hibernate template
     */	
	public ApplicationConfigurationRepositoryImpl(HibernateTemplate hibernateTemplate) {
    	if (hibernateTemplate == null)
    		throw new IllegalArgumentException("hibernateTemplate is null");
        setHibernateTemplate(hibernateTemplate);
    }
	
    /**
     * Retrieves an application configuration based upon 
     * the code
     * 
     * @param code
     * @return an application configuration
     */
	public IApplicationConfiguration retreiveApplicationConfigProperty(String code) {

        log.trace("Entering retreiveApplicationConfigurationConfigProperty repository method");
		if (code == null)
            throw new IllegalArgumentException("requestId is null");
		
		//ApplicationConfiguration ac = new ApplicationConfiguration();
		IApplicationConfiguration ac = (IApplicationConfiguration) getHibernateTemplate().get(ApplicationConfiguration.class, code);
		if (ac != null)
			ac.setCode(code);
		return ac;
		
	}
	
    /**
     * Retrieves a list of all application configuration properties 
     * for a category code
     * 
     * @param category code
     * @return a list of application configuration
     */
    @SuppressWarnings("unchecked")
    public List<IApplicationConfiguration> listApplicationConfigurationsByCategoryCode (String categoryCode)
    {
        log.trace("Entering listApplicationConfigurationsByCategoryCode repository method");
        if (categoryCode == null)
            throw new IllegalArgumentException("categoryCode is null");

        List<IApplicationConfiguration> list = getHibernateTemplate().findByNamedParam(
            "from ApplicationConfiguration as appconfig where appconfig.categoryCode = :categoryCode", "categoryCode", categoryCode);

        return list;
    }

    /**
     * Retrieves a list of all application configuration properties 
     * 
     * @return a list of all application configuration
     */
    @SuppressWarnings("unchecked")
    public List<IApplicationConfiguration> listApplicationConfigurations()
    {
        log.trace("Entering listApplicationConfigurations repository method");
        List<IApplicationConfiguration> list = getHibernateTemplate().loadAll(ApplicationConfiguration.class);
        return list;
    }
    
    
    /**
     * Retrieves a list of all the active Home page column configuration properties
     * ordered by tab and then sequence 
     * 
     * @return a list of all Home page column configuration
     */

    
    /**
     * Saves an application configuration
     * 
     * @param IApplicationConfiguration
     * @return IApplicationConfiguration
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = { MedException.class })
    public IApplicationConfiguration saveApplicationConfiguration (IApplicationConfiguration appConfig)
    {
        log.trace("Entering saveApplicationConfiguration repository method");
        if (appConfig == null)
            throw new IllegalArgumentException("appConfig is null");
        getHibernateTemplate().saveOrUpdate(appConfig);
        return appConfig;
    }

    /**
     * Removes an application configuration based upon 
     * the code
     * 
     * @param code
     * @return an application configuration
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = { MedException.class })
	public void removeApplicationConfigProperty(IApplicationConfiguration appConfig)
	{
        log.trace("Entering removeApplicationConfiguration repository method");
		if (appConfig == null)
            throw new IllegalArgumentException("appConfig is null");
		
		getHibernateTemplate().delete(appConfig);
		
	}
    
    public Long retrieveDBTimeOffsetInMilliseconds()
    {		
		DbTimestampType type = new DbTimestampType();
		Timestamp ts = (Timestamp) type.seed((SessionImplementor)getSession());
		Calendar cal  = Calendar.getInstance();		
		
		long actualTimeDiff = cal.getTimeInMillis() - ts.getTime();
		
		//System.out.println("Actual Time Diff in ms(AS - DB)="+actualTimeDiff);
    	return new Long(actualTimeDiff);
    }
    
    public Long retrieveDBTimeInMilliseconds()
    {
    	DbTimestampType type = new DbTimestampType();
		Timestamp ts = (Timestamp) type.seed((SessionImplementor)getSession());
		return ts.getTime();
    }
	

}
