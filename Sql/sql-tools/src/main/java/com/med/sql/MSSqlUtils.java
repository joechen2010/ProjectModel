package com.med.sql;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class MSSqlUtils {
	
	// MS SQL internal schemas -- format is database_name.schema_name
	static final String[] MSSQL_SCHEMAS = {
		"alineo_mssql_db.db_accessadmin",
		"alineo_mssql_db.db_backupoperator",
		"alineo_mssql_db.db_datareader",
		"alineo_mssql_db.db_datawriter",
		"alineo_mssql_db.db_ddladmin",
		"alineo_mssql_db.db_denydatareader",
		"alineo_mssql_db.db_denydatawriter",
		"alineo_mssql_db.INFORMATION_SCHEMA",	// views
		"alineo_mssql_db.db_owner",
		"alineo_mssql_db.db_securityadmin",
		"alineo_mssql_db.dbo",
		"alineo_mssql_db.guest",
		"alineo_mssql_db.sys",					// views
		"alineo_mssql_db.\"SYSTEM\""
	};
	
	// MS SQL 2008 reserved words
	static final String[] MSSQL_RESERVED_WORDS = {
		"ADD",
		"ALL",
		"ALTER",
		"AND",
		"ANY",
		"AS",
		"ASC",
		"AUTHORIZATION",
		"BACKUP",
		"BEGIN",
		"BETWEEN",
		"BREAK",
		"BROWSE",
		"BULK",
		"BY",
		"CASCADE",
		"CASE",
		"CHECK",
		"CHECKPOINT",
		"CLOSE",
		"CLUSTERED",
		"COALESCE",
		"COLLATE",
		"COLUMN",
		"COMMIT",
		"COMPUTE",
		"CONSTRAINT",
		"CONTAINS",
		"CONTAINSTABLE",
		"CONTINUE",
		"CONVERT",
		"CREATE",
		"CROSS",
		"CURRENT",
		"CURRENT_DATE",
		"CURRENT_TIME",
		"CURRENT_TIMESTAMP",
		"CURRENT_USER",
		"CURSOR",
		"DATABASE",
		"DBCC",
		"DEALLOCATE",
		"DECLARE",
		"DEFAULT",
		"DELETE",
		"DENY",
		"DESC",
		"DISK",
		"DISTINCT",
		"DISTRIBUTED",
		"DOUBLE",
		"DROP",
		"DUMP",
		"ELSE",
		"END",
		"ERRLVL",
		"ESCAPE",
		"EXCEPT",
		"EXEC",
		"EXECUTE",
		"EXISTS",
		"EXIT",
		"EXTERNAL",
		"FETCH",
		"FILE",
		"FILLFACTOR",
		"FOR",
		"FOREIGN",
		"FREETEXT",
		"FREETEXTTABLE",
		"FROM",
		"FULL",
		"FUNCTION",
		"GOTO",
		"GRANT",
		"GROUP",
		"HAVING",
		"HOLDLOCK",
		"IDENTITY",
		"IDENTITYCOL",
		"IDENTITY_INSERT",
		"IF",
		"IN",
		"INDEX",
		"INNER",
		"INSERT",
		"INTERSECT",
		"INTO",
		"IS",
		"JOIN",
		"KEY",
		"KILL",
		"LEFT",
		"LIKE",
		"LINENO",
		"LOAD",
		"MERGE",
		"NATIONAL",
		"NOCHECK",
		"NONCLUSTERED",
		"NOT",
		"NULL",
		"NULLIF",
		"OF",
		"OFF",
		"OFFSETS",
		"ON",
		"OPEN",
		"OPENDATASOURCE",
		"OPENQUERY",
		"OPENROWSET",
		"OPENXML",
		"OPTION",
		"OR",
		"ORDER",
		"OUTER",
		"OVER",
		"PERCENT",
		"PIVOT",
		"PLAN",
		"PRECISION",
		"PRIMARY",
		"PRINT",
		"PROC",
		"PROCEDURE",
		"PUBLIC",
		"RAISERROR",
		"READ",
		"READTEXT",
		"RECONFIGURE",
		"REFERENCES",
		"REPLICATION",
		"RESTORE",
		"RESTRICT",
		"RETURN",
		"REVERT",
		"REVOKE",
		"RIGHT",
		"ROLLBACK",
		"ROWCOUNT",
		"ROWGUIDCOL",
		"RULE",
		"SAVE",
		"SCHEMA",
		"SECURITYAUDIT",
		"SELECT",
		"SESSION_USER",
		"SET",
		"SETUSER",
		"SHUTDOWN",
		"SOME",
		"STATISTICS",
		"SYSTEM_USER",
		"TABLE",
		"TABLESAMPLE",
		"TEXTSIZE",
		"THEN",
		"TO",
		"TOP",
		"TRAN",
		"TRANSACTION",
		"TRIGGER",
		"TRUNCATE",
		"TSEQUAL",
		"UNION",
		"UNIQUE",
		"UNPIVOT",
		"UPDATE",
		"UPDATETEXT",
		"USE",
		"USER",
		"VALUES",
		"VARYING",
		"VIEW",
		"WAITFOR",
		"WHEN",
		"WHERE",
		"WHILE",
		"WITH",
		"WRITETEXT"
	};
	
	static boolean isReservedWord(String name) {
		if (name != null) {
			for (String word : MSSQL_RESERVED_WORDS) {
				if (word.equalsIgnoreCase(name))
					return true;
			}
		}
		return Sql2003Utils.isReservedWord(name);
	}

	static boolean isMSSqlSchema(String name) {
		if (name != null) {
			for (String word : MSSQL_SCHEMAS) {
				if (word.equalsIgnoreCase(name))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return Alineo system schema names regex pattern
	 */
	public static String getMSSqlSchemaNamesPattern() {
		return StringUtils.join(MSSQL_SCHEMAS, '|');
	}

	/**
	 * Prefixes schema names with Alineo schema prefix and generates regex pattern.
	 * @param targetSchemas
	 * @return regex pattern
	 */
	public static String getSchemaNamesPattern(
			Set<String> targetSchemas) {
		
		final String schemaPrefix = "alineo_mssql_db.";
		
		String schemaNames[] = new String[targetSchemas.size()];
		int i = 0;
		for (String name : targetSchemas) {
			if (isReservedWord(name)) {
				// escape with double quotes
				name = "\""+name+"\"";
			}
			schemaNames[i++] = schemaPrefix+name;
		}
		
		return StringUtils.join(schemaNames, '|');
	}

}
