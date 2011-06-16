package com.joe.utilities.core.common.globals;

//import java.io.FileInputStream;
//import java.lang.reflect.Field;
//import java.util.Enumeration;
//import java.util.Properties;

/**
 * Class to house static application server configuration settings and globally useful map properties.
 * Creation date: (12/8/99 5:45:28 PM)
 * @author: Sumitro
 */

public final class Globals
{
	public static boolean PRODUCTION_ENVIRONMENT = false;
	
	
	/////////////////////////////////////////////////////////////////////
	//Loading the properties file
	/////////////////////////////////////////////////////////////////////
	//private static Properties m_properties;

	static {
		Globals.PRODUCTION_ENVIRONMENT = com.joe.utilities.core.configuration.Globals.getString("PRODUCTION_ENVIRONMENT") != null && 
									      "true".equalsIgnoreCase( com.joe.utilities.core.configuration.Globals.getString("PRODUCTION_ENVIRONMENT"));
	}
	/*  Commented out because values are coming 
	static {
		m_properties = null;

		try
		{
			String errorFile = System.getProperties().getProperty("com.med.common.globals");
			if (errorFile == null)
			{
				errorFile = System.getProperties().getProperty("server.root") + System.getProperties().getProperty("file.separator") + "globals.properties";
			}

			if (errorFile != null)
			{
				System.out.println("Loading Global variables from file:" + errorFile);
				m_properties = new Properties();
				m_properties.load(new FileInputStream(errorFile));
			}
		}
		catch (Exception e)
		{
			m_properties = null;
			System.out.println("Error loading global variables file.");
			// e.printStackTrace();
		}

		if (m_properties != null)
		{
			Enumeration en = m_properties.propertyNames();

			String fieldName = "";
			String value;

			try
			{
				Class c =null;
				try{
				c = Class.forName("com.med.globals.Globals");
				}
				catch(ClassNotFoundException cnfe)
				{
					try{
					c = Class.forName("com.med.common.globals.Globals");
					} catch(ClassNotFoundException cnfe_aae)
					{
						System.out.println("Error loading global variables file: Bad name used to reference Globals class in Java.");
					}
					
				}
				while (en.hasMoreElements())
				{
					try
					{
						fieldName = (String) en.nextElement();
						System.out.print("Globals: field=" + fieldName);
						value = (m_properties.getProperty(fieldName, "")).trim();

						Field field = c.getField(fieldName);

						if (field.getType().toString().equals("boolean"))
							field.setBoolean(null, "true".equalsIgnoreCase(value));
						else if (field.getType().toString().equals("int"))
							field.setInt(null, Integer.parseInt(value));
						else
							field.set(null, value);

						System.out.println(" set to value=" + value);
					}
					catch (NoSuchFieldException e)
					{
						System.out.println(" not defined in class com.med.globals.Globals");
					}
					catch (Exception e)
					{
						System.out.println(" could not be set because: " + e.getMessage());
					}
				}

			}
			catch (Exception e)
			{
				System.out.println("General error loading globals class");
				e.printStackTrace();
			}

		}
	}

	*/
    /////////////////////////////////////////////////////////////////////
	//Variables defined after loading the properties file
	/////////////////////////////////////////////////////////////////////
	
	/** Identifies any problems with initialization of the SOA framework */
	//public static String SOA_INIT_ERROR = null;


	//public final static String LINE_SEPARATOR = System.getProperties().getProperty("line.separator");
	//public final static String CRLF = "\r\n";

}
