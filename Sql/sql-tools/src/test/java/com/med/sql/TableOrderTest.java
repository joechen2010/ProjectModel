package com.med.sql;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import schemacrawler.schema.Table;

public class TableOrderTest extends SchemaCrawlerTest {

	String basedir = System.getProperty("basedir");
	
	PrintStream out = null;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		File file = new File(basedir+"/tableLoadOrder.txt");
		if (file.exists())
			file.delete();
		out = new PrintStream(file);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		
		out.flush();
		out.close();
	}
	
	@Test
    public void testGetTableLoadOrder() {
    	if (connected) {
			try {
				TableOrder tlo = new TableOrder();
				
				List<Table> tableList = tlo.getTableLoadOrder(schemas, out);
				assertNotNull(tableList);
				assertFalse(tableList.isEmpty());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
	}
	
	@Test
    public void testGetTableDropOrder() {
    	if (connected) {
			try {
				TableOrder tlo = new TableOrder();
				
				List<Table> tableList = tlo.getTableDropOrder(schemas, out);
				assertNotNull(tableList);
				assertFalse(tableList.isEmpty());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
	}
    
	@Test
	public void testValuedTablesInOrder() {
		if (connected) {
			try {
				TableOrder tlo = new TableOrder();

				// out = System.out;
				List<Table> orderedTables = 
					tlo.getTableLoadOrder(schemas, out);
				List<Table> valuedTables = 
					tlo.getValuedTablesInOrder(ds, orderedTables, out);
				assertNotNull(valuedTables);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
