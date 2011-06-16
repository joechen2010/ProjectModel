package com.joe.utilities.core.lookup;

import java.util.Properties;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.Timestamper;

/**
 * Support for CacheManager. This implementation assumes
 * that identifiers have well-behaved <tt>toString()</tt> methods.
 */
public class CacheManagerHibernateCacheProvider implements CacheProvider {


	/**
	 * Builds a new {@link Cache} instance.
	 *
	 * @param region
	 * @param properties
	 * @return
	 * @throws CacheException
	 */
	public Cache buildCache(String region, Properties properties) throws CacheException {
		// construct the cache        
        return new CacheManagerHibernateCache(region);
	}

	/**
	 * Returns a timestamp.
	 * @return long
	 */
	public long nextTimestamp() {
		return Timestamper.next();
	}

	/**
	 * Callback to perform any necessary initialization of the underlying cache implementation
	 * during SessionFactory construction.
	 *
	 * @param properties current configuration settings.
	 */
	public void start(Properties properties) throws CacheException {
	}

	/**
	 * Callback to perform any necessary cleanup of the underlying cache implementation
	 * during SessionFactory.close().
	 */
	public void stop() {
	}

	/**
	 * Returns default setting for minimalPutsEnabled.  This can be adjusted in the Hibernate config.
	 * @return boolean
	 */
	public boolean isMinimalPutsEnabledByDefault() {
		return false;
	}
}
