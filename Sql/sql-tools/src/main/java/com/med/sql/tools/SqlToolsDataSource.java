package com.med.sql.tools;

import java.sql.Connection;
import java.sql.SQLException;
import  javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.med.sql.*;

/**
 * Supplies data source information for sql-tools applications based on sql-tools properties.
 * 
 * @author Jane Eisenstein
 */
public class SqlToolsDataSource implements SqlToolsConstants {

	 SqlToolsProperties props = null;
	 String driver = null;
	 String url = null;
	 String user = null;
	 String password = null;
	
	 ComboPooledDataSource ds = null;
	 String nameSpace = null;
	 DbmsInfo.Product dbms = null;
	 JdbcProperties jdbcProps = null;
	 
	 boolean initialized = false;
	
	/**
	 * Initialize from sql-tools properties.
	 * @param props
	 */
	 public SqlToolsDataSource(SqlToolsProperties props) {
		this.props = props;
	}

	/**
	 * Bypasses the need to construct a SqlToolsProperties.
	 * @param driver
	 * @param url
	 * @param user
	 * @param password
	 */
	public SqlToolsDataSource(String driver, String url, String user, String password) {
		initialize(driver, url, user, password);
	}

	private void initialize(String driver, String url, String user, String password) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
		
		String test = driver.toLowerCase();
		if (test.contains("oracle")) {
			this.dbms = DbmsInfo.Product.ORACLE;
		} else if (test.contains("net.sourceforge.jtds")) {
			this.dbms = DbmsInfo.Product.MSSQL;
		}
		
		initialized = true;
	}
	
	private void initialize() {
		if (!initialized && props != null) {
			initialize(
				props.getString(driver_key), 
				props.getString(url_key), 
				props.getString(user_key), 
				props.getString(password_key));
		}
	}
	
	/**
	 * Extracts the dbms type from the configured JDBC driver class name.
	 * @return DbmsInfo.Product
	 */
	public DbmsInfo.Product getDbms() {
		initialize();
		return dbms;
	}
	
	public JdbcProperties getJdbcProperties() {
		if (jdbcProps == null) {
			initialize();
			JdbcUrl jdbcUrl = null;
			if (dbms == null || dbms.equals(DbmsInfo.Product.ORACLE))
				jdbcUrl = new OracleJdbcUrl();
			else if (dbms.equals(DbmsInfo.Product.MSSQL))
				jdbcUrl = new MssqlJdbcUrl();
			jdbcUrl.setUrl(url);
			jdbcProps = JdbcPropertiesFactory.getJdbcProperties(dbms);
			jdbcProps.setProperties(jdbcUrl.getHost(), jdbcUrl.getPort(), jdbcUrl.getDatabase(), user, password);
		}
		return jdbcProps;
	}

	/**
	 * Extracts the database name of the configured data source.
	 * @return String
	 */
	public String getDatabaseName() {
		if (nameSpace == null) {
			initialize();
			JdbcUrl jdbcUrl = null;
			if (dbms == null || dbms.equals(DbmsInfo.Product.ORACLE))
				jdbcUrl = new OracleJdbcUrl();
			else if (dbms.equals(DbmsInfo.Product.MSSQL))
				jdbcUrl = new MssqlJdbcUrl();
			jdbcUrl.setUrl(url);
			nameSpace = jdbcUrl.getDatabase();
		}
		return nameSpace;
	}
	
	/**
	 * Returns the configured data source after verifying the connection info.
	 * @return DataSource if successful, otherwise null.
	 */
	public DataSource getDataSource() {
		if (ds == null) {
			initialize();
			ds = makeDataSource();
			Connection cn = null;
			try {
				cn = ds.getConnection();
			} catch (Exception e) {
				// e.printStackTrace();
			} finally {
				if (cn != null) {
					try {
						cn.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		return ds;
	}
	
	private ComboPooledDataSource makeDataSource() {
		// This is a C3P0 data source
		ComboPooledDataSource cpds = null;
	    try {
	    	cpds = new ComboPooledDataSource();
	    	cpds.setDriverClass(driver);
	    	cpds.setJdbcUrl(url);
	    	cpds.setUser(user);
	    	cpds.setPassword(password);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	cpds = null;
	    }
	    return cpds;
	}

}
