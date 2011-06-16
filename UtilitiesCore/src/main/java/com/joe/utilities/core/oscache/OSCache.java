package com.joe.utilities.core.oscache;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.Timestamper;
import org.hibernate.util.PropertiesHelper;

import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * Adapter for the OSCache implementation. This class replaces a class included in
 * the Hibernate distribution with the same name and is used by the corresponding
 * OSCacheProvider class.  The source code from this class was obtained
 * from the OSCache site (http://www.opensymphony.com/oscache/wiki/Hibernate.html).
 * The difference between this class and the class included in the
 * Hibernate distribution is that this class will create one static instance
 * of the OSCache GeneralCacheAdministrator while the Hibernate distribution
 * will create one instance per cache region which may lead to performance issues.
 */
public class OSCache implements Cache {
    
	private static Log log =  LogFactory.getLog(OSCache.class.getName());
	
    /** 
     * The <tt>OSCache</tt> cache capacity property suffix. 
     */
    public static final String OSCACHE_CAPACITY = "cache.capacity";

    private static final Properties OSCACHE_PROPERTIES = new Config().getProperties();
	/** 
	 * The OSCache 2.0 cache administrator. 
	 */
	private static GeneralCacheAdministrator cache = new GeneralCacheAdministrator();

    private static Integer capacity = PropertiesHelper.getInteger(OSCACHE_CAPACITY,
                                                                  OSCACHE_PROPERTIES);

    static {
        if (capacity != null) cache.setCacheCapacity(capacity.intValue());
    }
    
	private final int refreshPeriod;
	private final String cron;
	private final String regionName;
    private final String[] regionGroups;
	
	private String toString(Object key) {
		return String.valueOf(key) + "." + regionName;
	}

	public OSCache(int refreshPeriod, String cron, String region) {
		this.refreshPeriod = refreshPeriod;
		this.cron = cron;
		this.regionName = region;
        this.regionGroups = new String[] {region};
	}

	public Object get(Object key) throws CacheException {
		try {
			return cache.getFromCache( toString(key), refreshPeriod, cron );
		}
		catch (NeedsRefreshException e) {
			cache.cancelUpdate( toString(key) );
			return null;
		}
	}

	public void put(Object key, Object value) throws CacheException {
		
		// Add to cache with list of groups associated with entry
		try
		{
			cache.putInCache( toString(key), value, regionGroups );
		}
		catch (Throwable t)
		{
			log.warn("Could not update cache for key = '"+key+"'.", t);			
			cache.cancelUpdate(toString(key));
		}
	}

	public void remove(Object key) throws CacheException {
		cache.flushEntry( toString(key) );
	}

	public void clear() throws CacheException {
		cache.flushGroup(regionName);
	}

	public void destroy() throws CacheException {
		synchronized (cache) {
		    cache.destroy();
        }
	}

	public void lock(Object key) throws CacheException {
		// local cache, so we use synchronization
	}

	public void unlock(Object key) throws CacheException {
		// local cache, so we use synchronization
	}

	public long nextTimestamp() {
		return Timestamper.next();
	}

	public int getTimeout() {
		return Timestamper.ONE_MS * 60000; //ie. 60 seconds
	}

	public Map toMap() {
		throw new UnsupportedOperationException();
	}    

	public long getElementCountOnDisk() {
		return -1;
	}

	public long getElementCountInMemory() {
		return -1;
	}
    
	public long getSizeInMemory() {
		return -1;
	}

	public String getRegionName() {
		return regionName;
	}

	public void update(Object key, Object value) throws CacheException {
		put(key, value);
	}    

	public Object read(Object key) throws CacheException {
		return get(key);
	}
}
