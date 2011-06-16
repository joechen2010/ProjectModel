package com.joe.utilities.core.hibernate.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.joe.utilities.core.stdfield.domain.IStandardFieldMapping;
import com.joe.utilities.core.util.ILookupProfile;
import com.joe.utilities.core.util.IStandardFieldLookupProfile;

/**
* Lookup repository.  This repository is used to perform database operations on
* lookup table values.
* @author Dave Ousey
* 
* Creation date: 1/8/2007 4:30 PM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public interface LookupRepository
{
	
	public enum State {
		ALL, ACTIVE, INACTIVE;
	}
	
	public enum TX_TYPE {
		ALL, MEDICAL,BH;
		
		public String toString() {
			return name();
		}
	}
	
    /**
     * Method getLookupList. This method will return a list of lookup profile objects from the repository.  
     * @param domainClassName
     * @return List<ILoopkupProfile>
     */
    public List<ILookupProfile> getLookupList(String domainClassName);
    
    /**
     * Method getLookupFilteredList. This method will return a list of lookup profile objects from the repository,
     * filtered out by the filter parameter.  
     * @param domainClassName
     * @param lookupVariableName
     * @param filter
     * @return List<ILoopkupProfile>
     */
    public List<ILookupProfile> getLookupFilteredList(String domainClassName, String lookupVariableName, String filter);    
    
    /**
     * Method getLookupValue.  This method will return the instance of the specified value if found.  
     * If not found, a null will be returned.  
     * @param domainClassID
     * @param code
     * @return ILoopkupProfile
     */
    public ILookupProfile getLookupValue(String domainClassName, String code);
    
    /**
     * Persists a new row to the lookup table for the given domain class.
     * 
     * @param domainClassName
     * @param lookupProfile
     * @return void
     */
    public void saveLookupValue(String domainClassName, ILookupProfile lookupProfile);
    
    /**
     * Method saveLookups. Persist collection of lookup profile objects.
     * @param domainClassName
     * @param lookupCollection 
     * @return void
     */
    public void saveLookups(String domainClassName, Collection<? extends ILookupProfile> lookupCollection);
    
    
    /**
     * Method verfies whether or not the object is perisstent in the database
     * 
     * @param theClass
     * @param key
     * @return
     */
    public boolean isLookupValueValidInDatabase(Class theClass,Serializable key);
    
    /**
     * Method getStandardFieldLookupList. This method will return a list of standard field lookup profile objects from the repository.  
     * @param domainClassName
     * @return List<IStandardFieldLoopkupProfile>
     */
    public List<IStandardFieldLookupProfile> getStandardFieldLookupList(String domainClassName);
    
    /**
     * Method getStandardFieldLookupValue.  This method will return the instance of the specified value if found.  
     * If not found, a null will be returned.  
     * @param domainClassName
     * @param code
     * @return IStandardFieldLoopkupProfile
     */
    public IStandardFieldLookupProfile getStandardFieldLookupValue(String domainClassName, String code);

    /**
     * Method getStandardFieldLookupValueByDescription.  
     * This method will return the instance of the specified value if found by description passed in..  
     * If not found, a null will be returned.  Note the comparison is done case insensitively.
     * @param domainClassName
     * @param desc
     * @return IStandardFieldLoopkupProfile
     */
    public IStandardFieldLookupProfile getStandardFieldLookupValueByDescription(String domainClassName, String desc);
    
    /**
     * Method saveStandardFieldLookups. Persist collection of standard field lookup profile objects.
     * @param domainClassName
     * @param lookupCollection 
     * @return void
     */
    public void saveStandardFieldLookups(String domainClassName, Collection<? extends IStandardFieldLookupProfile> lookupCollection);
    
    /**
     * Persists a new row to the standard field lookup table for the given domain class.
     * 
     * @param domainClassName
     * @param lookupProfile
     * @return void
     */
    public void saveStandardFieldLookupValue(String domainClassName, IStandardFieldLookupProfile lookupProfile);
    
    /**
     * Method getStandardFieldLookupByDescription This method will return the instance of the specified description if found. 
     * @param domainClassName
     * @param description
     * @return IStandardFieldLookupProfile
     */
    public IStandardFieldLookupProfile getStandardFieldLookupByDescription(String domainClassName, String description);
    
    /**
     * Returns a collection of standard field lookup items based on the class defined by the domainClassName parameter
     * for evaluating uniqueness check of standard field lookup items
     * @param domainClassName
     * @param code
     * @param description
     * @return Collection<IStandardFieldLookupProfile>
     */
    public Collection<IStandardFieldLookupProfile> evaluateExistingStandardFieldLookup(String domainClassName, String code, String description);
    
    /**
     * Method getStandardFieldLookupFilteredList. This method will return a list of standard field lookup profile objects from the repository,
     * filtered out by the filter parameter.  
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
    public boolean isLookupFilteredValueValidInDatabase(String theClassName, String key, String lookupDomainName, String filterValue);
    
    /**
     * Get standard field mappings by class name and filter value.
     * @param mappingClassName
     * @param isPrimary
     * @param filterValue
     * @return
     */
    List<IStandardFieldMapping> getStandardFieldMappings(String mappingClassName, boolean isPrimary, String filterValue);
    
    
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

    /**
	 * looks up standard field list with a filter. The query's "where" clause is "like 'filterValue%'". 
	 * @param domainClassName
	 * @param sortColumn The column to sort by
     * @param sortAscending Should we sort ascending
	 * @param startRow
	 * @param endRow
	 * @param filterFieldName
	 * @param filterValue
	 * @return
	 */
    List<IStandardFieldLookupProfile> getStandardFieldLookupListWithPartialFilter(String domainClassName,
		            String sortColumn,
		            boolean sortAscending,
                    int startRow,
                    int endRow,
                    String filterFieldName,
                    String filterValue,
                    boolean activeOnly);
    
    /***
     * Get the count of items in the standard field lookup list.
     * @param domainClassName The domain class to query
     * @return
     */
    Integer getStandardFieldLookupCount(String domainClassName);

    /***
     * Delete a lookup profile object.
     * 
     * @param profile
     */
    void delete(ILookupProfile profile);

	public List<IStandardFieldLookupProfile> getStandardFieldLookupList(String domainClassName, String txType, String state);

}
