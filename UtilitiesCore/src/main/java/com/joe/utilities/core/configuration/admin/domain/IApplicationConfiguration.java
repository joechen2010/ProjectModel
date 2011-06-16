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
package com.joe.utilities.core.configuration.admin.domain;

/**
 * This interface represents an application configuration item.
 * For example, ....
 * 
 * @author Dave Bartlett 
 * Creation date: 05/22/2007 2:32 PM 
 * Copyright (c) 2007 MEDecision, Inc. All rights reserved.
 */
public interface IApplicationConfiguration {

	/**
	 * Get the code name for this configuration parameter
	 * @return name
	 */
	public String getCode();

	/**
	 * Sets the code name for this configuration parameter
	 * @param name
	 */
	public void setCode(String code);
	
	/**
	 * Gets the category code for this configuration parameter
	 * @return name
	 */
	public String getCategoryCode();

	/**
	 * Sets the category code for this configuration parameter
	 * @param category code
	 */
	public void setCategoryCode(String categoryCode);
	
	/**
	 * Sets the value for this configuration parameter
	 * @return value
	 */
	public String getValue();

	/**
	 * Sets the valuee for this configuration parameter
	 * @param value
	 */
	public void setValue(String value);
	
	/**
	 * Get the description for this configuration parameter
	 * @return text description
	 */
	public String getDescription();

	/**
	 * Sets the code description for this configuration parameter
	 * @param text description
	 */
	public void setDescription(String text);
	
	/**
	 * 
	 * Gets the Editable flag for this configuration parameter
	 * @return flag
	 */
	public boolean getEditableFlag();

	/**
	 * Sets the flag to determine if this configuration parameter
	 * is allowed to be edited.
	 * 
	 * @param flag
	 */
	public void setEditableFlag(boolean flag);
}
