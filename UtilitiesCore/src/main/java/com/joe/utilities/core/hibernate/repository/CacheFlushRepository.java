package com.joe.utilities.core.hibernate.repository;



/**
* This repository interface provide access to an underlying autocoder jms implementation
* to perform autocoder search
* @author Chris Wang
* 
* Creation date: 4/19/2007 9 AM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public interface CacheFlushRepository 
{
	/**
	 * broadcastCacheFlush.  Flush all caches
	 * void
	 */
	public void broadcastCacheFlush();
	
	/**
	 * broadcastCacheFlushGroup.  FLush specific group
	 * @param groupName
	 * void
	 */
	public void broadcastCacheFlushGroup(String groupName);
	
	/**
	 * broadcastCacheFlushItem. Flush specific key
	 * @param key
	 * void
	 */
	public void broadcastCacheFlushItem(String key);
	
	/**
	 * broadcastCacheFlush.  Flush all caches for specific MCO
	 * void
	 */
	public void broadcastMCOFlush(String mcoID);

	/**
	 * broadcastCacheFlushGroup
	 * @param mcoID
	 * @param groupName
	 * void
	 */
	public void broadcastCacheFlushGroup(String mcoID, String groupName);
	
	/**
	 * broadcastCacheFlushItem
	 * @param mcoID
	 * @param key
	 * void
	 */
	public void broadcastCacheFlushItem(String mcoID, String key);
	
}
