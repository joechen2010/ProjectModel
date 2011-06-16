package com.med.sql;

/*
 * This class implements non-DBMS specific features of a JdbcProperties.
 *
 * @author Jane Eisenstein
 *
 * Copyright (c) 2008 MEDecision, Inc. All rights reserved.
 */
public abstract class AbstractJdbcProperties implements JdbcProperties {

	protected abstract Class getDriverClass() throws ClassNotFoundException;
	protected abstract JdbcUrl createJdbcUrl();

	JdbcUrl url = null;
	String userId = null;
	String password = null;

	// JdbcProperties interface methods

	public void setProperties(
			String host, Long port, String database,
			String userId, String password) throws IllegalArgumentException {

		if (host != null && port != null && database != null
				&& userId != null && password != null) {

			this.url = createJdbcUrl();
			this.url.setProperties(host, port, database);
	    	this.userId = userId;
	    	this.password = password;

		} else
			throw new IllegalArgumentException();
	}

	public JdbcUrl getJdbcUrl() {
		return this.url;
	}

	public String getUserId() {
		return this.userId;
	}

	public String getPassword() {
		return this.password;
	}

	public String getDriverClassName() throws ClassNotFoundException {
		return getDriverClass().getName();
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getClass().getName());
		buf.append("{\ndriver: "); 
		try {
			Class c = getDriverClass();
			if (c != null)
				buf.append(c.getName());
			else
				buf.append(c);
		} catch (ClassNotFoundException e) {
			buf.append("class not found");
		}
		buf.append("\nurl: "); buf.append(getJdbcUrl().getUrl());
		buf.append("\nuser: "); buf.append(getUserId());
		buf.append("\npw: "); buf.append(getPassword());
		buf.append("\n}"); 
		return buf.toString();
	}
}
