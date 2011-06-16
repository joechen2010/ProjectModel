package com.joe.utilities.core.stdfield.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;

import com.joe.utilities.core.serviceLocator.ServiceLocator;
import com.joe.utilities.core.util.ReturnStatus;

/**
 * Domain object for StandardField  domain object.
 * @author GRT
 * 
 * Creation date: 14/10/2009
 * Copyright (c) 2009 MEDecision, Inc.  All rights reserved.
 * 
 */

public abstract class AbstractStandardField implements IStandardField {
	
	
    
	private int numForLimit = 0;

	
	@Autowired
	private ApplicationContext applicationContext;
	
	private String code;
	
	private String description;
	
	private boolean active;
	

	public AbstractStandardField() {
		numForLimit = NUMBER_FOR_LIMIT;
	}
	
	/**
	 * @see com.med.utilities.domain.IStandardField#isActive()
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @see com.med.utilities.core.ILookup#setActive(boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	/**
	 * @see com.med.utilities.core.ILookup#setCode(String)
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @see com.med.utilities.core.ILookupProfile#getCode()
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @see com.med.utilities.core.ILookup#setDescription(String)
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @see com.med.utilities.core.ILookupProfile#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see com.med.utilities.core.ILookupProfile#getDisplayDescription()
	 */
	public String getDisplayDescription() {
		String text = this.getDescription();
		if(text == null) return text;		
		StringBuffer buffer = new StringBuffer(numForLimit);
		int length = text.length();
		if (length > 0 && length > numForLimit)
		{
			buffer.append(text.substring(0, numForLimit-3));
			buffer.append("...");        
		}else {
			buffer.append(text);
		}        	
		return buffer.toString();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getCode()).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractStandardField other = (AbstractStandardField) obj;

		return new EqualsBuilder().append(this.getCode(), other.getCode())
				.isEquals();
	}
	/**
	 * @see com.med.standardfield.domain.IStandardField#validate()
	 */
	public ReturnStatus validate() {
		ReturnStatus status = new ReturnStatus();
		if (getCode() == null || getCode().trim().length() == 0) {
			status.addError(IStandardField.ERROR_CODE_IS_NULL,
					"code is a required field");
		} else {
			String invalidCharacters = this.getInvalidCharacters(getCode());
			if (invalidCharacters != null) {
				Map<String, String> parameterMap = new HashMap<String, String>();
				parameterMap.put("invalidCharacters", invalidCharacters);
				status.addError(IStandardField.ERROR_CODE_CONTAINS_INVALID_CHARACTERS,
						"Code contains invalid characters.", parameterMap);
			}
		}
		if (getDescription() == null || getDescription().trim().length() == 0) {
			status.addError(IStandardField.ERROR_DESCRIIPTION_IS_NULL,
					"description is a required field");
		} else {
			String invalidCharacters = this.getInvalidCharacters(getDescription());
			if (invalidCharacters != null) {
				Map<String, String> parameterMap = new HashMap<String, String>();
				parameterMap.put("invalidCharacters", invalidCharacters);
				status.addError(IStandardField.ERROR_DESCRIIPTION_CONTAINS_INVALID_CHARACTERS,
						"Description contains invalid characters.", parameterMap);
			}
		}

		return status;
	}

	/**
	 * @see com.med.utilities.core.IStandardFieldLookupProfile#isNew()
	 */
	public boolean isNew() {
		return false;
	}
	
	/**
	 * @see com.med.utilities.core.IStandardFieldLookupProfile#setNew(boolean)
	 */
	public void setNew(boolean newFlag) {
		
	}
	
	public Serializable getAuditableID() {
		return code;
	}
	
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
		return new ToStringBuilder(this).append("code", code).toString();
	}
    
    private String getInvalidCharacters(String str) {
    	if (str == null) {
    		return null;
    	}
    	String regEx = "^.*?[" +
				"\\u0001-\\u001F|" +	//ASCII 1-31
				"\\u007F-\\uFFFF|" +	//ASCII 127-255 and Unicode 256 - 24575
				"\\u007E|" +			//ASCII "~"
				"\\u005E|" +			//ASCII "^"
				"\\u007B|" +			//ASCII "{"
				"\\u007D|" +			//ASCII "}"
				"\\u005B|" +			//ASCII "["
				"\\u005D|" +			//ASCII "]"
				"\\u007C|" +			//ASCII "|"
				"\\u0060|" +			//ASCII "`"
				"\\u005C|" +			//ASCII "\"
				"].*?$";
		Pattern p = Pattern.compile(regEx);
		boolean flag = p.matcher(str).find();
		if (flag) {
			String result = "";
			for (int i=0; i<str.length(); i++) {
				if (p.matcher(String.valueOf(str.charAt(i))).find() && !result.contains(String.valueOf(str.charAt(i)))) {
					result += "\"" + str.charAt(i) + "\"" + ", ";
				}
			}
			result = result.substring(0, result.length() - 2);
			return result;
		}
    	return null;
    }
    
	public void setNumOfLimit(int numForLimit) {
		this.numForLimit = numForLimit;
	}

	 
}
