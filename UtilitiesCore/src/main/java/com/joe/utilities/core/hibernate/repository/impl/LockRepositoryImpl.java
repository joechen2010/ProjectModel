package com.joe.utilities.core.hibernate.repository.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
* Hibernate-specific database access to lock table operations.
* @author Dave Ousey
* 
* Creation date: 10/16/2007 11:43 PM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public class LockRepositoryImpl extends HibernateDaoSupport
{
    private static Log log = LogFactory.getLog(LockRepositoryImpl.class);
    
    /**
     * Contructor with dependent Session object
     * @param session
     */
    public LockRepositoryImpl(HibernateTemplate hibernateTemplate) {
        setHibernateTemplate(hibernateTemplate);
    }


        
    /**
     * @see com.med.utilities.repository.LockRepository#createPhysicalLock(org.hibernate.Session, java.lang.String)
     */
   
    
    /**
     * @see com.med.utilities.repository.LockRepository#deletePhysicalLock(java.lang.String)
     */
    public void deletePhysicalLock(String lockName)
    {
    	getSession(false).createQuery("delete from PhysicalLock where tableName = :lockName")
    					 .setParameter("lockName", lockName)
    					 .executeUpdate();
    }


    /**
     * @see com.med.utilities.repository.LockRepository#deleteSessionLocks(java.lang.String)
     */
    public void deleteSessionLocks(String sessionID)
    {
        getSession(false).createQuery("delete LogicalLock where sessionID = :sessionID")
                           .setParameter("sessionID", sessionID)
                           .executeUpdate();
    }

    /**
     * @see com.med.utilities.repository.LockRepository#deleteSessionLocksForTable(java.lang.String, java.lang.String)
     */
    public void deleteSessionLocksForTable(String tableName, String sessionID)
    {
        if (tableName == null)
            throw new IllegalArgumentException("Required table name parameter is missing in call to UtilityResourceHelper - deleteSessionLocksForTable task.");
        
        getSession(false).createQuery("delete LogicalLock where tableName = :tableName and sessionID = :sessionID")
                           .setParameter("tableName", tableName)
                           .setParameter("sessionID", sessionID)
                           .executeUpdate();    
    }


    
}
