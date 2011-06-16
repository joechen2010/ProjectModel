package com.joe.utilities.core.facade;

import java.util.List;

/**
* Facade interface methods to create offline and online perssimistic locks
* @author Dave Ousey
* 
* Creation date: 10/17/2007 12 AM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public interface LockFacade
{
    

    /**
     * Method deleteSessionLocks. Remove all logical lock record from the logical lock table for a given session ID.
     * @param sessionID 
     * @return void
     */
    public void deleteSessionLocks(String sessionID);

    /**
     * Method deleteSessionLocksForTable. Remove all logical lock record from the logical lock table for a given session ID and table ID.
     * @param tableName
     * @param sessionID
     * @return void
     */
    public void deleteSessionLocksForTable(String tableName, String sessionID);

        
    /**
     * Method createPhysicalLock. Creates physical lock with  name
     * @param lockName
     * @return void
     */
    public void createPhysicalLock(String lockName);
    
    /**
     * Deletes a physical lock record from the database.
     * @param lockName
     */
    public void deletePhysicalLock(String lockName);
    
}
