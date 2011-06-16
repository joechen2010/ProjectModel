package com.med.sql;

/**
 * This class encapsulates the components of a JDBC URL.
 *
 * @author Jane Eisenstein
 *
 * Copyright (c) 2008 MEDecision, Inc. All rights reserved.
 */
public interface JdbcUrl {

	public abstract void setProperties(String host, Long port, String database)
			throws IllegalArgumentException;

	public abstract void setUrl(String url) throws IllegalArgumentException;

	public abstract String getUrl();

	public abstract String getHost();

	public abstract Long getPort();

	public abstract String getDatabase();

}
