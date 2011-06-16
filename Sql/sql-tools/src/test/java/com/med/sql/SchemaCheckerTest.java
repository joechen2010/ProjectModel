package com.med.sql;

import java.io.PrintStream;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SchemaCheckerTest extends DataSourceTest {

	boolean checkSchemas = "true".equalsIgnoreCase(System.getProperty("checkSchemas"));
	
	public SchemaCheckerTest() {
	}

	@Test
    public void testCheckSchemas()
    {
        try {			
			if (checkSchemas && connected) {
	    		String nameSpace = getNameSpace();
	    		PrintStream out = System.out;
	        	boolean ok = SchemaChecker.checkSchemas(ds, nameSpace, dbms, out);
	        	assertTrue(ok);
			}
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
