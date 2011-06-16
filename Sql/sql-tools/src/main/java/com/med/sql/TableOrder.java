package com.med.sql;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

/**
 * This class generates lists of tables ordered by their foreign key dependencies.
 * 
 * @author Jane Eisenstein
 *
 */
public class TableOrder {
	
	public List<Table> getTableDropOrder(List<Schema> schemas, PrintStream out) {
		List<Table> tables = getTableLoadOrder(schemas, out);
		List<Table> tableDropOrder = null;
		if (!tables.isEmpty()) { 
			// flip table order by pushing contents onto a queue
			Table[] queue = new Table[tables.size()];
			int i = queue.length - 1;
			for (Table table : tables) {
				queue[i--] = table;
			}	
			// pop tables off queue into new list
			tableDropOrder = new ArrayList<Table>(tables.size());
			for (Table table : queue) {
				tableDropOrder.add(table);
			}
		}
		return tableDropOrder;
	}
	
	public List<Table> getValuedTablesInOrder(DataSource ds, List<Table> tables, PrintStream out) {
		
		List<Table> valuedTables = new ArrayList<Table>();
		
		Connection cn = null;
		try {
			cn = ds.getConnection();
			
			for (Table table : tables) {
				
				String tableName = table.toString();
				
			    Statement stmt = null;
			    ResultSet rs = null;
			    int rowCount = -1;
			    
			    try {
			      stmt = cn.createStatement();
			      rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
			      
			      // get the number of rows from the result set
			      rs.next();
			      rowCount = rs.getInt(1);
			    } catch ( Exception e ){
			    	e.printStackTrace();
			    } finally {
			      rs.close();
			      stmt.close();
			    }	
			    
			    if (rowCount > 0) {
			    	valuedTables.add(table);
			    	if (out != null) out.println("Added valued table in load order: "+tableName);
			    }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				cn.close();
			} catch (SQLException e) {
			}
			cn = null;
		}		
		
		return valuedTables;
	}

	
	public List<Table> getTableLoadOrder(List<Schema> schemas, PrintStream out) {

		final List<Table> tables = 
			new SchemaCrawlerUtils().getTableList(schemas);
		int tableCount = tables.size();

		List<Table> tableLoadOrder = new ArrayList<Table>(tableCount);
		Set<Table> listedTables = new TreeSet<Table>();
		Set<Table> fkTables = new TreeSet<Table>();
		
		// add all tables with foreign keys to fkTables
		// add all other tables (those without foreign keys) to listedTables and tableLoadOrder

		for (Table table : tables) {
			
			final ForeignKey[] foreignKeys = table.getForeignKeys();
			for (int j = 0; j < foreignKeys.length; j++) {
				final ForeignKey fk = foreignKeys[j];
				ForeignKeyColumnMap[] cms = fk.getColumnPairs();

				for (int k = 0; k < cms.length; k++) {
					ForeignKeyColumnMap cm = cms[k];
					if (table.equals(cm.getForeignKeyColumn().getParent())) {
						fkTables.add(table);
						//if (out != null) out.println("\tTable "+table+" references another table.");
						break;
					}
				}
			}
			
			if (!fkTables.contains(table)) {
				// table has no foreign keys
				tableLoadOrder.add(table);
				listedTables.add(table);
				if (out != null) out.println("Added to list table with no fk: "+table);
			}
		}
		
		if (out != null) out.println("Number of tables with no fk = "+listedTables.size());
		if (out != null) out.println("Number of tables with fk    = "+fkTables.size());
		if (out != null) out.println("Total number of tables      = "+tableCount);
		
		int iteration = 0;
		while (listedTables.size() < tableCount) {
			iteration++;
			boolean addedTable = false;
			
			for (Table fkTable : fkTables) {
				
				if (!listedTables.contains(fkTable)) {
				
					// if all tables referenced by this table's foreign keys
					// have already been listed, add this table to the list as well
					
					boolean referencesUnlistedTable = false;
	
					final ForeignKey[] foreignKeys = fkTable.getForeignKeys();
					for (int j = 0; j < foreignKeys.length; j++) {
						final ForeignKey fk = foreignKeys[j];
						ForeignKeyColumnMap[] cms = fk.getColumnPairs();
	
						for (int k = 0; k < cms.length; k++) {
							ForeignKeyColumnMap cm = cms[k];
							if (fkTable.equals(cm.getForeignKeyColumn().getParent())) {
								Table pkTable = (Table) cm.getPrimaryKeyColumn().getParent();
								if (!fkTable.equals(pkTable) && !listedTables.contains(pkTable) ) {
									referencesUnlistedTable = true;
									//if (out != null) out.println("\tTable "+table+" references an unlisted table.");
									break;
								}
							}
						}
					}
					
					if (!referencesUnlistedTable) {
						// table has no foreign keys to unlisted tables
						tableLoadOrder.add(fkTable);
						listedTables.add(fkTable);
						addedTable = true;
						if (out != null) out.println("Added to list table with fk: "+fkTable);
					}
				}
			}
			
			if (out != null) out.println("iteration: "+iteration);
			if (!addedTable)
				break;	// there must be fk reference cycle in remaining tables
		}

		
		if (out != null) out.println("Processed a total of " + tableCount + " tables.");
		if (out != null) out.println("Returned list contains " + tableLoadOrder.size() + " tables.");

		if (tableLoadOrder.size() < tableCount) {
			
			for (Table fkTable : fkTables) {
				
				if (!listedTables.contains(fkTable)) {
					tableLoadOrder.add(fkTable);
					listedTables.add(fkTable);
					if (out != null) out.println("Added to list table with unresolved fk: "+fkTable);
				}
			}
		}
		
		if (out != null) out.println("Processed a total of " + tableCount + " tables.");
		if (out != null) out.println("Returned list contains " + tableLoadOrder.size() + " tables.");

		return tableLoadOrder;
	}

}
