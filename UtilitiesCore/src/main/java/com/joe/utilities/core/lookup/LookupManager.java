package com.joe.utilities.core.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;

import com.joe.utilities.core.configuration.admin.domain.IApplicationConfiguration;
import com.joe.utilities.core.hibernate.repository.ApplicationConfigurationRepository;
import com.joe.utilities.core.hibernate.repository.LookupRepository;
import com.joe.utilities.core.serviceLocator.ServiceLocator;
import com.joe.utilities.core.util.ILookupProfile;
import com.joe.utilities.core.util.IStandardFieldLookupProfile;

/**
 * This class manages the retrieval and caching of lookup data that resides in database.
 * 
 * @author John J. Jones III, Dave Ousey
 * @version 1.0
 * 
 *          Creation date: Dec 15, 2004 4:12:50 PM Copyright (c) 2004 MEDecision, Inc. All rights reserved.
 */
public class LookupManager implements DisposableBean {
	/** Name of class. Used for cache and map keys. */
	public static final String NAME = LookupManager.class.getName();

	/** Reference to class singleton */
	private static LookupManager singletonInstance;

	/** Reference to log for this class */
	private static Log log = LogFactory.getLog(LookupManager.class);

	/** Group name for domain entries in CacheManager */
	public static final String LOOKUP_GROUP_CACHE_NAME = "lookupProfileGroup";
	public static final String APPLICATION_CONFIG_GROUP_CACHE_NAME = "appConfigGroup";

	public static final String DEFAULT_MCO_ID = "MDL";

	/**
	 * Method getInstance. Returns existing singleton instance of class or returns new class if one does not exist.
	 * 
	 * @return LookupManager
	 */
	public static LookupManager getInstance() {

		if (singletonInstance == null) {
			singletonInstance = new LookupManager();
		}

		return singletonInstance;
	}

	@Autowired
	private ApplicationContext applicationContext;

	private LookupRepository lookupRepository;

	private ApplicationConfigurationRepository applicationConfigRepo;

	/**
	 * 
	 */
	private LookupManager() {
	}

	/**
	 * Method getLookupProfile. Returns ILookupProfile object from cached items.
	 * 
	 * @param mcoID
	 * @param lookupClassName
	 * @param itemCode
	 * @return Cachable
	 */
	public ILookupProfile getLookupProfile(String mcoID, String lookupClassName, String itemCode) {
		if (log.isDebugEnabled())
			log.debug("getLookupProfile: " + lookupClassName + "." + itemCode);

		return getLookupMap(mcoID, lookupClassName).get(itemCode);
	}

	/**
	 * getLookupProfile
	 * 
	 * @param lookupClassName
	 * @param itemCode
	 * @return ILookupProfile
	 */
	public ILookupProfile getLookupProfile(String lookupClassName, String itemCode) {
		return getLookupProfile(DEFAULT_MCO_ID, lookupClassName, itemCode);
	}

	/**
	 * Method getDescription. Returns description for specified ILookupProfile
	 * 
	 * @param mcoID
	 * @param lookupClassName
	 * @param itemCode
	 * @return String
	 */
	public String getLookupDescription(String mcoID, String lookupClassName, String itemCode) {
		ILookupProfile theItem = getLookupProfile(mcoID, lookupClassName, itemCode);
		if (theItem != null)
			return theItem.getDescription();
		else
			return null;
	}

	/**
	 * getLookupDescription
	 * 
	 * @param lookupClassName
	 * @param itemCode
	 * @return String
	 */
	public static String getLookupDescription(String lookupClassName, String itemCode) {
		return getInstance().getLookupDescription(DEFAULT_MCO_ID, lookupClassName, itemCode);
	}

	/**
	 * Method getLookupMap. Returns map of domain items for simple domain table.
	 * 
	 * @param mcoID
	 * @param lookupClassName
	 * @return Map<String, ILookupProfile>
	 */
	public Map<String, ILookupProfile> getLookupMap(String mcoID, String lookupClassName) {
		if (mcoID == null || mcoID.length() == 0)
			throw new IllegalArgumentException("Null MCO ID passed to LookupManager processing.");

		if (lookupClassName == null || lookupClassName.length() == 0)
			throw new IllegalArgumentException("Null domain name passed to LookupManager processing.");

		// Get the cache entry for this domain
		Map<String, ILookupProfile> lookupMap = (Map<String, ILookupProfile>) CacheManager.getFromCache(mcoID, NAME
				+ '.' + lookupClassName);
		if (lookupMap == null)
			lookupMap = retrieveLookupList(lookupClassName, mcoID);

		// Return all items
		return lookupMap;
	}

