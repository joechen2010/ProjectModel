package com.joe.utilities.core.facade;



/**
* This facade interface to flush cache items in current app instance and other app instances.
* 
* Creation date: 07/07/2008 11 AM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public interface CacheFlushFacade 
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
