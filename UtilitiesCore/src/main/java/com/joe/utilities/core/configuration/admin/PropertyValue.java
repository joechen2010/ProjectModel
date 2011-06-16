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
package com.joe.utilities.core.configuration.admin;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author rrichard
 *
 */
public class PropertyValue implements IPropertyValue {
    String key = null;
    String value = null;
    String defaultValue = null;
    String systemValue = null;
    

    /**
     * @param key
     * @param value
     */
    public PropertyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public boolean isDefault() {
        if (this.value == null) {
            return true;
        }
        
        return value.equals(getDefaultValue());
    }
    
    /* (non-Javadoc)
     * @see com.med.utilities.core.config.IPropertyValue#getSystemValue()
     */
    public String getSystemValue() {
        return this.systemValue;
    }

    /* (non-Javadoc)
     * @see com.med.utilities.core.config.IPropertyValue#getDefaultValue()
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /* (non-Javadoc)
     * @see com.med.utilities.core.config.IPropertyValue#getPropertyValue()
     */
    public String getPropertyValue() {
        String returnValue = getValue();
        if (returnValue == null) {
            returnValue = getDefaultValue();
        }
        return returnValue;
    }
    /* (non-Javadoc)
     * @see com.med.utilities.core.config.IPropertyValue#getKey()
     */
    public String getKey() {
        return key;
    }

    /* (non-Javadoc)
     * @see com.med.utilities.core.config.IPropertyValue#getValue()
     */
    public String getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see com.med.utilities.core.config.IPropertyValue#setKey(java.lang.String)
     */
    public void setKey(String key) {
        this.key = key;
    }

    /* (non-Javadoc)
     * @see com.med.utilities.core.config.IPropertyValue#setValue(java.lang.String)
     */
    public void setValue(String value) {
        if (value != null && value.length() > 0) {
            this.value = value;
        }
    }
    

    /**
     * @param systemValue2
     */
    public void setSystemValue(String systemValue2) {
        this.systemValue = systemValue2;
    }

    /**
     * @param defaultValue2
     */
    public void setDefaultValue(String defaultValue2) {
        this.defaultValue = defaultValue2;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(key)
            .append(value)
            .toHashCode();
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PropertyValue other = (PropertyValue) obj;
        
        return new EqualsBuilder()
            .append(key, other.key)
            .append(value, other.value)
            .isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
        return new ToStringBuilder(this)
        .append("key", key)
        .append("value", getPropertyValue())
        .append("default", getDefaultValue())
        .append("system", getSystemValue())
        .toString();
    }
}
