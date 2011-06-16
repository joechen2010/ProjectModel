package com.joe.utilities.core.configuration;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* This Globals singleton class wraps Apache Commons configuration processing to provide a centralized means to
* access configurable application settings.
* @author rrichard
*
* Creation date: 03/13/2007 9 AM
* Copyright (c) 2007 MEDecision, Inc.  All rights reserved.
*/
public final class Globals {

    private static Log logger = LogFactory.getLog(Globals.class);

    /**
     * The name of the Default property file.
     */
    private static final String DEFAULTS_PROPERTY_FILE = "MEDefaults.properties";
    
    private CompositeConfiguration globalConfig = null;

    private static Globals instance;	

    /**
     * Private constructor to construct singleton
     */
    private Globals() throws GlobalConfigurationException {

        // Create composite configuration
        this.globalConfig = new CompositeConfiguration();
         
        // Load system properties
    	Configuration system = new SystemConfiguration();

        globalConfig.addConfiguration(system);
        
        // Load customer's properties
        PropertiesConfiguration customerGlobalProperties = loadCustomerGlobalProperties();
        if (customerGlobalProperties != null)
        	globalConfig.addConfiguration(customerGlobalProperties);
        
        // Load default properties
        PropertiesConfiguration defaultProperties = loadDefaultProperties();
        if (defaultProperties != null)
            globalConfig.addConfiguration(defaultProperties);
        
     }

	/**
	 * Load in default properties for the client application.  These kinds of properties are more rarely
	 * changed and this properties file is typically kept under source code control with the client application.
	 * @return PropertiesConfiguration
	 */
    public static PropertiesConfiguration loadDefaultProperties() {

        PropertiesConfiguration defaultProperties = null;
        try
        {
        	defaultProperties = new PropertiesConfiguration(DEFAULTS_PROPERTY_FILE);
        }
        catch (ConfigurationException e)
        {
    	/**
    	 * Removed this warning message as most clients no longer use MEDecision.properties and
    	 * and the file is no longer a part of our model solution.  We're going to support looking
    	 * for the file in the name backward compatibility but longer log a warning message. In
    	 * essence, we're swallowing the error because many users of utilities core are placing
    	 * an empty MEDecision.properties file in their class path just to remove this warning 
    	 * (callers still want to see other WARN-level messages).  Eventually, this lookup of
    	 * the default properties file should be removed or perhaps remain but we consider 
    	 * globals.xml the default properties file.  
    	 */        	
         //logger.warn("Could not open MEDefaults.properties file at '"+DEFAULTS_PROPERTY_FILE+"'. Skipping this properties file.");
        }
		return defaultProperties;
	}

	/**
	 * Load in override properties for the client application.  These kinds of properties 
	 * are usually environment-specific whose concrete values are typically not kept under source code control.
	 * 
	 * Properties may be stored in either XML or legacy format.
	 * 
	 * PropertiesConfiguration
	 */
	public static PropertiesConfiguration loadCustomerGlobalProperties() {

        PropertiesConfiguration customerGlobalProperties = null;
        String globalsPropertyFilePath = locateSysConfig();
        
        if (globalsPropertyFilePath == null){
			throw new GlobalConfigurationException("Customer Globals properties file path not specified.");
        }
        
        //parse based on file extension type
        // parse xml format
        if ( (globalsPropertyFilePath.toLowerCase()).indexOf(".xml")>0 ) {

            try {
            	customerGlobalProperties = new XMLPropertiesConfiguration(globalsPropertyFilePath);
                logger.info("Global properties file loaded from: " + customerGlobalProperties.getBasePath());
    		} catch (ConfigurationException e) {
                logger.info("Could not load globals properties as XML from file at '"+globalsPropertyFilePath+"'.", e);
    			throw new GlobalConfigurationException("Could not load globals properties as XML from file at '"+globalsPropertyFilePath+"'.", e);
    		}

    	// parse legacy properties format
        } else {
        	
	        try {
	        	customerGlobalProperties = new PropertiesConfiguration(globalsPropertyFilePath);
	            logger.info("Global properties file loaded from: " + customerGlobalProperties.getBasePath());
			} catch (ConfigurationException e) {
	            logger.error("Could not open globals properties file at '"+globalsPropertyFilePath+"'.", e);
	            throw new GlobalConfigurationException("Could not open globals properties file at '"+globalsPropertyFilePath+"'.", e);
	        }
       	
        }

        return customerGlobalProperties;
	}

