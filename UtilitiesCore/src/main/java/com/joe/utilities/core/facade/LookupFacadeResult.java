package com.joe.utilities.core.facade;

import java.util.List;

import com.joe.utilities.core.util.ILookupProfile;
import com.joe.utilities.core.util.ReturnStatus;

/**
* Facade result container for LookupFacade
* @author Dave Ousey
* 
* Creation date: 1/5/2007 9 AM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public class LookupFacadeResult
{
    /** Generic return status object */
    private ReturnStatus status;
    
    /** Returned List if lookup domain objects */
    private List<ILookupProfile> lookupList;

    /**
     * Method getLookupList. 
     * @return List<ILookupProfile>
     */
    public List<ILookupProfile> getLookupList()
    {
        return lookupList;
    }

    /**
     * Method setLookupList. 
     * @param lookupList
     * @return void
     */
    public void setLookupList(List<ILookupProfile> lookupList)
    {
        this.lookupList = lookupList;
    }

    /**
     * Method getStatus. 
     * @return ReturnStatus
     */
    public ReturnStatus getStatus()
    {
        return status;
    }

    /**
     * Method setStatus. 
     * @param status
     * @return void
     */
    public void setStatus(ReturnStatus status)
    {
        this.status = status;
    }
    
}
