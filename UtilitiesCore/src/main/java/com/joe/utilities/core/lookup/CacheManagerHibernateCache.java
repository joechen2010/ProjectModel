package com.joe.utilities.core.lookup;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.Timestamper;

import com.opensymphony.oscache.base.CacheEntry;


/**
 * Hibernate Cache adapter for the CacheManager implementation.
 * 
 * This Adapter REQUIRES that the Hibernate cache.region_prefix property contain the MCO ID
 * corresponding to the data that is being cached.  If the data is not specific to an MCO,
 * it should use a shared ID (ex. IEXCHANGE).
 */
public class CacheManagerHibernateCache implements Cache {
    
	private static Log logger =  LogFactory.getLog(CacheManagerHibernateCache.class.getName());

	private final String regionName;
	private final String mcoId;
	
	/**
	 * Constructor for CacheManagerHibernateCache.  This constructor parses the region prefix
	 * (MCO ID) from the region name and sets the parsed values as properties.
	 * @param completeRegionName this is cache.region_prefix(MCO ID) + "." + regionName
	 */
	public CacheManagerHibernateCache(String completeRegionName) 
	{
		int index = completeRegionName.indexOf(".");
		this.mcoId = completeRegionName.substring(0, index);
		this.regionName = completeRegionName.substring(index+1);
		if (logger.isDebugEnabled()) {
			logger.debug("CacheManagerHibernateCache created for MCO: '" + mcoId + "', region: '" + regionName + "'");
		}
	}
	
	/**
	 * Converts a cached data key Object to String and adds the region name
	 * @param key
	 */
	private String toString(Object key) {
		return String.valueOf(key) + '.' + regionName;
	}
	
	/**
	 * Returns an object from cached data
	 * @param Object key
	 * @return Object
	 */
	public Object get(Object key) throws CacheException {
		return CacheManager.getFromCache(mcoId, toString(key));
	}

	/**
	 * Returns an object from cached data
	 * @param Object key
	 * @return Object
	 */
	public Object read(Object key) throws CacheException {
		return get(key);
	}
	
	/**
	 * Updates an object in data cache
	 * @param key
	 * @param value
	 */
	public void update(Object key, Object value) throws CacheException {
		put(key, value);
	}
	
	/**
	 * Inserts an object in data cache
	 * @param key
	 * @param value
	 */
	public void put(Object key, Object value) throws CacheException {
		CacheManager.putInCache(mcoId, toString(key), value, regionName);
	}

	/**
	 * Flushes a cached item by key
	 * @param key
	 */
	public void remove(Object key) throws CacheException {
		CacheManager.flushItem(mcoId, toString(key));
	}

	/**
	 * Flushes all items from the data cache associated with this region
	 */
	public void clear() throws CacheException {
		CacheManager.flushGroup(mcoId, regionName);
	}

	/**
	 * Flushes all items from the data cache associated with this region
	 */
	public void destroy() throws CacheException {
		clear();
	}

	/**
	 * This performs no functionality for this implementation because this is
	 * a local cache and synchronization is used.
	 * @param key
	 */
	public void lock(Object key) throws CacheException {
	}

	/**
	 * This performs no functionality for this implementation because this is
	 * a local cache and synchronization is used.
	 * @param key
	 */
	public void unlock(Object key) throws CacheException {
	}
	
	/**
	 * Returns a timestamp
	 * @return long
	 */
	public long nextTimestamp() {
		return Timestamper.next();
	}

	/**
	 * Returns an indefinite timeout as the exact timeout is determined by CacheManager.
	 * @return int
	 */
	public int getTimeout() {
		return CacheEntry.INDEFINITE_EXPIRY;
	}

	/**
	 * Returns the region name associated with this cache.
	 * @return String
	 */
	public String getRegionName() {
		return regionName;
	}

	/**
	 * This always returns -1 because the information is not available from CacheManager.
	 * @return long
	 */
	public long getSizeInMemory() {
		return -1;
	}

	/**
	 * This always returns -1 because the information is not available from CacheManager.
	 * @return long
	 */
	public long getElementCountInMemory() {
		return -1;
	}

	/**
	 * This always returns -1 because the information is not available from CacheManager.
	 * @return long
	 */
	public long getElementCountOnDisk() {
		return -1;
	}

	/**
	 * This is not supported by this implementation.
	 * @throws UnsupportedOperationException
	 */
	public Map toMap() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a String value that identifies this object.
	 * @return String
	 */
	public String toString() {
		return "CacheManagerHibernateCache(" + regionName + ')';
	}
}
