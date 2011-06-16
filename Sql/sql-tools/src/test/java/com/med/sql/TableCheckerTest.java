package com.med.sql;

import java.io.PrintStream;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import schemacrawler.schema.Schema;


public class TableCheckerTest extends DataSourceTest {

	List<Schema> schemas = null;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		schemas = null;
	}
	@Test
    public void testTableChecker()
    {
        try {
        	
			if (connected) {
	    		String nameSpace = getNameSpace();
	    		PrintStream out = null;

	    		if (debug)
	    			out = System.out;

	    		TableChecker tc = new TableChecker();
	        	int scCount = tc.countTablesWithSchemaCrawler(ds, nameSpace, dbms, out);
	        	int jdbcCount = tc.countTablesWithJdbc(ds, nameSpace, dbms, out);

//	        	assertTrue(jdbcCount == scCount);

			}
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

}
