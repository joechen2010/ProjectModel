package com.joe.utilities.core.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;

/**
 * Helper class that provides a mechanism for storing and retrieving 
 * Logging Context Information.  
 * Currently, Log4j MDC implementation is used for this purpose. 
 */
public class LoggingContextHelper
{
	public static final String USER_ID = "UserId";
	public static final String TRANSACTION_ID = "TransactionId";
	public static final String TRANSACTION_TYPE = "TransactionType";
	
	private static Log log = LogFactory.getLog(LoggingContextHelper.class);
	
	/**
	 * Add a new key value pair to the logging context
	 * @param 	key
	 * @param 	value
	 */
	public static void addValueToContent(String key, String value)
	{
		log.trace("Adding value to diagnostic content - key: " + key + " - value: " + value);
		if (value != null) 
			MDC.put(key, value);
		else
			MDC.put(key, "No Value");
	}
	
	/**
	 * Remove the key value pair from the logging context
	 * diagnostic context.
	 */
	public static void removeValueFromContext(String key)
	{
		log.trace("Removing value from diagnostic content - key: " + key);
		MDC.remove(key);
	}
	
	/**
	 * Get the value for a specific key from the logging context
	 * @param 	key
	 */
	public static String getValueFromContext(String key)
	{
		log.trace("Retrieving value from diagnostic content - key: " + key);
		return (String)MDC.get(key);
	}
	
	/**
	 * Initialize the UserId in the Logging Context
	 */
	public static void initializeUserId()
	{
		MDC.put(USER_ID, "NO USER");
	}

	/**
	 * Initialize the UserId in the Logging Context
	 */
	public static void initializeTransactionId()
	{
		MDC.put(TRANSACTION_ID, "000000");
	}

}