package com.joe.utilities.core.stdfield.domain;

import java.util.List;

import com.joe.utilities.core.util.ILookup;
import com.joe.utilities.core.util.IStandardFieldLookupProfile;

/**
 * Business interface for StandardField  domain object.
 * @author james lu
 * 
 * Creation date: 14/10/2009
 * Copyright (c) 2009 MEDecision, Inc.  All rights reserved.
 * 
 */
public interface IStandardField extends IStandardFieldLookupProfile ,ILookup{
    public static final String ERROR_CODE_IS_NULL = "ERROR_CODE_IS_NULL";
    public static final String ERROR_DESCRIIPTION_IS_NULL = "ERROR_DESCRIIPTION_IS_NULL";
    public static final String ERROR_CODE_CONTAINS_INVALID_CHARACTERS = "ERROR_CODE_CONTAINS_INVALID_CHARACTERS";
    public static final String ERROR_DESCRIIPTION_CONTAINS_INVALID_CHARACTERS = "ERROR_DESCRIIPTION_CONTAINS_INVALID_CHARACTERS";
    public static final int NUMBER_FOR_LIMIT = 50;
    public static final int NUMBER_FOR_LIMIT_SHORT = 45;
    /**
     * retrieve the audit history
     * @return
     */
    /**
     * set the number for limit 
     * @param numForLimit
     */
    public void setNumOfLimit(int numForLimit);
}
