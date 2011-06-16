package com.joe.utilities.core.configuration.admin;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.joe.utilities.core.configuration.admin.domain.IApplicationConfiguration;
import com.joe.utilities.core.configuration.admin.facade.IApplicationConfigurationFacade;
import com.joe.utilities.core.serviceLocator.ServiceLocator;

/** 
 * This class contains static methods used to find Application Configuration records.
 * It also contains static methods used to retrieve standard value types
 */
public class ApplicationConfigurationUtility {
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * Returns True if the given Application Configuration exists and has a value of either "1" or "true".
	 * @param applicationConfigCode The application configuration parameter being tested.
	 * @return True if the given Application Configuration exists and has a value of either "1" or "true".  Otherwise return false.
	 */
	public static boolean isEnabled(String applicationConfigCode) {
        String value = getValue(applicationConfigCode);
        return StringUtils.equals(value, "1") || StringUtils.equalsIgnoreCase(value, "true");
	}
	
	/**
	 * Returns True if the given Application Configuration exists and has a value of either "1" or "true".
	 * @param applicationConfigCode The application configuration parameter being tested.
	 * @return True if the given Application Configuration exists and has a value of either "1" or "true".  Otherwise return false.
	 */
	public static boolean isEnabled(ApplicationConfigurationEnum applicationConfigCode) {
		return isEnabled(applicationConfigCode.getConfigCode());
	}
	
	/**
	 * Returns True if the given Application Configuration exists and has a value of "1".
	 * @param applicationConfigCode The application configuration parameter being tested.
	 * @return True if the given Application Configuration exists and has a value of "1" .  Otherwise return false.
	 */
	public static boolean isEnabledBit(String applicationConfigCode) {
		return StringUtils.equals(getValue(applicationConfigCode), "1");
	}
	
	/**
	 * Returns True if the given Application Configuration exists and has a value of "1".
	 * @param applicationConfigCode The application configuration parameter being tested.
	 * @return True if the given Application Configuration exists and has a value of "1" .  Otherwise return false.
	 */
	public static boolean isEnabledBit(ApplicationConfigurationEnum applicationConfigCode) {
		return isEnabledBit(applicationConfigCode.getConfigCode());
	}
	

	/**
	 * @return
	 */
	private static IApplicationConfigurationFacade getApplicationConfigurationFacade() {
		return (IApplicationConfigurationFacade) ServiceLocator
				.getInstance().getBean("applicationConfigFacade");
	}
	
	/** Returns the value associated with the application configuration parameter as a long.
	 * 
	 * @param applicationConfigCode The application configuration parameter to be returned
	 * @param defaultValue The value to be returned if the application parameter is null or not a long.
	 * @return The long value of the application configuration parameter or default value if the parameter is null or not a long.
	 */
	public static long getLongValue(String applicationConfigCode, long defaultValue){
		return NumberUtils.toLong(getValue(applicationConfigCode), defaultValue);
	}
	
	/** Returns the value associated with the application configuration parameter as a long.
	 * 
	 * @param applicationConfigCode The application configuration parameter to be returned
	 * @param defaultValue The value to be returned if the application parameter is null or not a long.
	 * @return The long value of the application configuration parameter or default value if the parameter is null or not a long.
	 */
	public static long getLongValue(ApplicationConfigurationEnum applicationConfigCode, long defaultValue){
		return getLongValue(applicationConfigCode.getConfigCode(), defaultValue);
	}
	
	/** Returns the value associated with the application configuration parameter as an int.
	 * 
	 * @param applicationConfigCode The application configuration parameter to be returned
	 * @param defaultValue The value to be returned if the application parameter is null or not a number.
	 * @return The long value of the application configuration parameter or default value if the parameter is null or not a int.
	 */
	public static int getIntValue(String applicationConfigCode, int defaultValue){
		return NumberUtils.toInt(getValue(applicationConfigCode), defaultValue);
	}
	
	/** Returns the value associated with the application configuration parameter as an int.
	 * 
	 * @param applicationConfigCode The application configuration parameter to be returned
	 * @param defaultValue The value to be returned if the application parameter is null or not a number.
	 * @return The long value of the application configuration parameter or default value if the parameter is null or not a int.
	 */
	public static int getIntValue(ApplicationConfigurationEnum applicationConfigCode, int defaultValue){
		return getIntValue(applicationConfigCode.getConfigCode(), defaultValue);
	}
	
	/** Returns the value associated with the application configuration parameter as an string.
	 * 
	 * @param applicationConfigCode The application configuration parameter to be returned
	 * @param defaultValue The value to be returned if the application parameter is null.
	 * @return The sting value of the application configuration parameter or default value if the parameter is null.
	 */
	public static String getStringValue(String applicationConfigCode, String defaultValue){
        String value = getValue(applicationConfigCode);
        return value != null ? value : defaultValue; 
	}
	
	/** Returns the value associated with the application configuration parameter as an string.
	 * 
	 * @param applicationConfigCode The application configuration parameter to be returned
	 * @param defaultValue The value to be returned if the application parameter is null.
	 * @return The sting value of the application configuration parameter or default value if the parameter is null.
	 */
	public static String getStringValue(ApplicationConfigurationEnum applicationConfigCode, String defaultValue){
        return getStringValue(applicationConfigCode.getConfigCode(), defaultValue); 
	}
	
	/**
	 * Returns the value associated with the application configuration or null if the configuration doesn't exist
	 * 
	 * @param applicationConfigCode The application configuration parameter to be returned
	 * @return The value associated with the application configuration or null if the configuration doesn't exist
	 */
	private static String getValue(String applicationConfigCode) {
		if (ServiceLocator.getInstance() == null) {
			return null;
		}
		
		IApplicationConfigurationFacade appConfigFacade = getApplicationConfigurationFacade();
		
		if (appConfigFacade == null) {
			return null;
		}
		
		IApplicationConfiguration appConfig = appConfigFacade
				.getApplicationConfiguration(applicationConfigCode);

		if (appConfig != null) {
			return appConfig.getValue();
		}

		return null;
	}
}
