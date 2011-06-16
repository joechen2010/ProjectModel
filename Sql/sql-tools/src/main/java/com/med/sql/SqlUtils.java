package com.med.sql;

import schemacrawler.schema.NamedObject;

/**
 * Convenience functions that do not require an active database connection.
 * Methods for generating unique ids were retired in Alineo 3.1
 * 
 * @author Jane Eisenstein
 */
public class SqlUtils {
	
	public static String escapeStringLiteralDelimiter(String value, String type) {
		String delimiter = "'";
		String doubleDelimiter = "''";
		if (type.equalsIgnoreCase("VARCHAR") && 
				value.indexOf(delimiter) >= 0 &&
				value.indexOf(doubleDelimiter) < 0) {
			// double the string literal delimiters occurring within the value
			String[] fragments = value.split("\'");
			String newValue = null;
			for (String fragment: fragments) {
				if (newValue == null)
					newValue = fragment;
				else
					newValue += doubleDelimiter + fragment;
			}
			value = newValue;
		}
		return value;
	}
	
//	static String getUniqueId(
//			final String tableId, final String prefix1, final String prefix2, String suffix, final String idUse, Set<String> names)
//			throws SqlToolsException {
//		
//		final int len = prefix1.length() + prefix2.length() + suffix.length();
//		final int varMax = OracleUtils.ORACLE_MAX_LEN - len;
//		
//		String id = tableId;
//		if (id.length() > varMax)
//			id = id.substring(0, varMax);
//		
//		String newId = prefix1 + prefix2 + id + suffix;
//		
//		if (names.contains(newId)) {
//			id = id.substring(0, id.length()-1);	// assume no more than 9 variations
//			int idx = 1;
//			newId = prefix1 + idx + prefix2 + id + suffix;
//			while (names.contains(newId) && idx < 9) {
//				idx++;
//				newId = prefix1 + idx + prefix2 + id + suffix;
//			}
//		}
//		
//		if (names.contains(newId)) { 
//			id = id.substring(0, id.length()-2);	// assume no more than 99 variations
//			int idx = 10;
//			newId = prefix1 + idx + prefix2 + id + suffix;
//			while (names.contains(newId) && idx < 99) {
//				idx++;
//				newId = prefix1 + idx + prefix2 + id + suffix;
//			}			
//		}
//		
//		if (!names.add(newId))
//			throw new SqlToolsException("Unable to generate "+idUse+" id for table: "+tableId);
//		
//		return newId;
//	}
//	
//	public static String getUniqueSequenceTriggerId(final String tableId, Set<String> names) 
//	throws SqlToolsException {
//
//		return getUniqueId(tableId, "TR", "_", "_SEQ_BI", "sequence trigger", names );
//	}
//	
//	public static String getUniqueAlternateKeyId(final String tableId, Set<String> names) 
//	throws SqlToolsException {
//		
//		return getUniqueId(tableId, "AK", "_", "", "alternate key", names );
//	}
//
//	public static String getUniqueSequenceId(final String tableId, Set<String> names) 
//	throws SqlToolsException {
//	
//		return getUniqueId(tableId, "SEQ", "_", "", "sequence", names );
//	}
//
//	public static String getUniqueForeignKeyId(final String fkTableId, final String pkTableId, Set<String> names) 
//	throws SqlToolsException {
//		
//		return getUniqueId(fkTableId+"_"+pkTableId, "FK", "_", "", "foreign key", names );
//	}
//	
//	public static String getUniquePrimaryKeyId(final String tableId, Set<String> names) 
//	throws SqlToolsException {
//		
//		return getUniqueId(tableId, "PK", "_", "", "primary key", names );
//	}
//	
//	public static String getIndexColumnList(NamedObject[] objs, IndexSortSequence sortSeq) {
//		String order = "";
//		if (IndexSortSequence.ascending.equals(sortSeq))
//			order = " ASC";
//		else if (IndexSortSequence.descending.equals(sortSeq))
//			order = " DESC";
//		StringBuffer buf = new StringBuffer(objs[0].getName()+order);
//		for (int i = 1; i < objs.length; i++) 
//			buf.append(", "+objs[i].getName()+order);
//		return buf.toString();
//	}
	
	public static String getCommaIdList(NamedObject[] objs) {
		StringBuffer buf = new StringBuffer(objs[0].getName());
		for (int i = 1; i < objs.length; i++) 
			buf.append(", "+objs[i].getName());
		return buf.toString();
	}
	
	public static String getCommaValueList(String[] objs) {
		StringBuffer buf = new StringBuffer(objs.length);
		buf.append(objs[0]);
		for (int i = 1; i < objs.length; i++) 
			buf.append(", "+objs[i]);
		return buf.toString();
	}

}
