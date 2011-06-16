package com.joe.utilities.core.lookup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joe.utilities.core.facade.CacheFlushFacade;
import com.joe.utilities.core.facade.LookupFacade;
import com.joe.utilities.core.facade.LookupFacadeInternal;
import com.joe.utilities.core.facade.LookupFacadeResult;
import com.joe.utilities.core.hibernate.repository.LookupRepository;
import com.joe.utilities.core.serviceLocator.ServiceLocator;
import com.joe.utilities.core.stdfield.domain.IStandardField;
import com.joe.utilities.core.stdfield.domain.IStandardFieldMapping;
import com.joe.utilities.core.util.EvaluationException;
import com.joe.utilities.core.util.ILookupProfile;
import com.joe.utilities.core.util.IStandardFieldLookupProfile;
import com.joe.utilities.core.util.MedException;
import com.joe.utilities.core.util.ReturnStatus;
import com.joe.utilities.core.util.Utils;

/**
*
* Domain model implementation of LookupFacade.  This facade provides
* lookup services from the domain model to the presentation tier.
* @author Dave Ousey
* 
* Creation date: 1/5/2007 9 AM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
@Transactional(readOnly = true)
public class LookupFacadeImpl implements LookupFacade, LookupFacadeInternal
{
    
    /** Reference to lookup repository */
    private LookupRepository lookupRepository;
    
    private LookupManager lookupManager;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private CacheFlushFacade cacheFlushFacade;
    
    /**
     * @param lookupRepository
     */
    public LookupFacadeImpl(LookupRepository lookupRepository)
    {
        super();
        this.lookupRepository = lookupRepository;
    }

    /**
     * @see com.joe.utilities.core.facade.LookupFacade#getLookupList(java.lang.String)
     */
    public LookupFacadeResult getLookupList(String domainClassName)
    {
        LookupFacadeResult facadeResult = new LookupFacadeResult();
        facadeResult.setStatus(new ReturnStatus());
        facadeResult.setLookupList(this.lookupManager.getLookupList(domainClassName));
        return facadeResult;
    }

 
    /**
     * @see com.joe.utilities.core.facade.LookupFacade#getLookupFilteredList(java.lang.String, java.lang.String)
     */
    public LookupFacadeResult getLookupFilteredList(String domainClassName, String lookupVariableName, String filter)
    {
        LookupFacadeResult facadeResult = new LookupFacadeResult();
        facadeResult.setStatus(new ReturnStatus());
        facadeResult.setLookupList(lookupRepository.getLookupFilteredList(domainClassName, lookupVariableName, filter));
        return facadeResult;
    }   
    
    /**
     * @see com.joe.utilities.core.facade.LookupFacade#saveLookups(java.lang.String, java.util.Collection)
     */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { MedException.class })
    public void saveLookups(String domainClassName, Collection<? extends ILookupProfile> lookupCollection) 
    {
        lookupRepository.saveLookups(domainClassName, lookupCollection);
	}

	/**
	 * @see com.joe.utilities.core.facade.LookupFacade#isLookupValueValidInDatabase(java.lang.Class, java.io.Serializable)
	 */
	public boolean isLookupValueValidInDatabase(Class theClass,Serializable key) {
		return this.lookupManager.getLookupMap(theClass.getName()).containsKey(key);
    }
	
	/**
     * @see com.joe.utilities.core.facade.LookupFacade#getStandardFieldLookupList(java.lang.String)
     */
    public List<IStandardFieldLookupProfile> getStandardFieldLookupList(String domainClassName)
    {
    	if (domainClassName == null || domainClassName.length() == 0)
    		throw new IllegalArgumentException("domainClassName cannot be null");
        return lookupRepository.getStandardFieldLookupList(domainClassName);
    }
	
    /**
     * @see com.joe.utilities.core.facade.LookupFacade#saveStandardFieldLookups(java.lang.String, java.util.Collection)
     */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { MedException.class })
    public void saveStandardFieldLookups(String domainClassName, Collection<? extends IStandardFieldLookupProfile> lookupCollection) 
    {
		if (lookupCollection == null || lookupCollection.isEmpty())
			throw new IllegalArgumentException("lookupCollection cannot be null or empty");
		lookupRepository.saveStandardFieldLookups(domainClassName, lookupCollection);

		// Broadcast cache flush to remove lokkup domain table from cache(s)
        CacheFlushFacade cacheFlushFacade = getCacheFlushFacade();
        cacheFlushFacade.broadcastCacheFlushItem(LookupManager.DEFAULT_MCO_ID, LookupManager.NAME + '.' + domainClassName);
		
	}

	/**
	 * @return
	 */
	private CacheFlushFacade getCacheFlushFacade() {
		if(this.applicationContext == null)
		{
			return (CacheFlushFacade) ServiceLocator.getInstance().getBean("cacheFlushFacade");
		}
		else
		{
			return this.cacheFlushFacade;
		}
	}
	
	/**
     * @see com.joe.utilities.core.facade.LookupFacade#saveStandardFieldLookupValue(java.lang.String, IStandardFieldLookupProfile)
     */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { MedException.class, EvaluationException.class })
    public LookupFacadeResult saveStandardFieldLookupValue(String domainClassName, IStandardFieldLookupProfile standardFieldLookup) throws EvaluationException
    {
		if (standardFieldLookup == null)
			throw new IllegalArgumentException("standardFieldLookup cannot be null");
		LookupFacadeResult facadeResult = new LookupFacadeResult();
        ReturnStatus status = new ReturnStatus();
        
        status.appendStatusItems(standardFieldLookup.validate());
		
		facadeResult.setStatus(status);
		if (status.isSuccess())
		{
			lookupRepository.saveStandardFieldLookupValue(domainClassName, standardFieldLookup);
			
			// Broadcast cache flush to remove lokkup domain table from cache(s)
	        CacheFlushFacade cacheFlushFacade = getCacheFlushFacade();
	        cacheFlushFacade.broadcastCacheFlushItem(LookupManager.DEFAULT_MCO_ID, LookupManager.NAME + '.' + domainClassName);
			
		}else
		{
			throw new EvaluationException(status);
		}
        return facadeResult;
    }

	/**
     * @see com.joe.utilities.core.facade.LookupFacade#retrieveStandardFieldLookup(String, String)
     */
	public IStandardFieldLookupProfile retrieveStandardFieldLookup(String domainClassName, String code) {
		if (domainClassName == null || domainClassName.length() == 0)
			throw new IllegalArgumentException("domainClassName cannot be null");
		if (code == null || code.length() == 0)
			throw new IllegalArgumentException("code cannot be null");
		return lookupRepository.getStandardFieldLookupValue(domainClassName, code);
	}


	/**
	 * @see com.joe.utilities.core.facade.LookupFacade#retrieveStandardFieldLookupByDescription(java.lang.String, java.lang.String)
	 */
	public IStandardFieldLookupProfile retrieveStandardFieldLookupByDescription(String domainClassName, String description) {
		if (domainClassName == null || domainClassName.length() == 0)
			throw new IllegalArgumentException("domainClassName cannot be null");
		if (description == null || description.length() == 0)
			throw new IllegalArgumentException("description cannot be null");
		return lookupRepository.getStandardFieldLookupValueByDescription(domainClassName, description);
	}
	
	/**
     * @see com.joe.utilities.core.facade.LookupFacade#getStandardFieldLookupFilteredList(String, String, String)
     */
	public List<IStandardFieldLookupProfile> getStandardFieldLookupFilteredList(String domainClassName, String lookupVariableName, String filter)
	{
		if (domainClassName == null || domainClassName.length() == 0)
			throw new IllegalArgumentException("domainClassName cannot be null");
		if (lookupVariableName == null || lookupVariableName.length() == 0)
			throw new IllegalArgumentException("lookupVariableName cannot be null");
		if (filter == null || filter.length() == 0)
			throw new IllegalArgumentException("filter cannot be null");
        return lookupRepository.getStandardFieldLookupFilteredList(domainClassName, lookupVariableName, filter);
	}
	
	/**
	 * @see com.joe.utilities.core.facade.LookupFacade#isLookupFilteredValueValidInDatabase(String, String, String, String)
	 */
	public boolean isLookupFilteredValueValidInDatabase(String theClassName, String key, String lookupDomainName, String filterValue) {
		if (theClassName == null || theClassName.length() == 0)
			throw new IllegalArgumentException("Class must be defined");
		if (key == null || key.length() == 0)
			throw new IllegalArgumentException("key cannot be null or empty");
		if (lookupDomainName == null || lookupDomainName.length() == 0)
			throw new IllegalArgumentException("lookupDomainName cannot be null or empty");
		if (filterValue == null || filterValue.length() == 0)
			throw new IllegalArgumentException("filterValue cannot be null or empty");
		return lookupRepository.isLookupFilteredValueValidInDatabase(theClassName, key, lookupDomainName, filterValue);
	}
	
	/**
	 * @see com.joe.utilities.core.facade.LookupFacade#getStandardFieldFromMapping(java.lang.String, boolean, java.lang.String, boolean, java.lang.String)
	 */
	public List<IStandardField> getStandardFieldFromMapping(String mappingClassName, boolean isPrimary, String filterValue, boolean isFielterActive, String selectedValue) {
		List<IStandardFieldMapping> mappings = lookupRepository.getStandardFieldMappings(mappingClassName, isPrimary, filterValue);
		List<IStandardField> standardFields = new ArrayList<IStandardField>();
		for (IStandardFieldMapping standardFieldMapping : mappings) {
			if (standardFieldMapping != null) {
				IStandardField standardField = isPrimary ? standardFieldMapping.getSecondaryStandardField() : standardFieldMapping.getPrimaryStandardField();
				if (standardField != null) {
					if (isFielterActive) {
						if (standardFieldMapping.isActive() && standardField.isActive()) {
							standardFields.add(standardField);
						} else if ( (!Utils.isBlankOrNull(standardField.getCode())) &&
								(!Utils.isBlankOrNull(selectedValue)) &&
								standardField.getCode().equals(selectedValue)) {
							standardFields.add(standardField);
						}
					} else {
						standardFields.add(standardField);
					}
				}
			}
		}
		return standardFields;
	}
	
	/**
  	 * @see com.joe.utilities.core.facade.LookupFacade#getStandardFieldMapping(java.lang.String, boolean, java.lang.String)
  	 */
  	public List<IStandardFieldMapping> getStandardFieldMapping(String mappingClassName, boolean isPrimary, String filterValue){
  		return lookupRepository.getStandardFieldMappings(mappingClassName, isPrimary, filterValue);
  	}

  	@Required
	public void setLookupManager(LookupManager lookupManager) {
		this.lookupManager = lookupManager;
	}

  	@Required
	public void setCacheFlushFacade(CacheFlushFacade cacheFlushFacade) {
		this.cacheFlushFacade = cacheFlushFacade;
	}

    public List<IStandardFieldLookupProfile> getStandardFieldLookupList(
                    String domainClassName, String sortColumn,
                    boolean sortAscending, int startRow, int endRow) {
        return lookupRepository.getStandardFieldLookupList(domainClassName,
                        sortColumn, sortAscending, startRow, endRow);
    }

    public Integer getStandardFieldLookupCount(String domainClassName) {
        return lookupRepository.getStandardFieldLookupCount(domainClassName);
    }

	@Override
	public List<IStandardFieldLookupProfile> getStandardFieldLookupListWithPartialFilter(
			String domainClassName,String sortColumn,boolean sortAscending,
			int startRow, int endRow, String filterFieldName, String filterValue,boolean activeOnly) {
        return lookupRepository.getStandardFieldLookupListWithPartialFilter(domainClassName,
        		sortColumn,sortAscending, startRow, endRow, filterFieldName, filterValue,activeOnly);
	}

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { MedException.class, EvaluationException.class })
    public void delete(String domainClassName, ILookupProfile profile) {
        lookupRepository.delete(profile);
        CacheFlushFacade cacheFlushFacade = getCacheFlushFacade();
        cacheFlushFacade.broadcastCacheFlushItem(LookupManager.DEFAULT_MCO_ID, LookupManager.NAME + '.' + domainClassName);
    }

    public <T extends IStandardFieldLookupProfile> List<T> getStandardFieldLookupList(
                    T example, Boolean activeSetting,
                    String sortColumn, boolean sortAscending, int startRow,
                    int endRow) {
        return lookupRepository.getStandardFieldLookupList(example,
                        activeSetting,
                        sortColumn,
                        sortAscending,
                        startRow,
                        endRow);
    }

    public Integer getStandardFieldLookupListCount(
                    IStandardFieldLookupProfile example, Boolean activeSetting) {
        return lookupRepository.getStandardFieldLookupListCount(example,
                        activeSetting);
    }

	@Override
	public List<IStandardFieldLookupProfile> getStandardFieldLookupList(String domainClassName, String txType, String state) {
		if (domainClassName == null || domainClassName.length() == 0)
    		throw new IllegalArgumentException("domainClassName cannot be null");
        return lookupRepository.getStandardFieldLookupList(domainClassName, txType, state);
	}
	
}
