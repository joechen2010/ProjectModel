package com.med.sql;

/**
 * This class implements the Cache-specific features of a JdbcProperties
 *
 * @author Jane Eisenstein
 *
 * Copyright (c) 2008 MEDecision, Inc. All rights reserved.
 */
public class CacheJdbcProperties extends AbstractJdbcProperties {

	protected Class getDriverClass() throws ClassNotFoundException {
		final String driverClassName = "com.intersys.jdbc.CacheDriver";
		return Class.forName(driverClassName);
	}

	protected JdbcUrl createJdbcUrl() {
		return new CacheJdbcUrl();
	}

}