	/**
	 * Method getLookupMap. Returns map of domain items for simple domain table.
	 * 
	 * @param lookupClassName
	 * @return Map<String, ILookupProfile>
	 */
	public Map<String, ILookupProfile> getLookupMap(String lookupClassName) {
		return getLookupMap(DEFAULT_MCO_ID, lookupClassName);
	}

	/**
	 * getLookupList
	 * 
	 * @param lookupClassName
	 * @return List<ILookupProfile>
	 */
	public static List<ILookupProfile> getLookupList(String lookupClassName) {
		return getSortedLookupList(DEFAULT_MCO_ID, lookupClassName);
	}

	/**
	 * Method retrieveLookup. Retrieves domain items from database and sets to cache.
	 * 
	 * @param lookupClassName
	 * @param mcoID
	 */
	private Map<String, ILookupProfile> retrieveLookupList(String lookupClassName, String mcoID) {
		LookupRepository lookupRepository = getLookupRepository();
		List<ILookupProfile> lookupList = lookupRepository.getLookupList(lookupClassName);

		// Construct the map
		Map<String, ILookupProfile> simpleMap = new HashMap<String, ILookupProfile>(lookupList.size());

		// Populate with data from the database
		for (ILookupProfile lookup : lookupList) {
			simpleMap.put(lookup.getCode(), lookup);
		}

		// Add map to MCO's domain cache
		CacheManager.putInCache(mcoID, NAME + '.' + lookupClassName, simpleMap, LOOKUP_GROUP_CACHE_NAME);
		return simpleMap;
	}

	/**
	 * Method getStandardFieldLookupMap. Returns map of domain items for simple domain table.
	 * 
	 * @param mcoID
	 * @param lookupClassName
	 * @return Map<String, IStandardFieldLookupProfile>
	 */
	public Map<String, IStandardFieldLookupProfile> getStandardFieldLookupMap(String mcoID, String lookupClassName) {
		if (mcoID == null || mcoID.length() == 0)
			throw new IllegalArgumentException("Null MCO ID passed to LookupManager processing.");

		if (lookupClassName == null || lookupClassName.length() == 0)
			throw new IllegalArgumentException("Null domain name passed to LookupManager processing.");

		// Get the cache entry for this domain
		Map<String, IStandardFieldLookupProfile> lookupMap = (Map<String, IStandardFieldLookupProfile>) CacheManager
				.getFromCache(mcoID, NAME + '.' + lookupClassName);
		if (lookupMap == null)
			lookupMap = retrieveStandardFieldLookupList(lookupClassName, mcoID);

		// Return all items
		return lookupMap;
	}

	/**
	 * Method getStandardFieldLookupMap. Returns map of domain items for simple domain table.
	 * 
	 * @param lookupClassName
	 * @return Map<String, IStandardFieldLookupProfile>
	 */
	public Map<String, IStandardFieldLookupProfile> getStandardFieldLookupMap(String lookupClassName) {
		return getStandardFieldLookupMap(DEFAULT_MCO_ID, lookupClassName);
	}

	/**
	 * getLookupList
	 * 
	 * @param lookupClassName
	 * @return List<IStandardFieldLookupProfile>
	 */
	public static List<IStandardFieldLookupProfile> getStandardFieldLookupList(String lookupClassName) {
		return getSortedStandardFieldLookupList(DEFAULT_MCO_ID, lookupClassName);
	}

	/**
	 * Method getSortedStandardFieldLookupList.
	 * 
	 * @param mcoID
	 * @param lookupClassName
	 * @return List<IStandardFieldLookupProfile>
	 */
	public static List<IStandardFieldLookupProfile> getSortedStandardFieldLookupList(String mcoID,
			String lookupClassName) {
		return getSortedStandardFieldLookupList(getInstance().getStandardFieldLookupMap(mcoID, lookupClassName));
	}

	/**
	 * Method getSortedLookupList.
	 * 
	 * @param lookupMap
	 * @return List<ILookupProfile>
	 */
	public static List<IStandardFieldLookupProfile> getSortedStandardFieldLookupList(
			Map<String, IStandardFieldLookupProfile> lookupMap) {
		List<IStandardFieldLookupProfile> sortedILookupProfiles = new ArrayList<IStandardFieldLookupProfile>(
				lookupMap.values());
		Collections.sort(sortedILookupProfiles, new Comparator<IStandardFieldLookupProfile>() {

			public int compare(IStandardFieldLookupProfile lookup1, IStandardFieldLookupProfile lookup2) {
				if (lookup1.getDescription().equalsIgnoreCase(lookup2.getDescription()))
					return 1;
				else
					return lookup1.getDescription().compareToIgnoreCase(lookup2.getDescription());
			}
		});

		return sortedILookupProfiles;
	}

