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
  * Created on Apr 17, 2007
 *
 */
package com.joe.utilities.core.validation;

import java.util.List;
import java.util.Vector;

import com.joe.utilities.core.configuration.CompositeGlobalConfigurationException;
import com.joe.utilities.core.configuration.GlobalConfigurationException;
import com.joe.utilities.core.util.ReturnStatus;


/**
 * Implements a Composit Strategy to determine validation.
 * 
 * @author rrichard
 *
 */
public class CompositValidator implements Validator {

    /**
     *  List of validators utilized to determine validation.
     */
    List<Validator> validators;
    
    /**
     * Implements a Composit Strategy to determine validation.
     */
    public CompositValidator() {
        this.validators = new Vector<Validator>(5);
    }
    
    
    /**
     * Adds a validator to the composite
     * @param validator
     */
    public void add(Validator validator) {
        if (validator != null) {
            this.validators.add(validator);
        }
    }
    
    
    /**
     * Cycles thru the list of Validators and returns <code>true<code> is each
     * validator returns true.
     * @return
     */
    public boolean isValid() {
        boolean valid = true;
        
        for (Validator currItem: this.validators) {
            ReturnStatus currStatus = currItem.validate();
            if (currStatus != null && !currStatus.isSuccess()) {
                valid = false;
                break;
            }
        }
        return valid;
        
    }
    
    /**
     * Cycles thru the list of Validators to determine the status.
     * @see com.med.configuration.validation.Validator#validate()
     */
    public ReturnStatus validate() {
        ReturnStatus status = new ReturnStatus();
        CompositeGlobalConfigurationException errorList = null;
        
        for (Validator currItem: this.validators) {
        	try { 
	            ReturnStatus currStatus = currItem.validate();
	            if (currStatus != null) {
	                status.appendStatusItems(currStatus);
	            }
           	} catch ( GlobalConfigurationException error ){
        		if ( errorList == null ){
        			errorList = new  CompositeGlobalConfigurationException(error);
        		} else {
        			errorList.addException(error);
        		}
        	}
	           
        }
        
        if ( errorList != null ){
        	throw errorList;
        }
        return status;
    }

}
