package com.med.sql.tools;

import org.junit.Test;

import com.med.sql.tools.SqlToolsProperties;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
/**
 * @author Jane Eisenstein
 *
 */
public class SqlToolsPropertiesTest {

	@Test
    public void testSqlToolsProperties() {
    	
    	if (null != System.getProperties().getProperty(SqlToolsConstants.properties_key)) {
	    	SqlToolsProperties props = new SqlToolsProperties();
	    	
	    	assertNotNull(props.getString(SqlToolsProperties.driver_key));
	    	assertNotNull(props.getString(SqlToolsProperties.url_key));
	    	assertNotNull(props.getString(SqlToolsProperties.user_key));
	    	assertNotNull(props.getString(SqlToolsProperties.password_key));
	   	
	    	String[] schemas = props.getStringArray(SqlToolsProperties.targetSchemas_key);
	    	assertNotNull(schemas);
	    	assertTrue(schemas.length == 1);
	    	
	    	boolean exclude = props.getBoolean(SqlToolsProperties.excludeTargetSchemas_key);
	    	assertFalse(exclude);
    	}
	}
	
}
