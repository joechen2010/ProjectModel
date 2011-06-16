package com.med.sql;

/*
 * Represents an Oracle JDBC URL with the format:
 * jdbc:oracle:thin:@<host>:<port1521>:<sid>
 *
 * @author Jane Eisenstein
 *
 * Copyright (c) 2008 MEDecision, Inc. All rights reserved.
 */
public class OracleJdbcUrl extends AbstractJdbcUrl {

	static final String hostPrefix = "jdbc:oracle:thin:@";
	static final String portPrefix = ":";
	static final String databasePrefix = ":";

	protected String getHostPrefix() {
		return hostPrefix;
	}

	protected String getPortPrefix() {
		return portPrefix;
	}

	protected String getDatabasePrefix(){
		return databasePrefix;
	}
}
