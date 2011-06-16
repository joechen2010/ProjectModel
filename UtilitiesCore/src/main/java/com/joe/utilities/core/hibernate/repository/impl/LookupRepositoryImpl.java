package com.joe.utilities.core.hibernate.repository.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joe.utilities.core.hibernate.repository.LookupRepository;
import com.joe.utilities.core.hibernate.repository.LookupRepository.State;
import com.joe.utilities.core.hibernate.repository.LookupRepository.TX_TYPE;
import com.joe.utilities.core.stdfield.domain.IStandardFieldMapping;
import com.joe.utilities.core.util.ILookupProfile;
import com.joe.utilities.core.util.IStandardFieldLookupProfile;
import com.joe.utilities.core.util.Utils;

/**
* Hibernate-specific database access to lookup table operations.
* @author Dave Ousey
* 
* Creation date: 1/8/2007 4:45 PM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
@Transactional(readOnly = true, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class LookupRepositoryImpl extends HibernateDaoSupport implements LookupRepository
{
    /**
     * Contructor with dependent Session object
     * @param session
     */
    public LookupRepositoryImpl(HibernateTemplate hibernateTemplate) {
        setHibernateTemplate(hibernateTemplate);
    }

    /**
     * @see com.med.utilities.repository.LookupRepository#getLookupList(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<ILookupProfile> getLookupList(String domainClassName)
    {
        try
        {
        	List<ILookupProfile> returnList = getHibernateTemplate().loadAll(Class.forName(domainClassName));
        	return returnList;
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Domain class name '"+domainClassName+"' cannot be mapped to a runtime class.", e);
        }
       
    }

    /**
     * @see com.med.utilities.repository.LookupRepository#getLookupFilteredList(java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<ILookupProfile> getLookupFilteredList(String domainClassName, String lookupVariableName, String filter)
    {

    	List<ILookupProfile> returnList = getHibernateTemplate().findByNamedParam(
            										"from " + domainClassName + " as className where className." + lookupVariableName +" = :filter",
            										"filter", filter);
        return returnList;
    }    
    
    /**
     * @see com.med.utilities.repository.LookupRepository#getLookupValue(java.lang.String, java.lang.String)
     */
    public ILookupProfile getLookupValue(String domainClassName, String code)
    {
        // Locate matching lookup value for given domain class name and code
        List<ILookupProfile> returnList = getLookupList(domainClassName);
        for (ILookupProfile lookup : returnList)
        {
            if (lookup.getCode().equals(code))
                return lookup;
        }
        
        return null;
    }

    /**
     * @see com.med.utilities.repository.LookupRepository#saveLookupValue(java.lang.String, ILookupProfile)
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void saveLookupValue(String domainClassName, ILookupProfile lookupProfile) {
    	throw new RuntimeException("This does not work yet.");
    }
    
    /**
     * @see com.med.utilities.repository.LookupRepository#saveLookups(java.util.Collection)
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void saveLookups(String domainClassName, Collection<? extends ILookupProfile> lookupCollection)
    {
        if (lookupCollection == null)
            return;

        // Note: this logic assumes that we are only inserting new records.
        if (lookupCollection.size() > 0)
        {
        	Session session = getSession(false);
        	for (ILookupProfile lookup : lookupCollection)
        	{
        		session.save(lookup);
        	}
        }
    }
    
    /**
     * @see com.med.utilities.repository.LookupRepository#isLookupValueValidInDatabase(java.lang.Class, java.io.Serializable)
     */
    public boolean isLookupValueValidInDatabase(Class theClass,Serializable key) {
    	
    	Object theObject = getHibernateTemplate().get(theClass, key);
    	
    	if (theObject==null)return false;
    	
    	return true;
    }
    
    /**
     * @see com.med.utilities.repository.LookupRepository#getStandardFieldLookupList(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<IStandardFieldLookupProfile> getStandardFieldLookupList(String domainClassName)
    {
        try
        {
            return getHibernateTemplate().loadAll(Class.forName(domainClassName));
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Domain class name '"+domainClassName+"' cannot be mapped to a runtime class.", e);
        }
    }
    
    /**
     * @see com.med.utilities.repository.LookupRepository#getStandardFieldLookupValue(java.lang.String, java.lang.String)
     */
    public IStandardFieldLookupProfile getStandardFieldLookupValue(String domainClassName, String code)
    {
        // Locate matching lookup value for given domain class name and code
        List<IStandardFieldLookupProfile> returnList = getStandardFieldLookupList(domainClassName);
        for (IStandardFieldLookupProfile lookup : returnList)
        {
            if (lookup.getCode().equals(code))
                return lookup;
        }
        
        return null;
    }

    /**
     * @see com.med.utilities.repository.LookupRepository#getStandardFieldLookupValue(java.lang.String, java.lang.String)
     */
    public IStandardFieldLookupProfile getStandardFieldLookupValueByDescription(String domainClassName, String desc)
    {
        // Locate matching lookup value for given domain class name and desc
        List<IStandardFieldLookupProfile> returnList = getStandardFieldLookupList(domainClassName);
        for (IStandardFieldLookupProfile lookup : returnList)
        {
            if (lookup.getDescription().equalsIgnoreCase(desc))
                return lookup;
        }
        
        return null;
    }
    
    /**
     * @see com.med.utilities.repository.LookupRepository#saveStandardFieldLookups(java.util.Collection)
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void saveStandardFieldLookups(String domainClassName, Collection<? extends IStandardFieldLookupProfile> lookupCollection)
    {
        if (lookupCollection == null)
            return;
        
        if (lookupCollection.size() > 0)
        {
        	Session session = getSession(false);
        	for (IStandardFieldLookupProfile lookup : lookupCollection)
        	{
        		session.saveOrUpdate(lookup);
        		lookup.setNew(false);
        	}
        }
    }
    
    /**
     * @see com.med.utilities.repository.LookupRepository#saveStandardFieldLookupValue(java.lang.String, ILookupProfile)
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void saveStandardFieldLookupValue(String domainClassName, IStandardFieldLookupProfile lookupProfile) {
    	if (lookupProfile == null)
    		return;
    	
    	getSession(false).saveOrUpdate(lookupProfile);
    	lookupProfile.setNew(false);
    	
    }
    
    /**
     * @see com.med.utilities.repository.LookupRepository#getStandardFieldLookupByDescription(java.lang.String, java.lang.String)
     */
    public IStandardFieldLookupProfile getStandardFieldLookupByDescription(String domainClassName, String description)
    {
        // Locate matching lookup value for given domain class name and description
        List<IStandardFieldLookupProfile> returnList = getStandardFieldLookupList(domainClassName);
        for (IStandardFieldLookupProfile lookup : returnList)
        {
            if (lookup.getDescription().equals(description))
                return lookup;
        }
        
        return null;
    }
    
    /**
     * @see com.med.utilities.repository.LookupRepository#evaluateExistingStandardFieldLookup(String, String, String)
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Collection<IStandardFieldLookupProfile> evaluateExistingStandardFieldLookup(String domainClassName, String code, String description)
    {
    	if (domainClassName == null || domainClassName.length() == 0)
    		throw new IllegalArgumentException("domainClassName cannot be null");
    	
    	List<IStandardFieldLookupProfile> results = null;
        try
        {
        	Criteria criteriaQuery = getSession(false).createCriteria(Class.forName(domainClassName));
        	
        	if (code != null && description != null)
                criteriaQuery.add(Restrictions.or(Restrictions.like("code", code), Restrictions.like("description", description)));
            else if (code != null)
                criteriaQuery.add(Restrictions.like("code", code));
            else if (description != null)
                criteriaQuery.add(Restrictions.like("description", description));
            
            results = criteriaQuery.list();
        	
        }catch(ClassNotFoundException e){
        	throw new RuntimeException("Domain class name '"+domainClassName+"' cannot be mapped to a runtime class.", e);
        }
        
        
        // Remove these items from the first level cache to avoid non-unique object exceptions if 
        // Existing standard field lookup is updated in same Hibernate session
        for (IStandardFieldLookupProfile item : results)
        {
            getHibernateTemplate().evict(item);
        }
        return results;
    }
    
    /**
     * @see com.med.utilities.repository.LookupRepository#getStandardFieldLookupFilteredList(String, String, String)
     */
    @SuppressWarnings("unchecked")
    public List<IStandardFieldLookupProfile> getStandardFieldLookupFilteredList(String domainClassName, String lookupVariableName, String filter)
    {
    	List<IStandardFieldLookupProfile> returnList = getHibernateTemplate().findByNamedParam(
				"from " + domainClassName + " as className where className." + lookupVariableName +" = :filter",
				"filter", filter);
    	return returnList;
    }
    
    /**
     * @see com.med.utilities.repository.LookupRepository#isLookupFilteredValueValidInDatabase(String, String, String, String)
     */
    public boolean isLookupFilteredValueValidInDatabase(String theClassName, String key, String lookupDomainName, String filterValue) {
    	
    	try {
    		Object theObject = getSession(false).createCriteria(Class.forName(theClassName), "desc")
    										.add(Restrictions.eq("desc.code" , key))
    										.add(Restrictions.eq("desc."+lookupDomainName, filterValue))
    										.uniqueResult();
    	
    		if (theObject == null) return false;
    	}catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Domain class name '"+theClassName+"' cannot be mapped to a runtime class.", e);
        }
    	
    	return true;
    }
    
    /**
     * @see com.med.utilities.repository.LookupRepository#getStandardFieldMappings(java.lang.String, boolean, java.lang.String)
     */
    public List<IStandardFieldMapping> getStandardFieldMappings(String mappingClassName, boolean isPrimary, String filterValue) {
    	String hql = "from " + mappingClassName + " as className ";
    	if (filterValue != null) {
    		hql += "where className." + (isPrimary ? "primaryStandardField" : "secondaryStandardField") + ".code = ?";
    		return getHibernateTemplate().find(hql, filterValue);
    	} else {
    		return getHibernateTemplate().find(hql);
    	}
    	
    }

    @SuppressWarnings("unchecked")
    public List<IStandardFieldLookupProfile> getStandardFieldLookupList(
                    final String domainClassName,
                    final String sortColumn,
                    final boolean sortAscending,
                    final int startRow,
                    final int endRow) {
        
        return (List<IStandardFieldLookupProfile>) 
        getHibernateTemplate().execute(new HibernateCallback() {
            
            @Override
            public Object doInHibernate(Session session) throws HibernateException,
                            SQLException {
                String hql = "select a from " + domainClassName + " a " +
                    "order by a." + sortColumn + 
                    (sortAscending ? " asc " : " desc ");
                
                Query query = session.createQuery(hql);
                query.setFirstResult(startRow);
                query.setMaxResults((endRow-startRow) + 1);
                return query.list();
            }
        });        
    }
    
    /**
     * looks up standard field list with a filter. The query's "where" clause is "like 'filterValue%'".  
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<IStandardFieldLookupProfile> getStandardFieldLookupListWithPartialFilter(
                     String domainClassName,
                     String sortColumn,
                     boolean sortAscending,
                     int startRow,
                     int endRow,
                     String filterFieldName, 
                     String filterValue,
                     boolean activeOnly) {
    	if (domainClassName == null || domainClassName.length() == 0)
    		throw new IllegalArgumentException("domainClassName cannot be null");
    	
    	List<IStandardFieldLookupProfile> results = null;
    	Criteria criteriaQuery;
    	String likeFilter = filterValue+"%";
        try
        {
        	criteriaQuery = getSession(false).createCriteria(Class.forName(domainClassName));
        	
        	if (filterFieldName != null && filterFieldName.length() != 0){
                criteriaQuery.add(Restrictions.ilike(filterFieldName, likeFilter));
                if (activeOnly)
                	criteriaQuery.add(Restrictions.eq("active",new Boolean(true)));
                
                if (sortAscending)
                	criteriaQuery.addOrder(Order.asc(sortColumn)); 
                else 
                	criteriaQuery.addOrder(Order.desc(sortColumn));
                
                criteriaQuery.setFirstResult(startRow);
                criteriaQuery.setMaxResults(endRow-startRow);
        	}
            
            results = criteriaQuery.list();
        	
        }catch(ClassNotFoundException e){
        	throw new RuntimeException("Domain class name '"+domainClassName+"' cannot be mapped to a runtime class.", e);
        }
        
        
        // Remove these items from the first level cache to avoid non-unique object exceptions if 
        // Existing standard field lookup is updated in same Hibernate session
        for (IStandardFieldLookupProfile item : results)
        {
            getHibernateTemplate().evict(item);
        }
        return results;   
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Integer getStandardFieldLookupCount(String domainClassName) {
        DetachedCriteria criteria = DetachedCriteria.forEntityName(domainClassName);
        criteria.setProjection(Projections.count("code"));
        List result = getHibernateTemplate().findByCriteria(criteria);
        return ((Number)result.get(0)).intValue();
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void delete(ILookupProfile profile) {
        getHibernateTemplate().delete(profile);
    }

    private Criteria createStandardFieldFilterCriteria(Session session,
                    String domainClassName,
                    IStandardFieldLookupProfile example,
                    Boolean activeSetting) {
        final Example ex = Example.create(example);
        ex.enableLike(MatchMode.START);
        ex.ignoreCase();
        ex.excludeProperty("active");            

        Criteria crit = session.createCriteria(domainClassName);
        crit.add(ex);
        // Limitation in hibernate doesn't consider the primary key when doing query by
        // example, so we have to do it ourselves.  argh...
        if (! Utils.isBlankOrNull(example.getCode())) {
            crit.add(Restrictions.ilike("code", example.getCode().toLowerCase() + "%"));
        }
        if (activeSetting != null) {
            crit.add(Restrictions.eq("active", activeSetting));
        }
        return crit;
    }
    
    @Override
    public Integer getStandardFieldLookupListCount(
                    final IStandardFieldLookupProfile example,
                    final Boolean activeSetting) {
        return (Integer) getHibernateTemplate().execute(new HibernateCallback() {            
            @Override
            public Object doInHibernate(Session session) throws HibernateException,
                            SQLException {
                Criteria crit = createStandardFieldFilterCriteria(session, example.getClass().getName(), example, activeSetting);
                crit.setProjection(Projections.count("id"));
                return ((Number)crit.uniqueResult()).intValue();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IStandardFieldLookupProfile> List<T> getStandardFieldLookupList(
                    final T example,
                    final Boolean activeSetting,
                    final String sortColumn,
                    final boolean sortAscending,
                    final int startRow,
                    final int endRow) {
        return (List<T>) getHibernateTemplate().execute(new HibernateCallback() {            
            @Override
            public Object doInHibernate(Session session) throws HibernateException,
                            SQLException {
                Criteria crit = createStandardFieldFilterCriteria(session, example.getClass().getName(), example, activeSetting);
                crit.setFirstResult(startRow);
                crit.setMaxResults(endRow-startRow+1);
                if (sortAscending) {
                    crit.addOrder(Order.asc(sortColumn));
                }
                else {
                    crit.addOrder(Order.desc(sortColumn));
                }
                return crit.list();
            }
        });
    }

	@Override
	public List<IStandardFieldLookupProfile> getStandardFieldLookupList(String domainClassName, String txType, String state) {
		try
        {
            final Class<?> clazz = Class.forName(domainClassName);
            final DetachedCriteria dc = DetachedCriteria.forClass(clazz, "field");
    
            /*
             * first handle whether the field is to be active or inactive
             */
            if(state.equals(State.ACTIVE))
            {
            	dc.add(Property.forName("field.active").eq(Boolean.TRUE));
            }
            else if(state.equals(State.INACTIVE)) {
            	dc.add(Property.forName("field.active").eq(Boolean.FALSE));
            }
            else
            {
            	// return all
            }
            
            /*
             * then handle whether it's supposed to be MEDICAL or BH
             */
            if(txType.equals(TX_TYPE.BH)) {
            	dc.add(Property.forName("field.behavioralHealth").eq(Boolean.TRUE));
            } else if(txType.equals(TX_TYPE.MEDICAL)) {
            	dc.add(Property.forName("field.behavioralHealth").in(Arrays.asList(new Boolean[]{Boolean.FALSE, null})));
            }
            
            
			return (List<IStandardFieldLookupProfile>) getHibernateTemplate().findByCriteria(dc);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Domain class name '"+domainClassName+"' cannot be mapped to a runtime class.", e);
        }
	}
    
}
