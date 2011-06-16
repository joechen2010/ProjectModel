package com.joe.utilities.core.facade;

import com.joe.utilities.core.hibernate.repository.CacheFlushRepository;


/**
 * The Class CacheFlushFacadeImpl.  This class implements facade methods to invoke repository layer code to flush
 * targets cache entries
 */
public class CacheFlushFacadeImpl implements CacheFlushFacade
{
	
	private CacheFlushRepository cacheFlushRepository;
	
	
	
	
	/**
	 * Constructor.
	 */
	public CacheFlushFacadeImpl(CacheFlushRepository cacheFlushRepository)
	{
		super();
		this.cacheFlushRepository = cacheFlushRepository;
	}

	/**
	 * Broadcast cache flush.
	 * 
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastCacheFlush()
	 */
	public void broadcastCacheFlush()
	{
		cacheFlushRepository.broadcastCacheFlush();
	}

	/**
	 * Broadcast cache flush group.
	 * 
	 * @param groupName the group name
	 * 
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastCacheFlushGroup(java.lang.String)
	 */
	public void broadcastCacheFlushGroup(String groupName)
	{
		cacheFlushRepository.broadcastCacheFlushGroup(groupName);
	}

	/**
	 * Broadcast cache flush item.
	 * 
	 * @param cacheKey the cache key
	 * 
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastCacheFlushItem(java.lang.String)
	 */
	public void broadcastCacheFlushItem(String cacheKey)
	{
		cacheFlushRepository.broadcastCacheFlushItem(cacheKey);
	}

	/**
	 * Broadcast cache flush group.
	 * 
	 * @param mcoID the mco id
	 * @param groupName the group name
	 * 
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastCacheFlushGroup(java.lang.String, java.lang.String)
	 */
	public void broadcastCacheFlushGroup(String mcoID, String groupName)
	{
		cacheFlushRepository.broadcastCacheFlushGroup(mcoID, groupName);
	}

	/**
	 * Broadcast cache flush item.
	 * 
	 * @param mcoID the mco id
	 * @param cacheKey the cache key
	 * 
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastCacheFlushItem(java.lang.String, java.lang.String)
	 */
	public void broadcastCacheFlushItem(String mcoID, String cacheKey)
	{
		cacheFlushRepository.broadcastCacheFlushItem(mcoID, cacheKey);
	}

	/**
	 * Broadcast mco flush.
	 * 
	 * @param mcoID the mco id
	 * 
	 * @see com.med.utilities.repository.CacheFlushRepository#broadcastMCOFlush(java.lang.String)
	 */
	public void broadcastMCOFlush(String mcoID)
	{
		cacheFlushRepository.broadcastMCOFlush(mcoID);
	}
}