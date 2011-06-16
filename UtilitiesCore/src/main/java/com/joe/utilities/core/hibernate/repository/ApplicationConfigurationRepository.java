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
package com.joe.utilities.core.hibernate.repository;
import java.util.List;

import com.joe.utilities.core.configuration.admin.domain.IApplicationConfiguration;

/**
 * This repository interface provide access to database
 * functions needed to set and get application configuration
 * parameters kept inside the database.  This does NOT 
 * include properties from globals.properties, etc.
 * 
 * @author Dave Bartlett 
 * Creation date: 05/22/2007 2:32 PM 
 * Copyright (c) 2007 MEDecision, Inc. All rights reserved.
 */
public interface ApplicationConfigurationRepository {

	public IApplicationConfiguration retreiveApplicationConfigProperty(String code);

	public List<IApplicationConfiguration> listApplicationConfigurationsByCategoryCode (String categoryCode);

	public List<IApplicationConfiguration> listApplicationConfigurations();
	

	public IApplicationConfiguration saveApplicationConfiguration (IApplicationConfiguration appConfig);

	public void removeApplicationConfigProperty(IApplicationConfiguration appConfig);
	
	public Long retrieveDBTimeOffsetInMilliseconds();
	
	public Long retrieveDBTimeInMilliseconds();

}