package com.med.sql;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import schemacrawler.schema.*;

public final class SchemaReporter {

	public static void printReport(
			List<Schema> schemas, Map<String, Set<TriggerInfo>> triggerMap, boolean verbose, boolean printIndices, PrintStream out) throws Exception {

		// Get the schema definitions
		for (Schema schema : schemas) {

			final Table[] tables = schema.getTables();
			for (int i = 0; i < tables.length; i++) {
				final Table table = tables[i];
			
				out.print("\n"+table.toString());
				if (table instanceof View) {
					out.println(" (view)");
				} else {
					out.println();
				}
	
				int primaryKeyColumns = 0;
				final Column[] columns = table.getColumns();
				if (columns.length > 0)
					out.println(" Columns:");
				for (int j = 0; j < columns.length; j++) {
					final Column column = columns[j];
					
					String info = "";
					if (column.isPartOfPrimaryKey()) {
						info = "(PK) ";
						primaryKeyColumns++;
					}
					else if (column.isPartOfForeignKey())
						info = "(fk) ";
					else
						info = "     ";
					
	//				if (verbose && column.isPartOfUniqueIndex())
	//					info += "(*) ";
					
					info += column.toString();
					if (verbose)
						info +=  ", type=" + column.getType()
							+ ", nullable=" + column.isNullable()
							+ ", inUniqueIndex=" + column.isPartOfUniqueIndex()
							+ ", size=" + column.getSize()
							+ ", digits=" + column.getDecimalDigits()
							+ ", width=" + column.getWidth()
							+ ", default value=" + column.getDefaultValue(); 
					out.println("  "+info);
				}
				
				if (verbose) {
					final PrimaryKey primaryKey = table.getPrimaryKey();
					if (primaryKey != null) {
						String info = primaryKey.toString();
						out.println(" Primary key: " + info);
						if (primaryKeyColumns > 1) {
							out.println(" ### "+table+" has a "+primaryKeyColumns+"-column primary key");
						}
					} else {
						out.println(" *** "+table+" does not have a primary key");
					}
				}
	
				if (verbose) {
					final ForeignKey[] foreignKeys = table.getForeignKeys();
					if (foreignKeys.length > 0)
						out.println(" Foreign keys:");
					for (int j = 0; j < foreignKeys.length; j++) {
						final ForeignKey fk = foreignKeys[j];
						final String fkId = fk.getName();
						
						ForeignKeyColumnMap[] cms = fk.getColumnPairs();
						Column fkCol0 = cms[0].getForeignKeyColumn();
						String fkTableId = fkCol0.getParent().getName();
						if (table.getName().equals(fkTableId)) { // the FK is defined on this table
							String info = fk.toString();
							ForeignKeyDeferrability deferability = fk.getDeferrability();
							ForeignKeyUpdateRule deleteRule = fk.getDeleteRule();
							ForeignKeyUpdateRule updateRule = fk.getUpdateRule();
							
							info += ", deferability="+deferability;
							info += ", deleteRule="+deleteRule;
							info += ", UpdateRule="+updateRule;
							
							info += fkColumnsToString(cms);
							out.println("  fk: " + info);
						}
						// perhaps also show referencing fks?
					}
				}
				
				if (verbose) {
					final CheckConstraint[] ccs = table.getCheckConstraints();
					if (ccs.length > 0)
						out.println(" Constraints:");
					for (int j = 0; j < ccs.length; j++) {
						final CheckConstraint cc = ccs[j];
						String info = cc.toString();
						if (verbose)
							info += ", definition=" +cc.getDefinition();
						out.println("  constraint: " + info);
					}
				}
	
				
				if (printIndices) {
					final Index[] indices = table.getIndices();
					if (indices.length > 0)
						out.println(" Indices:");
					for (int j = 0; j < indices.length; j++) {
						final Index index = indices[j];
							String info = index.toString();
							if (verbose)
								info += 
									//", type=" +index.getType() +
									//", cardinality=" + index.getCardinality() +
									", unique=" +index.isUnique() +
									", columns=" + columnsToString(index.getColumns());
							out.println("  index: " + info);
					}
				}	

				if (triggerMap != null) {
					String tableName = table.toString();
					Set<TriggerInfo> tableTriggers = triggerMap.get(tableName);
					if (tableTriggers != null) {
						out.println(" Triggers:");
						for (TriggerInfo triggerInfo : tableTriggers) {
							String info = triggerInfo.toString();
							if (verbose)
								info += ", event=" +triggerInfo.getEvent()
									+ ", type=" + triggerInfo.getType();
							out.println("  trigger: " + info);
						}
					}
				}
				
				out.flush();
			}
		}
	}

	static String fkColumnsToString(ForeignKeyColumnMap[] cms) {
		String s = "[";
		for (ForeignKeyColumnMap cm : cms)
			s += " "+cm.getForeignKeyColumn()+"->"+cm.getPrimaryKeyColumn();
		s += " ]";
		return s;
	}
	
	static String columnsToString(Column[] cols) {
		String s = "[";
		for (Column c : cols)
			s += " "+c;
		s += " ]";
		return s;
	}
	
}
