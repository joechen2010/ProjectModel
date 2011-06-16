package com.joe.utilities.core.util;

import com.joe.utilities.core.configuration.Globals;



/**
 * Contains list of database meta data checks used by the database.
 */
public class DbSupportUtil {

	/**
	 * Returns flag indicating that the SQL 2003 Nulls First/Last feature in 
	 * the order by is supported.
	 * @return True if the  the SQL 2003 Nulls First/Last feature in 
	 * the order by is supported.
	 */
	public static boolean isNullsFirstLastSupported(){
		String hibernateDialect = Globals.getString("hibernate.dialect");
		if ( hibernateDialect == null ){
			return true;
		}
		
		if ( ( hibernateDialect.indexOf("Cache")  >= 0 ||
			   hibernateDialect.indexOf("cache")  >= 0 ||
			   hibernateDialect.indexOf("SQLServer")  >= 0)

		){
			return false;
		} else {
			return true;
		}
	}
}
