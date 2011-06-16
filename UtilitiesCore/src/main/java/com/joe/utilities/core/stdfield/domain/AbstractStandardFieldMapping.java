package com.joe.utilities.core.stdfield.domain;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;



/**
 * Domain object represents Standard Field mapping domain object.
 * @author GRT
 * 
 * Creation date: 12/02/2009
 * Copyright (c) 2009 MEDecision, Inc.  All rights reserved.
 * 
 */

public abstract class AbstractStandardFieldMapping implements IStandardFieldMapping{
	private IStandardField primaryStandardField;
	
	private IStandardField secondaryStandardField;
	
	private boolean active;
	
	@Autowired
	private ApplicationContext applicationContext;

	
	public AbstractStandardFieldMapping(){
	}
	
	/**
	 * @see com.med.stdfield.domain.mapping.IStandardFieldMapping#getPrimaryStandardField()
	 */
	public IStandardField getPrimaryStandardField() {
		return primaryStandardField;
	}
	/** 
	 * @see com.med.stdfield.domain.mapping.IStandardFieldMapping#setPrimaryStandardField(com.joe.utilities.core.stdfield.domain.IStandardField)
	 */
	public void setPrimaryStandardField(IStandardField primaryStandardField) {
		this.primaryStandardField = primaryStandardField;
	}
	/** 
	 * @see com.med.stdfield.domain.mapping.IStandardFieldMapping#getSecondaryStandardField()
	 */
	public IStandardField getSecondaryStandardField() {
		return secondaryStandardField;
	}
	/** 
	 * @see com.med.stdfield.domain.mapping.IStandardFieldMapping#setSecondaryStandardField(com.joe.utilities.core.stdfield.domain.IStandardField)
	 */
	public void setSecondaryStandardField(IStandardField secondaryStandardField) {
		this.secondaryStandardField = secondaryStandardField;
	}
	/** 
	 * @see com.med.stdfield.domain.mapping.IStandardFieldMapping#getActive()
	 */
	public boolean isActive() {
		return active;
	}
	/** 
	 * @see com.med.stdfield.domain.mapping.IStandardFieldMapping#setActive(java.lang.boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public Serializable getAuditableID() {
		return primaryStandardField.getCode();
	}
	

}
