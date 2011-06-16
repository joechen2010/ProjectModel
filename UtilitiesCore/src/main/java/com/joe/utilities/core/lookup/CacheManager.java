package com.joe.utilities.core.lookup;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joe.utilities.core.configuration.Globals;
import com.joe.utilities.core.util.Utils;
import com.opensymphony.oscache.base.events.CacheEntryEventListener;
import com.opensymphony.oscache.extra.CacheEntryEventListenerImpl;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
* CacheManager. Manages cached data using OS (open symphony) cache implementation
* 
* @author Dave Ousey
* @version      1.0
* 
* Creation date: 07/13/2005 2:177 PM
* Copyright (c) 2005 MEDecision, Inc.  All rights reserved.
*/
public class CacheManager
{
	private static Log log =  LogFactory.getLog(CacheManager.class.getName());

	/** OSCache adminstrator */
	private GeneralCacheAdministrator cacheAdministrator = null;

	/** Records JVM times per MCO when caches should be flushed */
	private Map<String, Date> flushTimesByMCO = new HashMap<String, Date> (4);
	
	private static CacheEntryEventListenerImpl cacheEntryEventListener = null;

	/** Reference to flush thread */
	private FlushThread flusher = null;
	
	private static CacheManager singleton;
	static
	{
		initialize();
	}

	private CacheManager()
	{
		
	}
	
	/**
	 * Gets the single instance of CacheManager.
	 * 
	 * @return single instance of CacheManager
	 */
	public static CacheManager getInstance()
	{
		return singleton;
	}
	
	/**
	 * Method initialize.  Creates new OS cache administrator objects for each MCO defined in the service configuration factory.
	 */
	public static void initialize()  
	{
		singleton = new CacheManager();
		
		// Lookup configuration properties
		Properties cacheProperties = null;
		String osCachePropertiesFileName = System.getProperty("oscache.configuration");
		if (osCachePropertiesFileName != null && osCachePropertiesFileName.length() > 0)
		{
			log.info("Reading OSCache property settings from " + osCachePropertiesFileName+"...");
			InputStream fileStream = null;
			try
			{
				cacheProperties = new Properties();
				fileStream = new BufferedInputStream(new FileInputStream(osCachePropertiesFileName));
				cacheProperties.load(fileStream);
			}
			catch (IOException e)
			{
				cacheProperties = null;
				log.error("Error loading OSCache properties file from " +osCachePropertiesFileName +'.');
			}
			finally
			{
				if (fileStream != null) try { fileStream.close(); } catch (IOException e) {} 
			}
		}
		
		// Use the following default setting if configuration file is unavailable
		if (cacheProperties == null)
		{
			log.info("Using default OSCache property settings...");	
			cacheProperties = new Properties();	
			cacheProperties.put("cache.capacity", "50000");
		}
		
		// Initialize the cache administrator with the specified properties
		singleton.cacheAdministrator = new GeneralCacheAdministrator(cacheProperties);
		
		initializeMCOFlushTimes();
	}
	
	/**
	 * Method initializeMCOFlusher.
	 */
	public static void initializeMCOFlushTimes()  
	{
		// Build a list of all MCO IDs configured for the application
		Set<String> mcos = getMcoSet();
		
		String [] mcoList = Globals.getStringArray("com.med.config.mco.list");
		if (mcoList == null || mcoList.length == 0)
			mcos.add("MDL");
		else
			mcos.addAll(Arrays.asList(mcoList));


		// Lookup flush times per MCO.  Set initialized flag to true to avoid cache-related errors in OP cache lookup.
		Iterator<String> iter = mcos.iterator();
		while (iter.hasNext())
		{
			String mcoID = iter.next();
			try 
			{
				setNextFlushTime(mcoID);
			} catch (Throwable t) {
				log.warn("Unable to set next flush time for MCO: '" + mcoID + "'", t);
				//Ignore exceptions for MCOs that may not be available during initialization
				continue;
			}
		}

		// Start up flush timer
		singleton.flusher = new FlushThread();
		singleton.flusher.start();
	}	

