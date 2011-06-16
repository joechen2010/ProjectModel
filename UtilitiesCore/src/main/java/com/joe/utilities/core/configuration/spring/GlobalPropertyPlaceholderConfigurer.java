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
  * Created on Mar 29, 2007
 *
 */
package com.joe.utilities.core.configuration.spring;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.joe.utilities.core.configuration.GlobalConfigurationException;
import com.joe.utilities.core.configuration.Globals;


/**
 * Property placeholder utilized to allow Spring placeholder variables to be substituted via settings from
 * globals. property
 * 
 * @see  com.med.configuration.Globals
 * @see  org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
 * 
 * @author rrichard
 *
 */
public class GlobalPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    /** This method uses globals.properties  to resolve place holders.
     * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#resolvePlaceholder(java.lang.String, java.util.Properties)
     */
    @Override
    protected String resolvePlaceholder(String arg0, Properties arg1) {
        String returnValue = null;
        try {
            returnValue =  Globals.getInstance().getString(arg0);
        } catch (GlobalConfigurationException e) {
        }
        return returnValue;
    }

}
