package com.med.sql;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * configuration to print example Oracle CreateEntity events for a newly installed database:
 *
 * -Ddbms=oracle
 * -Dcom.med.config.dataSource.alineoDataSource.jdbc.driver=oracle.jdbc.driver.OracleDriver
 * -Dcom.med.config.dataSource.alineoDataSource.jdbc.url=jdbc:oracle:thin:@localhost:1521:LOCALDEV
 * -Dcom.med.config.dataSource.alineoDataSource.jdbc.user=system
 * -Dcom.med.config.dataSource.alineoDataSource.jdbc.password=manager42
 * -DtargetSchemas=AUDITS,BATCH,CORREMGMTSTAGING,HXVIEW,HIST,PERFMON,REMOTE,CDCEVENT,EVENT
 * -DexcludeTargetSchemas=true
 * -DprintEvents=true
 * -Ddebug=false
 *
 * @author Jane Eisenstein
 *
 */

public class InstallCDCEventWriterTest extends SchemaCrawlerTest {

	boolean printEvents = "true".equalsIgnoreCase(System.getProperty("printEvents"));

	InstallCDCEventWriter eventWriter = null;
	PrintStream out = null;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		eventWriter = new InstallCDCEventWriter();
		out = System.out;
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();

		eventWriter = null;
		out = null;
	}

	@Test
    public void testPrintOracleCreateEntityEvents() {
    	if (printEvents && dbmsName.equals("oracle")) {
			try {
				if (connected)
					eventWriter.printCreateEntityEvents(schemas, DbmsInfo.Product.ORACLE, ds, out, debug);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }

	@Test
    public void testPrintMSSqlCreateEntityEvents() {
    	if (printEvents && dbmsName.equals("mssql")) {
			try {
				if (connected)
					eventWriter.printCreateEntityEvents(schemas, DbmsInfo.Product.MSSQL, ds, out, debug);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }

}
