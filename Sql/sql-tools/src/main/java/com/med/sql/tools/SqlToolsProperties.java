package com.med.sql.tools;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;


/**
 * Supplies properties for sql-tools applications.
 * 
 * @author Jane Eisenstein
 */
public class SqlToolsProperties implements SqlToolsConstants {

    private CompositeConfiguration properties = null;

    public SqlToolsProperties() {
        // Create composite configuration
        this.properties = new CompositeConfiguration();
         
        // Load system properties
    	Configuration system = new SystemConfiguration();
        properties.addConfiguration(system);
        
        // Load customer's properties
        PropertiesConfiguration sqlToolsProperties = loadSqlToolsProperties();
        if (sqlToolsProperties != null)
        	properties.addConfiguration(sqlToolsProperties);
     }

    private PropertiesConfiguration loadSqlToolsProperties() {

		PropertiesConfiguration properties = null;
		String filePath = locatePropertiesFile();

		if (filePath == null) {
			throw new RuntimeException("Properties file path not specified.");
		} else {
			try {
				properties = new PropertiesConfiguration(filePath);
			} catch (ConfigurationException e) {
				throw new RuntimeException(
						"Could not open properties file at '" + filePath + "'.", e);
			}
		}

		return properties;
	}
	
    private String locatePropertiesFile(){
		String fileName = null;
		
		try {
			fileName = System.getProperties().getProperty(properties_key);
			if (fileName == null) {

				if (System.getProperties().getProperty("user.dir") != null) {
					String rootPath =
						System.getProperties().getProperty("user.dir")
							+ System.getProperties().getProperty("file.separator");

					fileName = rootPath + defaultPropertiesPath;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error loading properties file.", e);
		}

		return fileName;
	}
    
    /**
     * Method getBoolean.
     * @param arg0
     * @return boolean
     */
    public  boolean getBoolean(String arg0) {
        return properties.getBoolean(arg0);
    }
    
    /**
     * Method getString.
     * @param arg0
     * @return String
     */
    public  String getString(String arg0) {
        return properties.getString(arg0);
    }

    /**
     * Method getStringArray.
     * @param arg0
     * @return String[]
     */
    public  String[] getStringArray(String arg0) {
        return properties.getStringArray(arg0);
    }
}
