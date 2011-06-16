package com.med.sql;

import java.io.PrintStream;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

import schemacrawler.schema.Schema;

public class SchemaCrawlerUtilsTest extends SchemaCrawlerTest {

	@Test
	public void testGetSchemas() {
        try {
			if (connected && targetSchemaSet != null) {

	    		PrintStream out = null;
	    		if (debug)
	    			out = System.out;

	    		SchemaCrawlerUtils scu = new SchemaCrawlerUtils();
	    		long start = System.currentTimeMillis();
	    		List<Schema> schemas = 
	    			scu.getSchemas(jdbcProps, dbms, ds, targetSchemaSet, excludeTargetSchemas, true, false, out);
	    		long finish = System.currentTimeMillis();
	    		assertNotNull(schemas);
	    		System.out.println("completed testGetSchemas in "+(finish-start)+" ms");
			}
        } catch (Exception e) {
        	e.printStackTrace();
        }   	
    }
    
//    public void testGetAllSchemas() {
//        try {
//
//			if (connected) {
//
//	    		PrintStream out = null;
//	    		if (debug)
//	    			out = System.out;
//
//	    		SchemaCrawlerUtils scu = new SchemaCrawlerUtils();
//	    		long start = System.currentTimeMillis();
//	    		List<Schema> schemas = scu.getSchemas(ds, nameSpace,  null, false, out);
//	    		long finish = System.currentTimeMillis();
//	    		assertNotNull(schemas);
//	    		System.out.println("completed testGetAllSchemas in "+(finish-start)+" ms");
//			}
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }   	
//    }    

}