	/**
	 * getMcoSet: Returns set of MCOs configured in the globals properties file.  If setting is not defined, 
	 * assume single, default MCO environment
	 * @return Set<String>
	 */
	private static Set<String> getMcoSet()
	{ 
		// Get Set of all MCOs defined in services and resource helpers configuration since
		// some MCOs may use Services but no Resource Helpers and some MCOs may use Resource
		// Helpers but no Services.
		Set<String> mcos = new HashSet<String>(16);
		
		String [] mcoList = Globals.getStringArray("com.med.config.mco.list");
		if (mcoList == null || mcoList.length == 0)
			mcos.add("MDL");
		else
			mcos.addAll(Arrays.asList(mcoList));
		return mcos;
	}
	
	/**
	 * Method setNextFlushTime. Determine s and records in a map the next time (JVM time) when the given mco's cache
	 * items should be flushed.
	 * @param mcoID
	 */
	private static void setNextFlushTime(String mcoID)  
	{
		// Get time value from operation parameter (e.g. "2 AM")
		String jvmFlushTimeConfiguration = Globals.getString("com.med.config.mco."+mcoID+".cache.flushTime");
		if (jvmFlushTimeConfiguration == null)
			jvmFlushTimeConfiguration = "12:00 AM";
				
		// Check if current JVM time has passed the flush time on the current date. If not, use that time, else use
		// same time tomorrow.
		Date midnight = Utils.extractDate(new Date());
		Date mcoFlushDateTime = new Date(midnight.getTime()+Utils.timeToMillis(Utils.parseTime(jvmFlushTimeConfiguration)));
		if (mcoFlushDateTime.before(new Date()))
			mcoFlushDateTime = Utils.dateAdd(mcoFlushDateTime, 1, false);
			
		log.info("Next flush time calculated as " + mcoFlushDateTime + " for MCO = " + mcoID);
		
		// Put in map
		singleton.flushTimesByMCO.put(mcoID, mcoFlushDateTime);
	}

	/**
	 * Method reset. Clears current cache and reinitialized CacheManager - possibly after changes made to SOA configuration.
	 * @throws SOAException
	 */
	public static void reset()  
	{
		flushAll();
		singleton.flusher.kill = true;
		singleton.flusher = null;
		initialize();
	}

	/**
	 * Method putInCache. Places an object into given MCO's cache
	 * @param mcoID
	 * @param key
	 * @param o
	 */
	public static void putInCache(String mcoID, String key, Object o)
	{
		putInCache(mcoID, key, o, (String []) null);
	}
	
	/**
	 * Method putInCache. Places an object into given MCO's cache with associated group name.
	 * @param mcoID
	 * @param key
	 * @param o
	 * @param groupName
	 */
	public static void putInCache(String mcoID, String key, Object o, String groupName)
	{
		putInCache(mcoID, key, o, new String[] {groupName});
	}

	/**
	 * Method putInCache. Places an object into given MCO's cache with associated group names
	 * @param mcoID
	 * @param key
	 * @param o
	 * @param groupName
	 */
	public static void putInCache(String mcoID, String key, Object o, String [] groupNamesArray)
	{
	    if (log.isTraceEnabled())
	        log.trace("CacheManager - putInCache: mcoID = " +mcoID + ", key = "+key +", object = "+o+", group(s) = "+Arrays.toString(groupNamesArray));
	    
		singleton.validate(mcoID);
		singleton.validateNotNull(key, "Key must be valued.");
		singleton.validateNotNull(o, "Object to cache must be valued.");

		// Setup list of groups to include.  Always include MCO group to tag this entry as belonging to this MCO ID
		List<String> groupList = new ArrayList<String>(4);		
		groupList.add("mco."+mcoID);
		if (groupNamesArray != null)
		{
			// Add mco ID to each group name
			for (int i = 0; i < groupNamesArray.length; i++)
			{
				groupList.add(mcoID+"."+groupNamesArray[i]);
			}
		}

		// Add to cache with list of groups associated with entry
		try
		{
			singleton.cacheAdministrator.putInCache(mcoID+'.'+key, o, groupList.toArray(new String[groupList.size()]));
		}
		catch (Throwable t)
		{
			log.warn("Could not update cache for key = '"+key+"'.", t);			
			singleton.cacheAdministrator.cancelUpdate(key);
		}
	}

	
	/**
	 * Method getFromCache. Retrieve an object from the cache with the given key
	 * @param mcoID
	 * @param key
	 * @return Cachable
	 */
	public static Object getFromCache(String mcoID, String key)
	{
		singleton.validate(mcoID);
		singleton.validateNotNull(key, "Key must be valued");
		
		try
		{
			// Fetch object from cache
		    Object o = singleton.cacheAdministrator.getFromCache(mcoID+'.'+key);
		    if (log.isTraceEnabled() && o != null)
	            log.trace("CacheManager - getFromCache: mcoID = " +mcoID + ", key = "+key +", object retrieved = "+o);
		    return o;
		}
		catch (Throwable t1)
		{
		    if (log.isTraceEnabled())
		        log.trace("CacheManager - getFromCache:cacheAdministrator.getFromCache: mcoID = " +mcoID + ", key = "+key +": " + t1.getLocalizedMessage());
		    
			// OSCache will wait for us to refresh missing item and place into cache by blocking the current thread.  
			// We don't want it to work this way because it involves invasive code or callbacks from cache processing to layer above.  
			// Therefore, cancel this "update".
			try
			{
				singleton.cacheAdministrator.cancelUpdate(mcoID+'.'+key);
			}
			catch (Throwable t2)
			{
			    if (log.isTraceEnabled())
			        log.trace("CacheManager - getFromCache:cacheAdministrator.cancelUpdate: mcoID = " +mcoID + ", key = "+key +": " + t2.getLocalizedMessage());
			}

			// Return null instead of passing back silly exception
			return null;
		}
	}

