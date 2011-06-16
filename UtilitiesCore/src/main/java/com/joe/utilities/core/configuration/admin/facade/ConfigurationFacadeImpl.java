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
  * Created on Apr 6, 2007
 *
 */
package com.joe.utilities.core.configuration.admin.facade;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joe.utilities.core.configuration.admin.ConfigurationListHandler;
import com.joe.utilities.core.configuration.admin.IPropertyValue;
import com.joe.utilities.core.configuration.admin.repository.ConfigurationRepository;
import com.joe.utilities.core.listhandler.SearchResult;
import com.joe.utilities.core.listhandler.ValueListIterator;
import com.joe.utilities.core.util.EvaluationException;
import com.joe.utilities.core.util.ReturnStatus;

/**
 * @author rrichard
 *
 */
public class ConfigurationFacadeImpl implements ConfigurationFacade {

    private ConfigurationRepository configRepository = null;
    
    public ConfigurationFacadeImpl(ConfigurationRepository configRepository) {
        this.configRepository = configRepository;
    }
    
    /* (non-Javadoc)
     * @see com.med.utilities.facade.ConfigurationFacade#getProperties(java.lang.String)
     */
    public ConfigurationFacadeResult getProperties(String filter, Long startIndex, Long maxResults) {
        ConfigurationFacadeResult result = new ConfigurationFacadeResult();
        ReturnStatus status = new ReturnStatus();
        SearchResult<IPropertyValue> srchResult = configRepository.getProperties(filter, startIndex, maxResults);
        ValueListIterator<IPropertyValue> listHandler = new ConfigurationListHandler(srchResult);
        
        result.setPropertyList(listHandler);
        result.setStatus(status);
        return result;
    }

    /* (non-Javadoc)
     * @see com.med.utilities.facade.ConfigurationFacade#getProperty(java.lang.String)
     */
    public ConfigurationFacadeResult getProperty(String key) {
        ConfigurationFacadeResult result = new ConfigurationFacadeResult();
        ReturnStatus status = new ReturnStatus();
        
        IPropertyValue property = configRepository.getProperty(key);
        result.setProperty(property);
        result.setStatus(status);
        return result;
    }

    /* (non-Javadoc)
     * @see com.med.utilities.facade.ConfigurationFacade#saveProperties(com.med.utilities.core.config.IPropertyValue[])
     */
    public ConfigurationFacadeResult saveProperties(IPropertyValue[] properties) {
        ConfigurationFacadeResult result = new ConfigurationFacadeResult();
        ReturnStatus status = new ReturnStatus();
        if (properties != null) {
           for (IPropertyValue property: properties) {
                if (!property.isDefault()) {
                    configRepository.saveProperty(property);
                } else if (property.getValue() != null) {
                    configRepository.deleteOverride(property);
                }
            }
        }
        result.setStatus(status);
        return null;
    }

    /* (non-Javadoc)
     * @see com.med.utilities.facade.ConfigurationFacade#getSystemAssignedID(java.lang.String)
     */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = { EvaluationException.class })
    public ConfigurationFacadeResult getSystemAssignedID(String key) {
        ConfigurationFacadeResult result = new ConfigurationFacadeResult();
        ReturnStatus status = new ReturnStatus();
        
        IPropertyValue property = configRepository.getSystemAssignedID(key);
        result.setProperty(property);
        result.setStatus(status);
        return result;
    }

}
