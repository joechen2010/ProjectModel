package com.med.sql;


/**
 * Represents a Cache JDBC URL with the format:
 * jdbc:Cache://<host>:<port>/<namespace>
 *
 * @author Jane Eisenstein
 *
 * Copyright (c) 2007, 2008 MEDecision, Inc. All rights reserved.
 */
public class CacheJdbcUrl extends AbstractJdbcUrl {

	static final String hostPrefix = "jdbc:Cache://";
	static final String portPrefix = ":";
	static final String databasePrefix = "/";

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
