package com.med.sql;

/**
 * This class implements the Oracle-specific features of a JdbcProperties
 *
 * @author Jane Eisenstein
 *
 * Copyright (c) 2008 MEDecision, Inc. All rights reserved.
 */
public class OracleJdbcProperties  extends AbstractJdbcProperties {

	protected Class getDriverClass() throws ClassNotFoundException {
		final String driverClassName = "oracle.jdbc.driver.OracleDriver";
		return Class.forName(driverClassName);
	}

	protected JdbcUrl createJdbcUrl() {
		return new OracleJdbcUrl();
	}

}
