package com.med.sql;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

/**
 * Uses sql-tools properties to retrieve schema information from SchemaCrawler.
 * 
 * @author Jane Eisenstein
 */
public class SchemaCrawlerUtils {
	
	/**
	 * Retrieves an alphabetical list of the schemas the user requested.
	 * @param jdbcProps
	 * @param dbms
	 * @param dataSource
	 * @param targetSchemas
	 * @param excludeTargetSchemas
	 * @param sortColumns
	 * @param debug TODO
	 * @param out
	 * @return List<Schema>
	 */
	public List<Schema> getSchemas(
			JdbcProperties jdbcProps, 
			DbmsInfo.Product dbms, 
			DataSource dataSource, 
			Set<String> targetSchemas,
			boolean excludeTargetSchemas, 
			boolean sortColumns, 
			boolean debug, 
			PrintStream out) {
		
		List<Schema> schemaList = new ArrayList<Schema>();

		try {
			
			String driver = jdbcProps.getDriverClassName();
			String url = jdbcProps.getJdbcUrl().getUrl();
			String user = jdbcProps.getUserId();
			String password = jdbcProps.getPassword();
			
			// Create database connection options
			final DatabaseConnectionOptions connectionOptions = 
				new DatabaseConnectionOptions(driver, url);
			connectionOptions.setUser(user);
			connectionOptions.setPassword(password);
			
			// Create the schema retrieval options
			final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
			// Set what details are required in the schema - this affects the
			// time taken to crawl the schema
			options.setSchemaInfoLevel(SchemaInfoLevel.standard());
			// don't retrieve procedure information
			options.setProcedureInclusionRule(InclusionRule.EXCLUDE_ALL);
			// don't retrieve view information
			options.setTableTypes("TABLE");
			
			// empty targetSchemas => target is all Alineo schemas
			// always exclude system schemas
	
			if (DbmsInfo.Product.ORACLE.equals(dbms)) { 
				
				String includedSchemasPattern = 
					OracleUtils.getSchemaNamesPattern(dataSource, targetSchemas , excludeTargetSchemas);
				
				options.setSchemaInclusionRule(
						new InclusionRule(includedSchemasPattern, InclusionRule.NONE));
				
			} else if (DbmsInfo.Product.MSSQL.equals(dbms)) {
				
				if (!excludeTargetSchemas) { // include only target schemas
	
					if (!targetSchemas.isEmpty()) {
						
						String includedSchemasPattern = 
							MSSqlUtils.getSchemaNamesPattern(targetSchemas);
						
						options.setSchemaInclusionRule(
								new InclusionRule(includedSchemasPattern, InclusionRule.NONE));
						
					} else { // include all non-system schemas
						
						String mssqlSchemaNames = MSSqlUtils.getMSSqlSchemaNamesPattern();						
						options.setSchemaInclusionRule(
								new InclusionRule("alineo_mssql_db.*", mssqlSchemaNames));
					}
									
				} else {  // exclude target schemas and system schemas
									
					if (!targetSchemas.isEmpty()) {
						
						String targetSchemasPattern = 
							MSSqlUtils.getSchemaNamesPattern(targetSchemas);
						String mssqlSchemaNames = 
							MSSqlUtils.getMSSqlSchemaNamesPattern();
						String excludedSchemasPattern = targetSchemasPattern+"|"+mssqlSchemaNames;
					
						options.setSchemaInclusionRule(
							new InclusionRule("alineo_mssql_db.*", excludedSchemasPattern));
						
					} else { // some whacky user wants to exclude all schemas
						options.setSchemaInclusionRule(InclusionRule.EXCLUDE_ALL);
					}
				}	
			}
			
			InclusionRule inclusionRule = options.getSchemaInclusionRule();
			if (debug) {
				if (null == out)
					out = System.out;
				out.println(inclusionRule.toString());
			}
			
			// Sorting options
			options.setAlphabeticalSortForTableColumns(sortColumns);
	
			// Get the schema definition
			final Database database = 
				SchemaCrawlerUtility.getDatabase(
						connectionOptions.createConnection(), options);
	
			final Schema[] schemas = database.getSchemas();
			schemaList = Arrays.asList(schemas);
			
		} catch (Exception e) {
			e.printStackTrace();
			schemaList = null;
		}
		
		return schemaList;	
	}
	
	/**
	 * Returns a list of all the tables in the schemas
	 * @param schemas
	 * @return List<Table>
	 */
	public List<Table> getTableList(List<Schema> schemas) {
		ArrayList<Table> tableList = new ArrayList<Table>();
		
		if (schemas != null) {
			for (Schema schema : schemas) {
				final Table[] tables = schema.getTables();
				int tableCount = tables.length;
				for (int i = 0; i < tableCount; i++) {
					final Table table = tables[i];
					tableList.add(table);
				}
			}
		}
		
		return tableList;
	}
	
}
