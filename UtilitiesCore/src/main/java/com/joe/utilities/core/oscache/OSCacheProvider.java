package com.joe.utilities.core.oscache;

import java.util.Properties;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.Timestamper;
import org.hibernate.util.PropertiesHelper;
import org.hibernate.util.StringHelper;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.Config;

/**
 * Support for OpenSymphony OSCache. This implementation assumes
 * that identifiers have well-behaved <tt>toString()</tt> methods.
 * This class replaces a class included in the Hibernate distribution
 * with the same name.  The source code from this class was obtained
 * from the OSCache site (http://www.opensymphony.com/oscache/wiki/Hibernate.html).
 * The difference between this provider and the provider included in the
 * Hibernate distribution is that this provider will create one instance
 * of the OSCache GeneralCacheAdministrator while the Hibernate distribution
 * will create one instance per cache region which may lead to performance issues.
 */
public class OSCacheProvider implements CacheProvider {

	/** 
	 * The <tt>OSCache</tt> refresh period property suffix. 
	 */
	public static final String OSCACHE_REFRESH_PERIOD = "refresh.period";
	/** 
	 * The <tt>OSCache</tt> CRON expression property suffix. 
	 */
	public static final String OSCACHE_CRON = "cron";
	
	private static final Properties OSCACHE_PROPERTIES = new Config().getProperties();

	/**
	 * Builds a new {@link Cache} instance, and gets it's properties from the OSCache {@link Config}
	 * which reads the properties file (<code>oscache.properties</code>) from the classpath.
	 * If the file cannot be found or loaded, an the defaults are used.
	 *
	 * @param region
	 * @param properties
	 * @return
	 * @throws CacheException
	 */
	public Cache buildCache(String region, Properties properties) throws CacheException {

		int refreshPeriod = PropertiesHelper.getInt(
			StringHelper.qualify(region, OSCACHE_REFRESH_PERIOD), 
			OSCACHE_PROPERTIES, 
			CacheEntry.INDEFINITE_EXPIRY
		);
		String cron = OSCACHE_PROPERTIES.getProperty( StringHelper.qualify(region, OSCACHE_CRON) );

		// construct the cache        
        return new OSCache(refreshPeriod, cron, region);
	}

	public long nextTimestamp() {
		return Timestamper.next();
	}

	public boolean isMinimalPutsEnabledByDefault() {
		return false;
	}

	/**
	 * Callback to perform any necessary cleanup of the underlying cache implementation
	 * during SessionFactory.close().
	 */
	public void stop() {
	}

	/**
	 * Callback to perform any necessary initialization of the underlying cache implementation
	 * during SessionFactory construction.
	 *
	 * @param properties current configuration settings.
	 */
	public void start(Properties properties) throws CacheException {
	}    
}
