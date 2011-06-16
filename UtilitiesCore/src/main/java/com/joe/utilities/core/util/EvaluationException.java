package com.joe.utilities.core.util;


/**
* Checked exception signalling an error or warning condition present during 
* an evaluation.  The returnStatus attribute can then be interrogated for more details about the error.
* @author Dave Ousey
* 
* Creation date: 1/28/2007 9 AM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public class EvaluationException extends MedException
{
    /** Comment for <code>serialVersionUID</code> */
    private static final long serialVersionUID = -4415399722352136671L;

    /** Comment for <code>returnStatus</code> */
    private ReturnStatus returnStatus;
    
    /**
     * @param returnStatus
     */
    public EvaluationException(ReturnStatus returnStatus)
    {
        this.returnStatus = returnStatus;
    }

    /**
     * Method getReturnStatus. 
     * @return ReturnStatus
     */
    public ReturnStatus getReturnStatus()
    {
        return returnStatus;
    }
    
}
