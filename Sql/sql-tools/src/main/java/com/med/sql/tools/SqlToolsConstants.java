package com.med.sql.tools;

public interface SqlToolsConstants {
	
	/** 
	 * System property defining the file path to the properties file.
	 */
	public static final String properties_key = "com.med.sql-tools.properties";
	
	/**
	 * If file path to properties file is not defined, will try reading default properties path.
	 */
	public static final String defaultPropertiesPath = "sql-tools.properties";
	
	/**
	 * Property defining JDBC connection driver class
	 */
	public static final String driver_key = "com.med.sql-tools.jdbc.driver";

	/**
	 * Property defining JDBC connection URL
	 */
	public static final String url_key = "com.med.sql-tools.jdbc.url";
	
	/**
	 * Property defining JDBC connection user id
	 */
	public static final String user_key = "com.med.sql-tools.jdbc.user";

	/**
	 * Property defining JDBC connection user password
	 */
	public static final String password_key = "com.med.sql-tools.jdbc.password";

	/**
	 * Property defining comma separated list of schema names to be processed.
	 * If not defined, all schemas are processed
	 */
	public static final String targetSchemas_key = "com.med.sql-tools.targetSchemas";
	
	/**
	 * Property defining whether target schemas should be included in or excluded from processing.
	 * By default, target schemas are included.
	 */	
	public static final String excludeTargetSchemas_key = "com.med.sql-tools.excludeTargetSchemas";

}
