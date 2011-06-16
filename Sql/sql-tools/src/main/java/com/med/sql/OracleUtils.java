package com.med.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

public class OracleUtils {
	
	static final int ORACLE_MAX_LEN = 30; // Oracle name length limit
	
	static final String[] ORACLE_SCHEMAS = {
		"ANONYMOUS",
		"APEX_030200",
		"APEX_PUBLIC_USER",
		"APPQOSSYS",
		"AURORA$JIS$UTILITY$",
		"AURORA$ORB$UNAUTHENTICATED",
		"BI",
		"CTXSYS",
		"DBSNMP",
		"DIP",
		"DMSYS",
		"EXFSYS",
		"FLOWS_FILES",
		"FLOWS_XXXXXX",
		"HR",
		"IX",
		"LBACSYS",
		"MDDATA",
		"MDSYS",
		"MEDDBA",	// only appears on AIX
		"MGMT_VIEW",
		"MTSSYS",
		"OASPUBLIC",
		"ODM",
		"ODM_MTR",
		"OE",
		"OLAPSYS",
		"ORACLE_OCM",
		"ORDDATA",
		"ORDPLUGINS",
		"ORDSYS",
		"OSE$HTTP$ADMIN",
		"OUTLN",
		"OWBSYS",
		"OWBSYS_AUDIT",
		"PERFMON",
		"PM",
		"SCOTT",
		"SH",
		"SI_INFORMTN_SCHEMA",
		"SPATIAL_CSW_ADMIN_USR",
		"SPATIAL_WFS_ADMIN_USR",
		"SYS",
		"SYSMAN",
		"SYSTEM",
		"TRACESRV",
		"TSMSYS",
		"WEBSYS",
		"WKSYS",
		"WK_PROXY",
		"WK_TEST",
		"WMSYS",
		"XDB"		
	};

	static final String[] ORACLE_SCHEMAS_REGEX = {
		"ANONYMOUS",
		"AURORA\\$JIS\\$UTILITY\\$",
		"AURORA\\$ORB\\$UNAUTHENTICATED",
		"BI",
		"CTXSYS",
		"DBSNMP",
		"DIP",
		"DMSYS",
		"EXFSYS",
		"FLOWS_XXXXXX",
		"HR",
		"IX",
		"LBACSYS",
		"MDDATA",
		"MDSYS",
		"MEDDBA",	// only appears on AIX
		"MGMT_VIEW",
		"MTSSYS",
		"OASPUBLIC",
		"ODM",
		"ODM_MTR",
		"OE",
		"OLAPSYS",
		"ORACLE_OCM",
		"ORDPLUGINS",
		"ORDSYS",
		"OSE\\$HTTP\\$ADMIN",
		"OUTLN",
		"PERFMON",
		"PM",
		"SCOTT",
		"SH",
		"SI_INFORMTN_SCHEMA",
		"SYS",
		"SYSMAN",
		"SYSTEM",
		"TRACESRV",
		"TSMSYS",
		"WEBSYS",
		"WKSYS",
		"WK_PROXY",
		"WK_TEST",
		"WMSYS",
		"XDB"		
	};

