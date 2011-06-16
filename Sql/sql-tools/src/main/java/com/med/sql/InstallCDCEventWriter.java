package com.med.sql;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

import com.medecision.eda.event100.EventDocument;

/**
 * Prototypical CDC event generater for newly installed Alineo databases.
 * 
 * @author Jane Eisenstein
 */
public class InstallCDCEventWriter {
	
	/**
	 * NOTE: printEvents should be only be run against a database containing initial, model data.
	 *
	 * @param schemas
	 * @param targetDbms
	 * @param out
	 * @param ds
	 * @param debug
	 * @throws Exception
	 */
	public void printCreateEntityEvents(
			List<Schema> schemas, DbmsInfo.Product targetDbms, DataSource ds, PrintStream out, boolean debug) throws Exception {
			
		TableOrder to = new TableOrder();
		List<Table> orderedTables = to.getTableLoadOrder(schemas, null);
		List<Table> valuedTables = to.getValuedTablesInOrder(ds, orderedTables, null);

		Connection cn = null;
		try {
			cn = ds.getConnection();
			
			Timestamp txnTimestamp = null; 

			for (Table table : valuedTables) {
				
				final String tableName = table.toString();

				final String entityType = CDCUtils.getEntityType(table);
				
				if (CDCUtils.isNonauditedEntity(entityType))
					continue;
				
				int primaryKeyColumnCount = 0;
				Column primaryKeyColumn = null;
				
				// collect all columns 
				List<Column> dataColumnList = new ArrayList<Column>();
				final Column[] columns = table.getColumns();
				for (int j = 0; j < columns.length; j++) {
					final Column column = columns[j];
					if (column.isPartOfPrimaryKey()) {
						primaryKeyColumn = column;
						primaryKeyColumnCount++;
					} else { // non-PK column
						dataColumnList.add(column);
					}
				}

				if (!dataColumnList.isEmpty() && 1 == primaryKeyColumnCount) {
					
					txnTimestamp = null;

					String pkColumnId = primaryKeyColumn.getName();
					if (debug && out != null) out.println("pk column: "+pkColumnId);

					Column[] dataColumns = new Column[dataColumnList.size()];
					for (int i = 0; i < dataColumns.length; i++)
						dataColumns[i] = dataColumnList.get(i);
					String columnCommaIdList = SqlUtils.getCommaIdList(dataColumns);
					if (debug && out != null) out.println(columnCommaIdList);
					
				    Statement stmt = null;
				    ResultSet rs = null;
				    try {
						stmt = cn.createStatement();

						String sql = "SELECT " + columnCommaIdList +  ", "  + pkColumnId +" FROM " + tableName;
						if (debug && out != null) out.println(sql);
						
						rs = stmt.executeQuery(sql);
						while (rs.next()) {
							int colCount = dataColumns.length;
							String[] values = new String[colCount];
							String pkValue = null;
							
							int i = 0;
							for (	; i < colCount; i++) {
								Column column = dataColumns[i];

								// TODO: verify this logic is right
								String value = rs.getString(i + 1);
								if (value != null) {
									value = CDCUtils.getColumnValueString(column, value);
								}

								values[i] = value;
							}

							String value = rs.getString(i + 1);
							if (value != null) {
								pkValue = CDCUtils.getColumnValueString(primaryKeyColumn, value);
							}
							
							EventDocument eventDoc = 
								CDCUtils.createEventDoc(entityType, dataColumns, colCount, values, pkValue);
						
							if (out != null) {
								String xml = CDCUtils.getEventXML(eventDoc);
								out.println(xml);
							}
							
						} // while

				    } catch (Exception e) {
						e.printStackTrace();
					} finally {
						try { rs.close(); } catch (SQLException e) {}
						rs = null;
						try { stmt.close(); } catch (SQLException e) {}
						stmt = null;
				    }
				}
			} // for
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				cn.close();
			} catch (SQLException e) { }
			cn = null;
		}
	}




	
}