	/**
	 * Method getSortedStandardFieldLookupList.
	 * 
	 * @param lookupClassName
	 * @return List<IStandardFieldLookupProfile>
	 */
	public static List<IStandardFieldLookupProfile> getSortedStandardFieldLookupList(String lookupClassName) {
		return getSortedStandardFieldLookupList(DEFAULT_MCO_ID, lookupClassName);
	}

	/**
	 * Method retrieveStandardFieldLookup. Retrieves domain items from database and sets to cache.
	 * 
	 * @param lookupClassName
	 * @param mcoID
	 */
	private Map<String, IStandardFieldLookupProfile> retrieveStandardFieldLookupList(
			String standardFieldLookupClassName, String mcoID) {
		LookupRepository lookupRepository = getLookupRepository();
		List<IStandardFieldLookupProfile> lookupList = lookupRepository
				.getStandardFieldLookupList(standardFieldLookupClassName);

		// Construct the map
		Map<String, IStandardFieldLookupProfile> simpleMap = new HashMap<String, IStandardFieldLookupProfile>(
				lookupList.size());

		// Populate with data from the database
		for (IStandardFieldLookupProfile lookup : lookupList) {
			simpleMap.put(lookup.getCode(), lookup);
		}

		// Add map to MCO's domain cache
		CacheManager.putInCache(mcoID, NAME + '.' + standardFieldLookupClassName, simpleMap, LOOKUP_GROUP_CACHE_NAME);
		return simpleMap;
	}

	/**
	 * Method isValidLookupProfile.
	 * 
	 * @param mcoID
	 * @param lookupClassName
	 * @param code
	 * @return boolean
	 */
	public static boolean isValidLookupProfile(String mcoID, String lookupClassName, String code) {
		return null != getInstance().getLookupProfile(mcoID, lookupClassName, code);
	}

	/**
	 * isValidLookupProfile
	 * 
	 * @param lookupClassName
	 * @param code
	 * @return boolean
	 */
	public static boolean isValidLookupProfile(String lookupClassName, String code) {
		return isValidLookupProfile(DEFAULT_MCO_ID, lookupClassName, code);
	}

	/**
	 * Method flushMCOLookup. Flush all domain table entries for the given MCO.
	 * 
	 * @param mcoID
	 */
	public static void flushMCOLookup(String mcoID) {
		CacheManager.flushGroup(mcoID, LOOKUP_GROUP_CACHE_NAME);
		CacheManager.flushGroup(mcoID, APPLICATION_CONFIG_GROUP_CACHE_NAME);
	}

	/**
	 * Method setTestLookupMap. Allows test processing to set a domain map into the LookupManager cache as an
	 * alternative to the domain values in the current database.
	 * 
	 * @param mcoID
	 * @param lookupClassName
	 * @param lookupMap
	 */
	public static void setTestLookupMap(String mcoID, String lookupClassName, Map<String, ILookupProfile> lookupMap) {
		// Validate that MCO ID = "GNL". Must only be used for test purposes
		if (!"GNL".equals(mcoID))
			throw new IllegalArgumentException(
					"Cannot call LookupManager's setTestLookupMap method outside of JUnit test code with MCO ID = 'GNL'.");

		// Validate that OP is valued
		if (lookupClassName == null)
			throw new IllegalArgumentException(
					"Null domain name parameter passed to 'setTestLookupMap' method of LookupManager.");

		// Flush any existing entry
		CacheManager.flushItem(mcoID, NAME + '.' + lookupClassName);

		// Place new entry
		CacheManager.putInCache(mcoID, NAME + '.' + lookupClassName, lookupMap, LOOKUP_GROUP_CACHE_NAME);
	}

	/**
	 * Method getSortedLookupList.
	 * 
	 * @param mcoID
	 * @param lookupClassName
	 * @return List<ILookupProfile>
	 */
	public static List<ILookupProfile> getSortedLookupList(String mcoID, String lookupClassName) {
		return getSortedLookupList(getInstance().getLookupMap(mcoID, lookupClassName));
	}

	/**
	 * Method getSortedLookupList.
	 * 
	 * @param lookupClassName
	 * @return List<ILookupProfile>
	 */
	public static List<ILookupProfile> getSortedLookupList(String lookupClassName) {
		return getSortedLookupList(DEFAULT_MCO_ID, lookupClassName);
	}