	/**
	 * Method setMCOFlushTime. Sets time when MCO cache will be flushed.  This method does not usually need to be called 
	 * because CacheManager.initialize will automatically set these values automatically for all MCOs defined in the SOA 
	 * service configuration
	 * @param mcoID
	 * @param mcoTimeToFlush
	 */
	public static void setMCOFlushTime(String mcoID, Date mcoTimeToFlush)
	{
		singleton.validate(mcoID);
		singleton.validateNotNull(mcoTimeToFlush, "Organization time to flush must be valued.");
		singleton.flushTimesByMCO.put(mcoID, mcoTimeToFlush);	
	}

	/**
	 * Method flushAll. Flushes all caches across all MCOs
	 */
	public static void flushAll()
	{
		log.info("flushAll called.  Removing all elements from CacheManager.");
		
		try
		{
			singleton.cacheAdministrator.flushAll();
		}
		catch (Throwable t)
		{
			log.error("Error in calling to OSCache - flushAll.", t);
		}

	}

	/**
	 * Method flushMCO. 
	 * @param mcoID
	 */
	public static void flushMCO(String mcoID)
	{
		singleton.validate(mcoID);
		log.info("flushMCO called for MCO = '"+mcoID+"'...");
		try
		{
			singleton.cacheAdministrator.flushGroup("mco."+mcoID);	
		}
		catch (Throwable t)
		{
			log.info("Error in calling to OSCache - flushMCO for MCO = '"+mcoID+"'.  If NullPointerException, this is safe to ignore since error is thrown when the group is not already in the cache.", t);
		}
	}

	/**
	 * Method flushGroup.
	 * @param mcoID
	 * @param groupName
	 */
	public static void flushGroup(String mcoID, String groupName)
	{
		singleton.validate(mcoID);
		singleton.validateNotNull(groupName, "Group name must be valued.");
		log.info("flushGroup called for MCO = '"+mcoID+"' and group = '"+groupName+"'...");
		try
		{
			singleton.cacheAdministrator.flushGroup(mcoID+'.'+groupName);	
		}
		catch (Throwable t)
		{
			// OSCache stupidly returns NullPointerException if group does not exist in the cache
			log.info("Error in calling to OSCache - flushGroup for MCO = '"+mcoID+"' and group = '"+groupName+"'.  If NullPointerException, this is safe to ignore since error is thrown when the group is not already in the cache.", t);
		}
	}

	/**
	 * Method flushItem.
	 * @param mcoID
	 * @param key
	 */
	public static void flushItem(String mcoID, String key)
	{
		singleton.validate(mcoID);
		singleton.validateNotNull(key, "Key must be valued.");
		log.trace("flushItem called for MCO = '"+mcoID+"' and key = '"+key+"'...");
		try
		{
			singleton.cacheAdministrator.flushEntry(mcoID+'.'+key);	
		}
		catch (Throwable t)
		{
			log.error("Error in calling to OSCache - flushEntry.", t);
		}
	}

