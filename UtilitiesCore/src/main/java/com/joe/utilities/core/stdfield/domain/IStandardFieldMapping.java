package com.joe.utilities.core.stdfield.domain;



/**
* Business interface for the standard field table mapping.
* @author GRT
* 
* Creation date: 12/02/2009 11AM
* Copyright (c) 2009 MEDecision, Inc.  All rights reserved.
* 
*/
public interface IStandardFieldMapping {
    
	/**
	 * Get the primary standard field.
	 * @return IStandardField
	 */
	public IStandardField getPrimaryStandardField();
    
	/**
	 * Set the primary standard field.
	 * @param IStandardField
	 */
	public void setPrimaryStandardField(IStandardField primaryStandardField);
    
	/**
	 * Get the secondary standard field.
	 * @return IStandardField
	 */
	public IStandardField getSecondaryStandardField();
    
	/**
	 * Set the secondary standard field
	 * @param IStandardField
	 */
	public void setSecondaryStandardField(IStandardField secondaryStandardField);
    
	/**
	 * Get the active flag.
	 * @return boolean
	 */
	public boolean isActive();
    
	/**
	 * Set the active flag
	 * @param boolean
	 */
	public void setActive(boolean active);

}