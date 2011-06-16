package com.joe.utilities.core.facade;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.joe.utilities.core.stdfield.domain.IStandardField;
import com.joe.utilities.core.stdfield.domain.IStandardFieldMapping;
import com.joe.utilities.core.util.EvaluationException;
import com.joe.utilities.core.util.ILookupProfile;
import com.joe.utilities.core.util.IStandardFieldLookupProfile;

/**
* Interface to access domain facade layer for lookup table domain services.
* @author Dave Ousey
* 
* Creation date: 1/5/2007 9 AM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public interface LookupFacade
{
    /**
     * Method getLookupList.  This method will retrieve a lookup table contents for the given domain class.  
     * @param domainClassName
     * @return LookupFacadeResult
     */
    public LookupFacadeResult getLookupList(String domainClassName);
    
    /**
     * Method getLookupList.  This method will retrieve a lookup table contents for the given domain class.
     * A filtered value is passed in so that the results returned are a subset of the whole by the filter.  
     * @param domainClassName
     * @return LookupFacadeResult
     */  
    public LookupFacadeResult getLookupFilteredList(String domainClassName, String lookupVariableName, String filter);

    /**
     * This method will commit 1 or more lookup objects for the given domain class.  
     * @param domainClassName 
     * @param lookupCollection
     * @return
     */
    public void saveLookups(String domainClassName, Collection<? extends ILookupProfile> lookupCollection);
    
    public boolean isLookupValueValidInDatabase(Class theClass,Serializable key);
    
    /**
     * Method getStandardFieldLookupList.  This method will retrieve a standard field lookup table contents for the given domain class.  
     * @param domainClassName
     * @return List<IStandardFieldLookupProfile>
     */
    public List<IStandardFieldLookupProfile> getStandardFieldLookupList(String domainClassName);
    
    /**
     * This method will commit 1 or more standard field lookup objects for the given domain class.  
     * @param domainClassName 
     * @param lookupCollection
     * @return LookupFacadeResult
     */
    public void saveStandardFieldLookups(String domainClassName, Collection<? extends IStandardFieldLookupProfile> lookupCollection);
    
    /**
     * This method will commit 1 standard field lookup object for the given domain class.  
     * @param domainClassName 
     * @param standardFieldLookup
     * @return LookupFacadeResult
     */
    public LookupFacadeResult saveStandardFieldLookupValue(String domainClassName, IStandardFieldLookupProfile standardFieldLookup) throws EvaluationException;
    
    /**
     * Returns a single standard field lookup object for the given domain class and code
     * @param domainClassName
     * @param code
     * @return
     */
    public IStandardFieldLookupProfile retrieveStandardFieldLookup(String domainClassName, String code);
    
    /**
     * Returns a single standard field lookup object for the given domain class and code
     * @param domainClassName
     * @param desc
     * @return
     */
    public IStandardFieldLookupProfile retrieveStandardFieldLookupByDescription(String domainClassName, String description);      
    /**
     * Method getStandardFieldLookupFilteredList.  This method will retrieve a standard field lookup table contents for the given domain class.
     * A filtered value is passed in so that the results returned are a subset of the whole by the filter.  
     * @param domainClassName
     * @param lookupVariableName
     * @param filter
     * @return List<IStandardFieldLookupProfile>
     */  
    public List<IStandardFieldLookupProfile> getStandardFieldLookupFilteredList(String domainClassName, String lookupVariableName, String filter);
    
    /**
     * Method isLookupFilteredValueValidInDatabase. Returns boolean on if the key is found for the filterValue as the lookupDomainName
     * @param theClassName
     * @param key
     * @param lookupDomainName
     * @param filterValue
     * @return boolean
     */
    public boolean isLookupFilteredValueValidInDatabase(String theClassname, String key, String lookupDomainName, String filterValue);
    
    /**
     * Get standard field from mapping by mapping class name, filter value, whether filter active and selected value.
     * @param mappingClassName
     * @param isPrimary
     * @param filterValue
     * @param isFielterActive
     * @param selectedValue
     * @return
     */
    List<IStandardField> getStandardFieldFromMapping(String mappingClassName, boolean isPrimary, String filterValue, boolean isFielterActive, String selectedValue);
    
    /**
     * Get standard field mapping by mapping class name and filter value.
     * @param mappingClassName
     * @param isPrimary
     * @param filterValue
     * @return List<IStandardFieldMapping>
     */
    List<IStandardFieldMapping> getStandardFieldMapping(String mappingClassName, boolean isPrimary, String filterValue);

    /***
     * Do a standard field lookup for a range of items
     * @param domainClassName The domain class to query
     * @param sortColumn The column to sort by
     * @param sortAscending Should we sort ascending
     * @param startRow The starting row we want to retrieve (zero based)
     * @param endRow The ending row we want to retrieve (zero based, inclusive)
     * @return
     */
    List<IStandardFieldLookupProfile> getStandardFieldLookupList(String domainClassName,
                    String sortColumn,
                    boolean sortAscending,
                    int startRow,
                    int endRow);
    
    /**
     * looks up standard field list with a filter. The query's "where" clause is "like 'filterValue%'". 
     * @param domainClassName
     * @param sortColumn The column to sort by
     * @param sortAscending Should we sort ascending
     * @param startRow
     * @param endRow
     * @param filter
     * @param filterValue
     * @param boolean activeOnly
     * @return
     */
    List<IStandardFieldLookupProfile> getStandardFieldLookupListWithPartialFilter(String domainClassName,
		            String sortColumn,
		            boolean sortAscending,
		            int startRow,
                    int endRow, 
                    String filter, 
                    String filterValue,
                    boolean activeOnly);
    /***
     * Get the count of items in the standard field lookup list.
     * @param domainClassName The domain class to query
     * @return
     */
    Integer getStandardFieldLookupCount(String domainClassName);

    /***
     * Do a standard field lookup for a range of items based on an example object
     * @param example An example object to use to formulate the query
     * @param activeSetting Whether or not to consider the active flag, and if so (a non-null value), what it should be.
     * @param sortColumn The column to sort by
     * @param sortAscending Should we sort ascending
     * @param startRow The starting row we want to retrieve (zero based)
     * @param endRow The ending row we want to retrieve (zero based, inclusive)
     * @return
     */
    <T extends IStandardFieldLookupProfile> List<T> getStandardFieldLookupList(T example,
                    Boolean activeSetting,
                    String sortColumn,
                    boolean sortAscending,
                    int startRow,
                    int endRow);

    /***
     * Do a count of standard field lookup based on an example object
     * @param example An example object to use to formulate the query
     * @param activeSetting Whether or not to consider the active flag, and if so (a non-null value), what it should be.
     * @return
     */
   Integer getStandardFieldLookupListCount(IStandardFieldLookupProfile example,
                   Boolean activeSetting);

	public List<IStandardFieldLookupProfile> getStandardFieldLookupList(String domainClass,  String txType, String state);

}