    /**
     * Returns the location of the Globals properties file.  The file must be in the following places:<br>
     * <li> It can be fully specified by the -Dcom.med.comon.globals system property
     * <li> It can be placed in the system directory specified by the system property server.root (if present)
     * <li> Finally, if -Dcom.med.common.globals is not specified, and server.root is not present, it can be placed
     * in the system directory specified by user.dir.
     *
     * @return Returns the location of the Globals properties file.
     */
    private static String locateSysConfig() throws GlobalConfigurationException{
		String fileName = null;
		try {
			fileName = System.getProperties().getProperty("com.med.common.globals");
			if (fileName == null) {

				if (System.getProperties().getProperty("server.root") != null) {
					String rootPath =
						System.getProperties().getProperty("server.root")
							+ System.getProperties().getProperty("file.separator");

					fileName = rootPath + "globals.xml";
					if (!fileExists(fileName)) {
						fileName = rootPath + "globals.properties";
					}
				} else if (System.getProperties().getProperty("user.dir") != null) {
					String rootPath =
						System.getProperties().getProperty("user.dir")
							+ System.getProperties().getProperty("file.separator");

					fileName = rootPath + "globals.xml";
					if (fileExists(fileName)) {
						fileName = rootPath + "globals.properties";
					}
				}
			}
		} catch (Exception e) {
			throw new GlobalConfigurationException("Error loading global variables file.", e);
		}

		return fileName; // try to return something whether or not it exists
	}

	private static boolean fileExists(String fileName) {
		boolean exists = false;
		if (fileName != null) {
			File file = new File(fileName);
			exists = file.exists() && file.canRead();
		}
		return exists;
	}

    /**
	 * Method getInstance.
	 *
	 * @return Globals
	 */
    public static Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }

    /**
     * Forces Globals to refresh its configuration.
     */
    public static void clearInstance() {
    	instance = null;
    }

    /**
     * Method getBigDecimal.
     * @param arg0
     * @return BigDecimal
     */
    public static BigDecimal getBigDecimal(String arg0) {
        return getInstance().globalConfig.getBigDecimal(arg0);
    }

    /**
     * Method getBigInteger.
     * @param arg0
     * @return BigInteger
     */
    public static BigInteger getBigInteger(String arg0) {
        return getInstance().globalConfig.getBigInteger(arg0);
    }

    /**
     * Method getBoolean.
     * @param arg0
     * @return boolean
     */
    public static boolean getBoolean(String arg0) {
        return getInstance().globalConfig.getBoolean(arg0);
    }

    /**
     * Method getByte.
     * @param arg0
     * @return byte
     */
    public static byte getByte(String arg0) {
        return getInstance().globalConfig.getByte(arg0);
    }

    /**
     * Method getDouble.
     * @param arg0
     * @return double
     */
    public static double getDouble(String arg0) {
        return getInstance().globalConfig.getDouble(arg0);
    }

    /**
     * Method getFloat.
     * @param arg0
     * @return float
     */
    public static float getFloat(String arg0) {
        return getInstance().globalConfig.getFloat(arg0);
    }

    /**
     * Method getInt.
     * @param arg0
     * @return int
     */
    public static int getInt(String arg0) {
        return getInstance().globalConfig.getInt(arg0);
    }

    /**
     * Method getList.
     * @param arg0
     * @return List
     */
    public static List getList(String arg0) {
        return getInstance().globalConfig.getList(arg0);
    }

    /**
     * Method getLong.
     * @param arg0
     * @return long
     */
    public static long getLong(String arg0) {
        return getInstance().globalConfig.getLong(arg0);
    }

    /**
     * Method getProperty.
     * @param arg0
     * @return Object
     */
    public static Object getProperty(String arg0) {
        return getInstance().globalConfig.getProperty(arg0);
    }

    /**
     * Method getShort.
     * @param arg0
     * @return short
     */
    public static short getShort(String arg0) {
        return getInstance().globalConfig.getShort(arg0);
    }

    /**
     * Method getString.
     * @param arg0
     * @return String
     */
    public static String getString(String arg0) {
        return getInstance().globalConfig.getString(arg0);
    }

    /**
     * Method getStringArray.
     * @param arg0
     * @return String[]
     */
    public static String[] getStringArray(String arg0) {
        return getInstance().globalConfig.getStringArray(arg0);
    }
    
    /**
     * Returns a boolean stating that key is/is not in globals.
     * @param key The key to be tested.
     * @return True if the key is in globals.
     */
    public static boolean containsKey(String key){
    	return getInstance().globalConfig.containsKey(key);
    }
    
}
