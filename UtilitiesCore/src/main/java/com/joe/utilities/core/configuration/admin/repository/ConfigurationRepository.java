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
  * Created on Apr 9, 2007
 *
 */
package com.joe.utilities.core.configuration.admin.repository;

import com.joe.utilities.core.configuration.admin.IPropertyValue;
import com.joe.utilities.core.listhandler.SearchResult;

/**
 * @author rrichard
 *
 */
public interface ConfigurationRepository {

    public void saveProperty(IPropertyValue property);
    
    public IPropertyValue getProperty(String key);
    
    public SearchResult<IPropertyValue> getProperties(String filer, Long startIndex, Long maxResults);

    public IPropertyValue getSystemAssignedID(String key);

    /**
     * @param property
     */
    public void deleteOverride(IPropertyValue property);
}
