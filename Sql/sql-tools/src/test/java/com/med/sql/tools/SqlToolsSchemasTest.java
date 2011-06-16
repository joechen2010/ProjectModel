package com.med.sql.tools;

import java.util.List;

import org.junit.Test;

import com.med.sql.tools.SqlToolsProperties;
import com.med.sql.tools.SqlToolsSchemas;
import com.med.sql.tools.SqlToolsConstants;
import schemacrawler.schema.Schema;
import static org.junit.Assert.assertTrue;

public class SqlToolsSchemasTest {
	
	@Test
    public void testSqlToolsSchemas() {
    	
    	if (null != System.getProperties().getProperty(SqlToolsConstants.properties_key)) {
	    	SqlToolsProperties props = new SqlToolsProperties();
	    	SqlToolsSchemas sts = new SqlToolsSchemas(props);
	    	List<Schema> schemas = sts.getSchemas(false);
	    	assertTrue(schemas.size() == 1);
    	}
    }

}
