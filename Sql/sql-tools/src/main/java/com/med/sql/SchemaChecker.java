package com.med.sql;

import java.io.PrintStream;
import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

public class SchemaChecker {
	
	public static boolean checkSchemas(DataSource dataSource, String nameSpace, DbmsInfo.Product dbms, PrintStream out) {
		boolean ok = true;
		String[] tableTypes = { "TABLE" };

		Connection cn = null;
		try {
			cn = dataSource.getConnection();
			DatabaseMetaData dbmd = cn.getMetaData();
			ok = checkSchemaNames(cn, dbmd, tableTypes, nameSpace, dbms, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				cn.close();
			} catch (SQLException e) {
			}
			cn = null;
		}
		return ok;
	}
	
	static boolean checkSchemaNames(Connection cn, DatabaseMetaData dbmd,
			String[] tableTypes, String nameSpace, DbmsInfo.Product dbms, PrintStream out)throws SQLException 
	{
		boolean debug = false;
		boolean okay = true;
		HashSet<String> uniqueNames = new HashSet<String>();
		ResultSet rsSchemas = dbmd.getSchemas();
		while (rsSchemas.next()) {
			String schemaName = rsSchemas.getString(1);
				
			if (dbms.equals(DbmsInfo.Product.ORACLE) && OracleUtils.isOracleSchema(schemaName))
				continue;

//			if (dbms.equals(DbmsInfo.Product.CACHE) && CacheUtils.isCacheSchema(schemaName))
//				continue;
			
			if (debug)
				System.out.println("Schema: "+schemaName);

			if (!OracleUtils.isValidLength(schemaName)) {
				out.println(
						"Schema id: " + schemaName + " is "+schemaName.length()+" bytes long.");
				okay = false;
			} 
			if (OracleUtils.isReservedWord(schemaName)) {
				out.println(
						"Schema id: " + schemaName + " is an Oracle reserved word.");
				okay = false;
			} 

			ResultSet rsTable = 
				dbmd.getTables(nameSpace, schemaName, "%", tableTypes);

			// check table names for the schema
			while (rsTable.next()) {
				String tableName = rsTable.getString("TABLE_NAME");
				
				//out.println("Table id: "+tableName+" Table id: "+schemaName + "." + tableName);
				
				if (!uniqueNames.contains(tableName))
					uniqueNames.add(tableName);
				else {
					out.println(
							"WARNING Table id: " + tableName + " is not unique across schemas.");
					//okay = false;
				} 
				if (!OracleUtils.isValidLength(tableName)) {
					out.println(
							"Table id: " + schemaName + "." + tableName + " is "+tableName.length()+" bytes long.");
					okay = false;
				} 
				if (OracleUtils.isReservedWord(tableName)) {
					out.println(
							"Table id: " + schemaName + "." + tableName +  " is an Oracle reserved word.");
					okay = false;
				}

				// check column names for the table
				ResultSet rsColumn = 
					dbmd.getColumns(schemaName, null, tableName, "%");

				while (rsColumn.next()) {
					String columnName = rsColumn.getString("COLUMN_NAME");
					
					if (!OracleUtils.isValidLength(columnName)) {
						out.println(
								"Column id: " + columnName + " ("+ schemaName + "." + tableName + "." + columnName + " is "+columnName.length()+" bytes long.");
						okay = false;
					} 
					if (OracleUtils.isReservedWord(columnName)) {
						out.println(
								"Column id: " + schemaName + "." + tableName + "." + columnName + " is an Oracle reserved word.");
						okay = false;
					}
				}
				rsColumn.close();
				
				// check primary key name for the table
				ResultSet rsPrimaryKey = dbmd.getPrimaryKeys(null, schemaName, tableName);

//				boolean hasPrimaryKey = false;
				
				while (rsPrimaryKey.next()) {
//					hasPrimaryKey = true;
					String keyName = rsPrimaryKey.getString("PK_NAME");
					short seqNumber = rsPrimaryKey.getShort("KEY_SEQ");
					
					if ("IDKEYField_As_PKey".equals(keyName))
						continue; // Cache's default PK for the IDENTITY column if no PK spec'd
					
					if (!uniqueNames.contains(keyName))
						uniqueNames.add(keyName);
					else if (seqNumber == 1){
						out.println(
								"WARNING Primary key id: " + keyName + " is not unique across schemas.");
						//okay = false;
					} 
					if (!OracleUtils.isValidLength(keyName)) {
						out.println(
								"Primary key id: " + schemaName + "." + tableName + "." + keyName + " is "+keyName.length()+" bytes long.");
						okay = false;
					} 
					if (OracleUtils.isReservedWord(keyName)) {
						out.println(
								"Primary key id: " + schemaName + "." + tableName + "." + keyName + " is an Oracle reserved word.");
						okay = false;
					}
				}
				rsPrimaryKey.close();
				
//				if (!hasPrimaryKey) {
//					out.println(
//							"WARNING Table: " + schemaName + "." + tableName + " does not define a primary key.");
//					//okay = false;
//				}
				
				// check foreign key names for the table
				ResultSet rsForeignKey = 
					dbmd.getImportedKeys(null, schemaName, tableName);

				while (rsForeignKey.next()) {
					String keyName = rsForeignKey.getString("FK_NAME");
					short seqNumber = rsForeignKey.getShort("KEY_SEQ");
//					short updateRule = rsForeignKey.getShort("UPDATE_RULE");
//					short deleteRule = rsForeignKey.getShort("DELETE_RULE");
//					if (debug) {
//						System.out.println("checking fk: "+keyName+", seqNumber="+seqNumber);
//						if (deleteRule != DatabaseMetaData.importedKeyNoAction )
//							System.out.println("FK: " + keyName + " delete rule="+getDeleteRule(deleteRule));
//					}
					if (!uniqueNames.contains(keyName))
						uniqueNames.add(keyName);
					else if (seqNumber == 1){
						if (debug) {
							System.out.println("Current schema names:");
							for (String s : uniqueNames) {
								System.out.println("\t"+s);
							}
						}
						out.println(
								"WARNING Foreign key id: " + keyName + " is not unique across schemas.");
						//okay = false;
					} 
					if (!OracleUtils.isValidLength(keyName)) {
						out.println(
								"Foreign key id: " + schemaName + "." + tableName + "." + keyName + " is "+keyName.length()+" bytes long.");
						okay = false;
					} 
					if (OracleUtils.isReservedWord(keyName)) {
						out.println(
								"Foreign key id: " + schemaName + "." + tableName + "." + keyName + " is an Oracle reserved word.");
						okay = false;
					}
				}
				rsForeignKey.close();				
				
//				// check unique index names for the table
//				ResultSet rsUniqueIndices =  dbmd.getIndexInfo(nameSpace, schemaName, tableName, true, true);
//				HashSet<String> uniqueIndexNames = new HashSet<String>();
//				String oldIndexName = "xxxxjunkxxx";
//				while (rsUniqueIndices.next()) {
//					if (rsUniqueIndices.getString("INDEX_NAME") != null) {
//						if (!oldIndexName.equals(rsUniqueIndices.getString("INDEX_NAME"))) {
//							String indexName = rsUniqueIndices.getString("INDEX_NAME");
//							
//							if (!uniqueIndexNames.contains(indexName))
//								uniqueIndexNames.add(indexName);
//
//							if (!uniqueNames.contains(indexName))
//								uniqueNames.add(indexName);
//							else {
//								if (debug) {
//									System.out.println("Current schema names:");
//									for (String s : uniqueNames) {
//										System.out.println("\t"+s);
//									}
//								}
//								out.println(
//										"WARNING Unique Index id: " + indexName + " is not unique across schemas.");
//								//okay = false;
//							} 							
//							
//							if (!OracleUtils.isValidLength(indexName)) {
//								out.println(
//									"Unique Index id: " + schemaName + "." + tableName + "." + indexName + " is "+indexName.length()+" bytes long.");
//								okay = false;
//							} 
//							if (OracleUtils.isReservedWord(indexName)) {
//								out.println(
//										"Unique Index id: " + schemaName + "." + tableName + "." + indexName + " is an Oracle reserved word.");
//								okay = false;
//							}
//							oldIndexName = indexName;
//						}
//					} // if index not null
//				} // while indices
//				rsUniqueIndices.close();
//				
//				// check non-unique index names for the table
//				ResultSet rsIndices =  dbmd.getIndexInfo(nameSpace, schemaName, tableName, false, true);
//
//				oldIndexName = "xxxxjunkxxx";
//				while (rsIndices.next()) {
//					if (rsIndices.getString("INDEX_NAME") != null) {
//						if (!oldIndexName.equals(rsIndices.getString("INDEX_NAME"))) {
//							String indexName = rsIndices.getString("INDEX_NAME");
//							
//							if (uniqueIndexNames.contains(indexName)) 
//								continue;
//							
//							if (!uniqueNames.contains(indexName))
//								uniqueNames.add(indexName);
//							else {
//								if (debug) {
//									System.out.println("Current schema names:");
//									for (String s : uniqueNames) {
//										System.out.println("\t"+s);
//									}
//								}
//								out.println(
//										"WARNING Non-unique Index id: " + indexName + " is not unique across schemas.");
//								//okay = false;
//							} 							
//							
//							if (!OracleUtils.isValidLength(indexName)) {
//								out.println(
//									"Non-unique Index id: " + schemaName + "." + tableName + "." + indexName + " is "+indexName.length()+" bytes long.");
//								okay = false;
//							} 
//							if (OracleUtils.isReservedWord(indexName)) {
//								out.println(
//										"Non-unique Index id: " + schemaName + "." + tableName + "." + indexName + " is an Oracle reserved word.");
//								okay = false;
//							}
//							oldIndexName = indexName;
//						}
//
//					} // if index not null
//				} // while indices
//				rsIndices.close();

				
				// check trigger names for the table
				List<String> triggerNames = getTriggerNames(cn, schemaName, tableName, dbms);
				for (String triggerName : triggerNames) {
					
					if (!uniqueNames.contains(triggerName))
						uniqueNames.add(triggerName);
					else {
						if (debug) {
							System.out.println("Current schema names:");
							for (String s : uniqueNames) {
								System.out.println("\t"+s);
							}
						}
						out.println(
								"WARNING Trigger id: " + triggerName + " is not unique across schemas.");
						//okay = false;
					} 
					
					if (!OracleUtils.isValidLength(triggerName)) {
						out.println(
								"Trigger id: " + triggerName + "." + tableName + "." + triggerName + " is "+triggerName.length()+" bytes long.");
						okay = false;
					} 
					if (OracleUtils.isReservedWord(triggerName)) {
						out.println(
								"Trigger id: " + schemaName + "." + tableName + "." + triggerName + " is an Oracle reserved word.");
						okay = false;
					}
				}

			} // while table
			rsTable.close();
			
		}
		rsSchemas.close();
		return okay;
	}
	
