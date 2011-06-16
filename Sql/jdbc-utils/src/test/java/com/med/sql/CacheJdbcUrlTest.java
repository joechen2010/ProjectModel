package com.med.sql;

import com.med.sql.CacheJdbcUrl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CacheJdbcUrlTest extends TestCase {
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CacheJdbcUrlTest( String testName )
    {
        super( testName );
    }
    
    public void testSetUrl()
    {
    	boolean success = true;
    	
    	CacheJdbcUrl url = new CacheJdbcUrl();
    	try {
    		url.setUrl("jdbc:Cache://localhost:56773/REMOTE");
    	} catch (IllegalArgumentException e) {
    		success = false;
    	}
    	assertTrue(success);
    	
    	assertTrue("localhost".equals(url.getHost()));
    	assertTrue(new Long("56773").equals(url.getPort()));
    	assertTrue("REMOTE".equals(url.getDatabase()));
    }
    
    public void testSetProperties()
    {
    	boolean success = true;
    	
    	CacheJdbcUrl url = new CacheJdbcUrl();
    	try {
    		url.setProperties("localhost", new Long("56773"), "REMOTE");
    	} catch (IllegalArgumentException e) {
    		success = false;
    	}
    	assertTrue(success);
    	
    	assertTrue("jdbc:Cache://localhost:56773/REMOTE".equals(url.getUrl()));
    }
  
}
