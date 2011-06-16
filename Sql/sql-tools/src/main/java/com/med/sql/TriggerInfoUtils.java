package com.med.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.sql.DataSource;

import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

/**
 * Supplies common information about database triggers.
 * 
 * @author Jane Eisenstein
 */
public class TriggerInfoUtils {
	
	
	public static Map<String, Set<TriggerInfo>> getTriggerInfo(
			DataSource dataSource, List<Schema> schemas, DbmsInfo.Product dbms) {
		
		Map<String,Set<TriggerInfo>> tableTriggerMap = 
			new HashMap<String,Set<TriggerInfo>>();

		Connection cn = null;
		try {
			cn = dataSource.getConnection();

			for (Schema schema : schemas) {
				final Table[] tables = schema.getTables();
				for (int i = 0; i < tables.length; i++) {
					final Table table = tables[i];

					final String tableName = table.toString();
					final String schemaId = table.getSchema().getName();
					final String tableId = table.getName();			
			
					List<TriggerInfo> triggers = 
						getTriggerInfoForTable(cn, schemaId, tableId, dbms);
			
					if (triggers.size() > 0) {
						Set<TriggerInfo> tableTriggers = new TreeSet<TriggerInfo>();
						for (TriggerInfo trigger : triggers) {
							tableTriggers.add(trigger);
						}
						tableTriggerMap.put(tableName, tableTriggers);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				cn.close();
			} catch (SQLException e) { }
			cn = null;
		}	
		
		return tableTriggerMap;
	}
	
	private static List<TriggerInfo> getTriggerInfoForTable(
			Connection cn, String schemaId, String tableId, DbmsInfo.Product dbms) {
		
		List<TriggerInfo> triggerInfo = new ArrayList<TriggerInfo>();

		java.sql.Statement statement = null;
		try {

			if (dbms.equals(DbmsInfo.Product.ORACLE)) {

				// select table_owner, table_name, trigger_name, trigger_type, triggering_event 
				// from dba_triggers where base_object_type='TABLE' and table_owner='PARTYROLE' 
				// and table_name='SPECIALTY'

				String sql = "select trigger_name, trigger_type, triggering_event "
					+ "from dba_triggers where base_object_type='TABLE' and table_owner='" + schemaId +"' "
					+ "and table_name='"+tableId+"'";
				statement = cn.createStatement();
				ResultSet results = statement.executeQuery(sql);
				while (results.next()) {
					String triggerId = results.getString("trigger_name");
					TriggerInfo info = new TriggerInfo(triggerId, tableId, schemaId);
					info.setEvent(results.getString("triggering_event"));
					info.setType(results.getString("trigger_type"));
					triggerInfo.add(info);
				}
			} else if (dbms.equals(DbmsInfo.Product.MSSQL)) {
				// TODO fill in this blank asap
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException e) { }
		}

		return triggerInfo;
	}
	
	static String stripUnderscores(String s) {
		String[] subStrings = s.split("_");
		String ss = subStrings[0];
		for (int i = 1; i < subStrings.length; i++)
			ss += subStrings[i];
		return ss;
	}
}
