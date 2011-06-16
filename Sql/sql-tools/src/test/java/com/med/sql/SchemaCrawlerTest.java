package com.med.sql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;

import schemacrawler.schema.Schema;

public abstract class SchemaCrawlerTest extends DataSourceTest {

	protected String targetSchemas = System.getProperty("targetSchemas");
	protected boolean excludeTargetSchemas = "true".equalsIgnoreCase(System.getProperty("excludeTargetSchemas"));

	protected Set<String> targetSchemaSet = null;
	protected List<Schema> schemas = null;

	public SchemaCrawlerTest() {
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		if (debug) {
			System.out.println("targetSchemas=" + targetSchemas);
			System.out.println("excludeTargetSchemas=" + excludeTargetSchemas);
		}
	
		if (targetSchemas != null) {
			targetSchemaSet = new HashSet<String>();
			String[] schemaIds = targetSchemas.split(",");
			for (String s : schemaIds) {
				targetSchemaSet.add(s);
			}
		}
		
		SchemaCrawlerUtils scu = new SchemaCrawlerUtils();
		schemas = 
			scu.getSchemas(jdbcProps, dbms, ds, targetSchemaSet, excludeTargetSchemas, true, false, null);
		if (debug) {
			System.out.println("schemas retrieved = "+schemas.size());
		}
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		targetSchemaSet = null;
		schemas = null;
	}
}