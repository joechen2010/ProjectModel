package com.med.sql;

import java.io.PrintStream;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

public class SchemaNames {
	
	public static void printReport(
			List<Schema> schemas, boolean printSchemaNames, boolean printTables, boolean printColumns, PrintStream out) throws Exception {

		// Get the schema definitions
		for (Schema schema : schemas) {
			String schemaName = schema.getName();
			if ( OracleUtils.isOracleSchema(schemaName))
				continue;
			if (printSchemaNames && !printTables && !printColumns) {
				out.println(schemaName);
			} else { // print table and/or column names
				final Table[] tables = schema.getTables();
				for (int i = 0; i < tables.length; i++) {
					final Table table = tables[i];
					String tableName = table.toString();
				
					if (printTables && !printColumns)
						out.println(tableName);
					
					if (printColumns) {
						final Column[] columns = table.getColumns();
						for (int j = 0; j < columns.length; j++) {
							final Column column = columns[j];
							if (printTables)
								out.println(tableName+"\t"+column.getName());
							else
								out.println(column.getFullName());
						}
					}
				}
			}
		}
	}
	
}