	/**
	 * Method validate. Common validation when calling CacheManager function
	 * @param mcoID
	 */
	private void validate(String mcoID)
	{
		if (mcoID == null || mcoID.length() == 0)
			throw new IllegalArgumentException("MCO ID must be valued when accessing the CacheManager API.");	
	}
	
	/**
	 * Method validateNotNull.
	 * @param o
	 * @param validationMessage
	 */
	private void validateNotNull(Object o, String validationMessage)
	{
		if (o == null)
			throw new IllegalArgumentException(validationMessage);
	}

	/**
	 * Method getEntryAddedCount. Returns # of entries added since cache listener was started.
	 * @return int
	 */
	public static int getEntryAddedCount()
	{
	    if (cacheEntryEventListener == null) 
	    	throw new RuntimeException("Must call CacheManager - startCacheListener before calling this method.");
	    startCacheListener();
	    return cacheEntryEventListener.getEntryAddedCount();
	}
	
	/**
	 * Method getEntryFlushedCount. Returns # of entries flushed since cache listener was started.
	 * @return int
	 */
	public static int getEntryFlushedCount()
	{
	    if (cacheEntryEventListener == null) 
	    	throw new RuntimeException("Must call CacheManager - startCacheListener before calling this method.");
	    startCacheListener();
	    return cacheEntryEventListener.getEntryFlushedCount();
	}

	/**
	 * Method getEntryRemovedCount. Returns # of entries removed since cache listener was started.
	 * @return int
	 */
	public static int getEntryRemovedCount()
	{
	    if (cacheEntryEventListener == null) 
	    	throw new RuntimeException("Must call CacheManager - startCacheListener before calling this method.");
	    startCacheListener();
	    return cacheEntryEventListener.getEntryRemovedCount();
	}
	
	/**
	 * Method getEntryUpdatedCount. Returns # of entries updated since cache listener was started. 
	 * @return int
	 */
	public static int getEntryUpdatedCount()
	{
	    if (cacheEntryEventListener == null) 
	    	throw new RuntimeException("Must call CacheManager - startCacheListener before calling this method.");
	    startCacheListener();
	    return cacheEntryEventListener.getEntryUpdatedCount();
	}
	
	/**
	 * Method startCacheListener. This should be used primarily for debug/analysis.  This will activate a cache listener
	 * that will listen for events in the underlying OSCache caching mechanism.   
	 * 
	 * Returns void
	 */
	public static void startCacheListener()
	{
	    if (cacheEntryEventListener == null)
	    {
	        cacheEntryEventListener = new CacheEntryEventListenerImpl();
	        singleton.cacheAdministrator.getCache().addCacheEventListener(cacheEntryEventListener, CacheEntryEventListener.class);
	    }
	}
	
	/**
	 * Method stopCacheListener. Deactivate the cache listener
	 * 
	 * Returns void
	 */
	public static void stopCacheListener()
	{
	    if (cacheEntryEventListener != null)
	    {
	    	singleton.cacheAdministrator.getCache().removeCacheEventListener(cacheEntryEventListener, CacheEntryEventListener.class);
	        cacheEntryEventListener = null;
	    }
	}
	
	/**
	 * Private class to flush MCO caches
	 **/
	private static class FlushThread extends Thread
	{
		protected boolean kill = false;
		
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			// Build a list of all MCO IDs configured for the application
			Set<String> mcos = getMcoSet();

			log.info("Flush thread started...");
			
			while (!kill)
			{
				try
				{
					// Check every minute
					sleep(60000);	
				}
				catch (InterruptedException e)
				{}
				
				Iterator<String> iter = mcos.iterator();
				while (iter.hasNext())
				{
					String mcoID = iter.next();

					Date currentJVMTime = new Date();
					Date flushTime = (Date) singleton.flushTimesByMCO.get(mcoID);
					if (flushTime == null)
					{
						setNextFlushTime(mcoID);
					}
					else if (currentJVMTime.after(flushTime))
					{
						// Flush the MCO
						flushMCO(mcoID);
						
						log.info("Cached items for MCO ID '"+mcoID+"' flushed.  JVM time = "+currentJVMTime);
						
						// Set next flush time
						setNextFlushTime(mcoID);
					}
				}
				
			}
		}

	}

}
