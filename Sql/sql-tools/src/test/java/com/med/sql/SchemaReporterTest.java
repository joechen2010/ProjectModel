package com.med.sql;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.assertFalse;

public class SchemaReporterTest extends SchemaCrawlerTest {
	
	boolean printReport = "true".equalsIgnoreCase(System.getProperty("printReport"));
	boolean printIndices = "true".equalsIgnoreCase(System.getProperty("printIndices"));
	
//    public void testPrintReport() {
//		if (printReport && connected) {
//			try {
//
//				PrintStream out = System.out;
//				SchemaReporter.printReport(schemas, null, true, printIndices, out);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
	@Test
    public void testPrintReportWithTriggers() {
		if (printReport && connected) {
			try {

				PrintStream out = System.out;
				
				Map<String, Set<TriggerInfo>> triggerMap = 
					TriggerInfoUtils.getTriggerInfo(ds, schemas, dbms);
				assertFalse(dbms.equals(DbmsInfo.Product.ORACLE) && triggerMap.isEmpty());
				
				SchemaReporter.printReport(schemas, triggerMap, true, printIndices, out);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