	static List<String> getTriggerNames(Connection cn, String schemaName, String tableName, DbmsInfo.Product dbms) {
		List<String> triggerNames = new ArrayList<String>();
		
		if (dbms.equals(DbmsInfo.Product.CACHE)) {
			java.sql.Statement statement = null;
			try {
				// SELECT parent , Event ,  Origin , SqlName , _Time  
				// FROM %Dictionary.CompiledTrigger where parent='TASKLIST.TASK'
				String sql = 
					"SELECT parent, Event,  Origin, SqlName, _Time "+
					"FROM %Dictionary.CompiledTrigger where parent='"+schemaName+"."+tableName+"'";
				statement = cn.createStatement();
				ResultSet results  = statement.executeQuery(sql);
				while (results.next()) {
					String triggerName = results.getString("SqlName");
					triggerNames.add(triggerName);
				}
				statement.close();
				
			} catch (SQLException e)
	        {
				e.printStackTrace();
	        }
		}
		
		return triggerNames;
	}
	
	static String getDeleteRule(short rule) {
		String s = null;
		
		if (rule == DatabaseMetaData.importedKeySetDefault )
			s = "importedKeySetDefault ";
		else if (rule == DatabaseMetaData.importedKeySetNull  )
			s = "importedKeySetNull";
		else if (rule == DatabaseMetaData.importedKeyCascade  )
			s = "importedKeyCascade";
		else if (rule == DatabaseMetaData.importedKeyRestrict  )
			s = "importedKeyRestrict";
		else if (rule == DatabaseMetaData.importedKeySetNull  )
			s = "importedKeySetNull";
		
		return s;
	}

}
