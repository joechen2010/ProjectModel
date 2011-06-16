package com.med.sql;

import java.io.PrintStream;

import org.junit.Test;

/**
 * example configuration to print out only schema names:
 * 
 * -Ddbms=oracle	
 * -Dcom.med.config.dataSource.alineoDataSource.jdbc.driver=oracle.jdbc.driver.OracleDriver
 * -Dcom.med.config.dataSource.alineoDataSource.jdbc.url=jdbc:oracle:thin:@localhost:1521:LOCALDEV
 * -Dcom.med.config.dataSource.alineoDataSource.jdbc.user=system
 * -Dcom.med.config.dataSource.alineoDataSource.jdbc.password=manager42
 * -DtargetSchemas=AUDITS,HXVIEW,HIST,REMOTE,BATCH,CORREMGMTSTAGING
 * -DexcludeTargetSchemas=true
 * -DprintSchemas=true
 * 
 * @author Jane Eisenstein
 *
 */

public class SchemaNamesTest  extends SchemaCrawlerTest {

	boolean printSchemas = "true".equalsIgnoreCase(System.getProperty("printSchemas"));
	boolean printTables = "true".equalsIgnoreCase(System.getProperty("printTables"));
	boolean printColumns = "true".equalsIgnoreCase(System.getProperty("printColumns"));

	@Test
    public void testPrintTableNames() {
		if ((printSchemas || printTables || printColumns) && connected) {
			try {

				PrintStream out = System.out;
				
				SchemaNames.printReport(schemas, printSchemas, printTables, printColumns, out);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
