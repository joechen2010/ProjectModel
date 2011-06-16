package com.med.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import static org.junit.Assert.assertNotNull;

public abstract class DataSourceTest {

	protected boolean debug = "true".equalsIgnoreCase(System.getProperty("debug"));
	
	String dbmsName = System.getProperty("dbms");
	
	String driver = System.getProperty("com.med.config.dataSource.alineoDataSource.jdbc.driver");
	String url = System.getProperty("com.med.config.dataSource.alineoDataSource.jdbc.url");
	String user = System.getProperty("com.med.config.dataSource.alineoDataSource.jdbc.user");
	String password = System.getProperty("com.med.config.dataSource.alineoDataSource.jdbc.password");
	
	protected ComboPooledDataSource ds = null;
	protected boolean connected = false;
	protected String nameSpace = null;
	protected DbmsInfo.Product dbms = null;
	protected JdbcProperties jdbcProps = null;

	public DataSourceTest() {
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
	
	private JdbcProperties getJdbcProperties() {
		JdbcUrl jdbcUrl = null;
		if (dbms == null || dbms.equals(DbmsInfo.Product.ORACLE))
			jdbcUrl = new OracleJdbcUrl();
		else if (dbms.equals(DbmsInfo.Product.MSSQL))
			jdbcUrl = new MssqlJdbcUrl();
		jdbcUrl.setUrl(url);
		JdbcProperties jdbcProps = JdbcPropertiesFactory.getJdbcProperties(dbms);
		jdbcProps.setProperties(jdbcUrl.getHost(), jdbcUrl.getPort(), jdbcUrl.getDatabase(), user, password);
		return jdbcProps;
	}

	protected String getNameSpace() {
		JdbcUrl jdbcUrl = null;
		if (dbms == null || dbms.equals(DbmsInfo.Product.ORACLE))
			jdbcUrl = new OracleJdbcUrl();
		else if (dbms.equals(DbmsInfo.Product.MSSQL))
			jdbcUrl = new MssqlJdbcUrl();
		jdbcUrl.setUrl(url);
		return jdbcUrl.getDatabase();
	}

	@Before
	public void setUp() throws Exception {
		if (debug) {
			System.out.println("dbms=" + dbmsName);
			System.out.println("driver=" + driver);
			System.out.println("url=" + url);
			System.out.println("user=" + user);
			System.out.println("password=" + password);
		}
	
		assertNotNull(driver);
		assertNotNull(url);
		assertNotNull(user);
		assertNotNull(password);
		
		if (dbmsName == null || dbmsName.equalsIgnoreCase("oracle"))
			dbms = DbmsInfo.Product.ORACLE;
		else if (dbmsName.equalsIgnoreCase("mssql"))
			dbms = DbmsInfo.Product.MSSQL;
		
		ds = makeDataSource();
		assertNotNull(ds);
		
		Connection cn = null;
		try {
			cn = ds.getConnection();
			connected = true;
			nameSpace = getNameSpace();
			jdbcProps = getJdbcProperties();
		} catch (Exception e) {
			if (debug) e.printStackTrace();
		} finally {
			if (cn != null) {
				try {
					cn.close();
				} catch (SQLException e) {}
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		connected = false;
		DataSources.destroy( ds );
	}

}