	static final String[] ORACLE_RESERVED_WORDS = {
		"ACCESS",
		"ADD",
		"ALL",
		"ALTER",
		"AND",
		"ANY",
		"ARRAYLEN",
		"AS",
		"ASC",
		"AUDIT",
		"BETWEEN",
		"BY",
		"CHAR",
		"CHECK",
		"CLUSTER",
		"COLUMN",
		"COMMENT",
		"COMPRESS",
		"CONNECT",
		"CREATE",
		"CURRENT",
		"DATE",
		"DECIMAL",
		"DEFAULT",
		"DELETE",
		"DESC",
		"DISTINCT",
		"DROP",
		"ELSE",
		"EXCLUSIVE",
		"EXISTS",
		"FILE",
		"FLOAT",
		"FOR",
		"FROM",
		"GRANT",
		"GROUP",
		"HAVING",
		"IDENTIFIED",
		"IMMEDIATE",
		"IN",
		"INCREMENT",
		"INDEX",
		"INITIAL",
		"INSERT",
		"INTEGER",
		"INTERSECT",
		"INTO",
		"IS",
		"LEVEL",
		"LIKE",
		"LOCK",
		"LONG",
		"MAXEXTENTS",
		"MINUS",
		"MODE",
		"MODIFY",
		"NOAUDIT",
		"NOCOMPRESS",
		"NOT",
		"NOTFOUND",
		"NOWAIT",
		"NULL",
		"NUMBER",
		"OF",
		"OFFLINE",
		"ON",
		"ONLINE",
		"OPTION",
		"OR",
		"ORDER",
		"PCTFREE",
		"PRIOR",
		"PRIVILEGES",
		"PUBLIC",
		"RAW",
		"RENAME",
		"RESOURCE",
		"REVOKE",
		"ROW",
		"ROWID",
		"ROWLABEL",
		"ROWNUM",
		"ROWS",
		"SELECT",
		"SESSION",
		"SET",
		"SHARE",
		"SIZE",
		"SMALLINT",
		"SQLBUF",
		"START",
		"SUCCESSFUL",
		"SYNONYM",
		"SYSDATE",
		"TABLE",
		"THEN",
		"TO",
		"TRIGGER",
		"UID",
		"UNION",
		"UNIQUE",
		"UPDATE",
		"USER",
		"VALIDATE",
		"VALUES",
		"VARCHAR",
		"VARCHAR2",
		"VIEW",
		"WHENEVER",
		"WHERE",
		"WITH"
	};
	
	static boolean isValidLength(String name) {
		return name != null && name.length() <= ORACLE_MAX_LEN;
	}
	
	static boolean isReservedWord(String name) {
		if (name != null) {
			for (String word : ORACLE_RESERVED_WORDS) {
				if (word.equalsIgnoreCase(name))
					return true;
			}
		}
		return Sql2003Utils.isReservedWord(name);
	}
	
	static boolean isOracleSchema(String name) {
		if (name != null) {
			for (String word : ORACLE_SCHEMAS) {
				if (word.equalsIgnoreCase(name))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * @return Oracle system schema names regex pattern
	 */
	public static String getOracleSchemaNamesPattern() {
		return StringUtils.join(ORACLE_SCHEMAS_REGEX, '|');
	}

	/**
	 * Analyzes dataSource metadata to generate regex pattern of schema names.
	 * If excludeTargetSchemas is false, the pattern contains all schema names in targetSchemas.
	 * If excludeTargetSchemas is true, the pattern contains all non-system schema 
	 * names that are not in targetSchemas.
	 * @param dataSource
	 * @param targetSchemas
	 * @param excludeTargetSchemas
	 * @return regex pattern
	 */
	public static String getSchemaNamesPattern(
			DataSource dataSource,  
			Set<String> targetSchemas, 
			boolean excludeTargetSchemas) {
		
		Set<String> names = new TreeSet<String> ();

		Connection cn = null;
		try {
			cn = dataSource.getConnection();
			DatabaseMetaData dbmd = cn.getMetaData();
			
			ResultSet rsSchemas = dbmd.getSchemas();
			while (rsSchemas.next()) {
				String schemaName = rsSchemas.getString(1);
				
				if (OracleUtils.isOracleSchema(schemaName))
					continue;
				
				boolean addName = (targetSchemas == null) || targetSchemas.isEmpty();
				if (!addName) {
					boolean inTargetSchemas = targetSchemas.contains(schemaName);
					addName = 
						(inTargetSchemas && !excludeTargetSchemas)
							|| (!inTargetSchemas && excludeTargetSchemas);
				}
				
				if (addName)
					names.add(schemaName);
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

		String schemaNames[] = new String[names.size()];
		int i = 0;
		for (String name : names) {
			if (isReservedWord(name)) {
				// escape with double quotes
				name = "\""+name+"\"";
			}
			schemaNames[i++] = name;
		}
		
		return StringUtils.join(schemaNames, '|');
	}
}
