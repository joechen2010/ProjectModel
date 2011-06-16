package com.med.sql;


/**
 * This class encapsulates the properties needed to establish a JDBC connection
 * to a particular DBMS. There is no setter for the driver class name because
 * it is always DBMS-specific.
 *
 * @author Jane Eisenstein
 *
 * Copyright (c) 2008 MEDecision, Inc. All rights reserved.
 */
public interface JdbcProperties {

	public void setProperties(
			String host, Long port, String database,
			String userId, String password) throws IllegalArgumentException;

	public JdbcUrl getJdbcUrl();

	public String getUserId();

	public String getPassword();

	public String getDriverClassName() throws ClassNotFoundException;

}