	/**
	 * Method getSortedLookupList.
	 * 
	 * @param lookupMap
	 * @return List<ILookupProfile>
	 */
	public static List<ILookupProfile> getSortedLookupList(Map<String, ILookupProfile> lookupMap) {
		List<ILookupProfile> sortedILookupProfiles = new ArrayList<ILookupProfile>(lookupMap.values());
		Collections.sort(sortedILookupProfiles, new Comparator<ILookupProfile>() {

			public int compare(ILookupProfile lookup1, ILookupProfile lookup2) {
				if (lookup1.getDescription().equalsIgnoreCase(lookup2.getDescription()))
					return 1;
				else
					return lookup1.getDescription().compareToIgnoreCase(lookup2.getDescription());
			}
		});

		return sortedILookupProfiles;
	}

	/**
	 * getApplicationConfiguration
	 * 
	 * @param configurationName
	 * @return IApplicationConfiguration
	 */
	public static IApplicationConfiguration getApplicationConfiguration(String configurationName) {
		return getInstance().getApplicationConfiguration(DEFAULT_MCO_ID, configurationName);
	}

	/**
	 * getApplicationConfiguration
	 * 
	 * @param mcoID
	 * @param configurationName
	 * @return IApplicationConfiguration
	 */
	private IApplicationConfiguration getApplicationConfiguration(String mcoID, String configurationName) {
		if (mcoID == null || mcoID.length() == 0)
			throw new IllegalArgumentException("Null MCO ID passed to LookupManager processing.");

		if (configurationName == null || configurationName.length() == 0)
			throw new IllegalArgumentException("Null app configuration name passed to LookupManager processing.");

		// Get the cache entry for this domain
		IApplicationConfiguration appConfiguration = (IApplicationConfiguration) CacheManager.getFromCache(mcoID, NAME
				+ ".appconfig." + configurationName);
		if (appConfiguration == null)
			appConfiguration = retrieveApplicaitonConfiguration(mcoID, configurationName);

		// Return all items
		return appConfiguration;
	}

	/**
	 * retrieveApplicaitonConfiguration
	 * 
	 * @param configurationName
	 * @return IApplicationConfiguration
	 */
	private IApplicationConfiguration retrieveApplicaitonConfiguration(String mcoID, String configurationName) {
		ApplicationConfigurationRepository acr = getAppConfigurationRepository();
		IApplicationConfiguration ac = acr.retreiveApplicationConfigProperty(configurationName);

		// Add map to MCO's domain cache
		if (ac != null)
			CacheManager.putInCache(mcoID, NAME + ".appconfig." + configurationName, ac,
					APPLICATION_CONFIG_GROUP_CACHE_NAME);
		return ac;
	}



	/**
	 * @return
	 */
	private ApplicationConfigurationRepository getAppConfigurationRepository() {

		/*
		 * hack: if this bean wasn't wired by spring, use ServiceLocator
		 */
		if (this.applicationContext == null) {
			return (ApplicationConfigurationRepository) ServiceLocator.getInstance().getBean(
					"applicationConfigRepository");
		} else {
			return this.applicationConfigRepo;
		}
	}

	/***
	 * Lookup an {@link IProfile} instance and properly cast it to the appropriate type.
	 * 
	 * @param <T>
	 *            The class type
	 * @param clazz
	 *            The class which you are trying to lookup
	 * @param code
	 *            The code which you are looking up
	 * @return The instance, or null
	 */
	public <T extends ILookupProfile> T lookup(Class<T> clazz, String code) {
		return (T) getLookupProfile(clazz.getName(), code);
	}

	/**
	 * Hack for figuring out if this bean was loaded via Spring
	 * 
	 * @return
	 */
	private LookupRepository getLookupRepository() {

		/*
		 * hack: if this bean wasn't wired by spring, use ServiceLocator
		 */
		if (this.applicationContext == null) {
			// execute resource call to retrieve table contents
			ServiceLocator svcLocator = ServiceLocator.getInstance();

			// Call lookup repository
			return (LookupRepository) svcLocator.getBean("lookupRepository");
		} else {
			return lookupRepository;
		}
	}

	@Required
	public void setLookupRepository(LookupRepository lookupRepository) {
		this.lookupRepository = lookupRepository;
	}

	@Required
	public void setApplicationConfigRepo(ApplicationConfigurationRepository applicationConfigRepo) {
		this.applicationConfigRepo = applicationConfigRepo;
	}

	/**
	 * @formatter:off
	 * Workaround for some unit tests.
	 * 
	 * The workaround is needed because {@link LookupManager} is loaded two ways:
	 * 1. wired via Spring
	 * 2. manually constructed using {@link LookupManager#getInstance()}
	 * 
	 * If you do (1) first and then try (2), then {@link LookupManager#applicationContext} will not be null, and
	 * then tests that use a mock {@link ServiceLocator} will fail because  {@link LookupManager#applicationContext} will not be null
	 * 
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		setApplicationConfigRepo(null);
		this.applicationContext = null;
		setApplicationConfigRepo(null);
		
	}
	
	
}