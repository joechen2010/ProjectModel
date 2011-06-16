package com.med.sql;

/** Represents a MSSQL JDBC URL with the format: 
 *  jdbc:jtds:sqlserver://<host>:<port133>/<sid> 
 *  @author TeamSQL 
 */

public class MssqlJdbcUrl extends AbstractJdbcUrl {
	static final String hostPrefix = "jdbc:jtds:sqlserver://";
	static final String portPrefix = ":";
	static final String databasePrefix = "/";

	protected String getHostPrefix() {
		return hostPrefix;
	}

	protected String getPortPrefix() {
		return portPrefix;
	}

	protected String getDatabasePrefix() {
		return databasePrefix;
	}
}