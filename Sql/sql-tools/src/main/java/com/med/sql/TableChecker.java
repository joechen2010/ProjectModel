package com.med.sql;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
//import java.util.List;

import javax.sql.DataSource;

//import schemacrawler.schema.Schema;
//import schemacrawler.schema.Table;

public class TableChecker {

	public int countTablesWithSchemaCrawler(DataSource dataSource, String nameSpace, 
			DbmsInfo.Product dbms, PrintStream out) {
		
// TODO refactor to work with SchemaCrawler 8.4
//		
//		SchemaCrawlerUtils scu = new SchemaCrawlerUtils();
//		List<Schema> schemas = 
//			scu.getSchemas(dataSource, dbms, new HashSet<String>(), 
//				false, false, false, null);
//		List<Table> tableList = scu.getTableList(schemas);
//
//		int tableCount = tableList.size();
//		if (out != null)
//			out.println("countTablesWithSchemaCrawler -> "+tableCount);
//		return tableCount;
		
		return 0;
	}
	
	public int countTablesWithJdbc(DataSource dataSource, String nameSpace, 
			DbmsInfo.Product dbms, PrintStream out) {
		int jdbcCount = 0;
		String[] tableTypes = { "TABLE" };

		Connection cn = null;
		try {
			cn = dataSource.getConnection();
			DatabaseMetaData dbmd = cn.getMetaData();
			jdbcCount = checkTablesWithJdbc(cn, dbms, dbmd, tableTypes, nameSpace, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				cn.close();
			} catch (SQLException e) {
			}
			cn = null;
		}
		
		if (out != null)
			out.println("countTablesWithJdbc -> "+jdbcCount);
		return jdbcCount;
	}
	
	private int checkTablesWithJdbc(Connection cn, DbmsInfo.Product dbms, DatabaseMetaData dbmd,
			String[] tableTypes, String nameSpace, PrintStream out)
	throws SQLException 
	{
		int tableCount = 0;
		
		HashSet<String> uniqueNames = new HashSet<String>();
		ResultSet rsSchemas = dbmd.getSchemas();
		while (rsSchemas.next()) {
			String schemaName = rsSchemas.getString(1);

			if (dbms.equals(DbmsInfo.Product.ORACLE) && OracleUtils.isOracleSchema(schemaName))
				continue;
			
//			if (dbms.equals(DbmsInfo.Product.CACHE) && CacheUtils.isCacheSchema(schemaName))
//				continue;
			
			ResultSet rsTable = 
				dbmd.getTables(nameSpace, schemaName, "%", tableTypes);

			// check table names for the schema
			while (rsTable.next()) {
				
				String tableName = rsTable.getString("TABLE_NAME");
				//out.println("Table id: "+tableName+" Table name: "+schemaName + "." + tableName);
				
				
				tableCount++;
			}
		}
		return tableCount;
	}
}
