package com.med.sql;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class SqlUtilsTest {
	
	boolean debug = "true".equalsIgnoreCase(System.getProperty("debug"));

//    public void testGetUniquePrimaryKeyName() {
//    	String tableId = "ProcedureCrosswalk";
//    	HashSet<String> names  = new HashSet<String>(); 
//     	try {
//	    	String id1 = SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	if (debug) System.out.println("id1="+id1);
//	    	String id2 = SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	if (debug) System.out.println("id2="+id2);
//	    	String id3 = SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	if (debug) System.out.println("id3="+id3);
//	    	String id4 = SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	if (debug) System.out.println("id4="+id4);
//	    	String id5 = SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	if (debug) System.out.println("id5="+id5);
//	    	String id6 = SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	if (debug) System.out.println("id6="+id6);
//	    	String id7 = SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	if (debug) System.out.println("id7="+id7);
//	    	String id8 = SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	if (debug) System.out.println("id8="+id8);
//	    	String id9 = SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	if (debug) System.out.println("id9="+id9);
//	    	String id10 = SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	if (debug) System.out.println("id10="+id10);
//
//	    	for (int i = 0; i < 90; i++)
//	    		SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	String id100 = SqlUtils.getUniquePrimaryKeyId(tableId, names);
//	    	if (debug) System.out.println("id100="+id100);
//	    	fail("expected 101rst invocation to fail");
//	    	
//    	} catch (SqlToolsException e) {
//    		//e.printStackTrace();
//    	}
//    }
//    
//    public void testGetUniqueAlternateKeyName() {
//    	String tableId = "ProcedureCrosswalk";
//    	HashSet<String> names  = new HashSet<String>(); 
//     	try {
//	    	String id1 = SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	if (debug) System.out.println("id1="+id1);
//	    	String id2 = SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	if (debug) System.out.println("id2="+id2);
//	    	String id3 = SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	if (debug) System.out.println("id3="+id3);
//	    	String id4 = SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	if (debug) System.out.println("id4="+id4);
//	    	String id5 = SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	if (debug) System.out.println("id5="+id5);
//	    	String id6 = SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	if (debug) System.out.println("id6="+id6);
//	    	String id7 = SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	if (debug) System.out.println("id7="+id7);
//	    	String id8 = SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	if (debug) System.out.println("id8="+id8);
//	    	String id9 = SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	if (debug) System.out.println("id9="+id9);
//	    	String id10 = SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	if (debug) System.out.println("id10="+id10);
//
//	    	for (int i = 0; i < 90; i++)
//	    		SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	String id100 = SqlUtils.getUniqueAlternateKeyId(tableId, names);
//	    	if (debug) System.out.println("id100="+id100);
//	    	fail("expected 101rst invocation to fail");
//	    	
//    	} catch (SqlToolsException e) {
//    		//e.printStackTrace();
//    	}
//    }
//    
//    public void testGetUniqueSequenceName() {
//    	String tableId = "ProcedureCrosswalk";
//    	HashSet<String> names  = new HashSet<String>(); 
//     	try {
//	    	String id1 = SqlUtils.getUniqueSequenceId(tableId, names);
//	    	if (debug) System.out.println("id1="+id1);
//	    	String id2 = SqlUtils.getUniqueSequenceId(tableId, names);
//	    	if (debug) System.out.println("id2="+id2);
//	    	String id3 = SqlUtils.getUniqueSequenceId(tableId, names);
//	    	if (debug) System.out.println("id3="+id3);
//	    	String id4 = SqlUtils.getUniqueSequenceId(tableId, names);
//	    	if (debug) System.out.println("id4="+id4);
//	    	String id5 = SqlUtils.getUniqueSequenceId(tableId, names);
//	    	if (debug) System.out.println("id5="+id5);
//	    	String id6 = SqlUtils.getUniqueSequenceId(tableId, names);
//	    	if (debug) System.out.println("id6="+id6);
//	    	String id7 = SqlUtils.getUniqueSequenceId(tableId, names);
//	    	if (debug) System.out.println("id7="+id7);
//	    	String id8 = SqlUtils.getUniqueSequenceId(tableId, names);
//	    	if (debug) System.out.println("id8="+id8);
//	    	String id9 = SqlUtils.getUniqueSequenceId(tableId, names);
//	    	if (debug) System.out.println("id9="+id9);
//	    	String id10 = SqlUtils.getUniqueSequenceId(tableId, names);
//	    	if (debug) System.out.println("id10="+id10);
//
//	    	for (int i = 0; i < 90; i++)
//	    		SqlUtils.getUniqueSequenceId(tableId, names);
//	    	String id100 = SqlUtils.getUniqueSequenceId(tableId, names);
//	    	if (debug) System.out.println("id100="+id100);
//	    	fail("expected 101rst invocation to fail");
//	    	
//    	} catch (SqlToolsException e) {
//    		if (debug) e.printStackTrace();
//    	}
//    }
//    
//    public void testGetUniqueSequenceTriggerId() {
//    	String tableId = "ProcedureCrosswalk";
//    	HashSet<String> names  = new HashSet<String>(); 
//    	try {
//	    	String id1 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id1="+id1);
//	    	String id2 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id2="+id2);
//	    	String id3 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id3="+id3);
//	    	String id4 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id4="+id4);
//	    	String id5 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id5="+id5);
//	    	String id6 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id6="+id6);
//	    	String id7 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id7="+id7);
//	    	String id8 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id8="+id8);
//	    	String id9 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id9="+id9);
//	    	String id10 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id10="+id10);
//
//	    	for (int i = 0; i < 90; i++)
//	    		SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	String id100 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id100="+id100);
//	    	fail("expected 101rst invocation to fail");
//	    	
//    	} catch (SqlToolsException e) {
//    		if (debug) e.printStackTrace();
//    	}
//    }
//    
//    public void testGetUniqueSequenceTriggerIdToo() {
//    	String tableId = "ProcedureCrosswalkMonitorPosition";
//    	HashSet<String> names  = new HashSet<String>(); 
//    	try {
//	    	String id1 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id1="+id1);
//	    	String id2 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id2="+id2);
//	    	String id3 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id3="+id3);
//	    	String id4 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id4="+id4);
//	    	String id5 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id5="+id5);
//	    	String id6 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id6="+id6);
//	    	String id7 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id7="+id7);
//	    	String id8 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id8="+id8);
//	    	String id9 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id9="+id9);
//	    	String id10 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id10="+id10);
//
//	    	for (int i = 0; i < 90; i++)
//	    		SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	String id100 = SqlUtils.getUniqueSequenceTriggerId(tableId, names);
//	    	if (debug) System.out.println("id100="+id100);
//	    	fail("expected 101rst invocation to fail");
//	    	
//    	} catch (SqlToolsException e) {
//    		if (debug) e.printStackTrace();
//    	}
//    }
//    
//    public void testGetUniqueForeignKeyIdToo() {
//    	String tableId = "ProcedureCrosswalk";
//    	String pkTableId = "MonitorPosition";
//    	HashSet<String> names  = new HashSet<String>(); 
//    	try {
//	    	String id1 = SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	if (debug) System.out.println("id1="+id1);
//	    	String id2 = SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	if (debug) System.out.println("id2="+id2);
//	    	String id3 = SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	if (debug) System.out.println("id3="+id3);
//	    	String id4 = SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	if (debug) System.out.println("id4="+id4);
//	    	String id5 = SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	if (debug) System.out.println("id5="+id5);
//	    	String id6 = SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	if (debug) System.out.println("id6="+id6);
//	    	String id7 = SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	if (debug) System.out.println("id7="+id7);
//	    	String id8 = SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	if (debug) System.out.println("id8="+id8);
//	    	String id9 = SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	if (debug) System.out.println("id9="+id9);
//	    	String id10 = SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	if (debug) System.out.println("id10="+id10);
//	    	
//	    	for (int i = 0; i < 90; i++)
//	    		SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	String id100 = SqlUtils.getUniqueForeignKeyId(tableId, pkTableId, names);
//	    	if (debug) System.out.println("id100="+id100);
//	    	fail("expected 101rst invocation to fail");
//	    	
//    	} catch (SqlToolsException e) {
//    		if (debug) e.printStackTrace();
//    	}
//    }
    @Test
    public void testEscapeStringDelimiter() {
    	String value = "Korea, Democratic People's Republic of";
    	String newValue = SqlUtils.escapeStringLiteralDelimiter(value, "VARCHAR");
    	assertFalse(value.equals(newValue));
    	value = "Korea, Democratic People''s Republic of";
    	newValue = SqlUtils.escapeStringLiteralDelimiter(value, "VARCHAR");
    	assertTrue(value.equals(newValue));
    }
}
