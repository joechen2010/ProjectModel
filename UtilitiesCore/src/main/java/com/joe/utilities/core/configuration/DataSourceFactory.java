
package com.joe.utilities.core.configuration;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.ConfigurationException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joe.utilities.core.util.ReturnStatus;
import com.joe.utilities.core.validation.Validator;
import com.jolbox.bonecp.BoneCPDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;


/**
 * The DataSourceFactory is responsible for creating or retrieving the DataSource Object to be
 * used by all components. object allow us to be more flexible with accessing thewith the 
 * <br><br>
 * <b>DataSource configuration.</b>
 * <br><br>
 * This Application utilizes a Datasource to obtain connections to the database.
 * The datasource must be properly configured for this application to function properly.
 * <br><br>
 * Ideally, the dataSource will be configured and managed by the application server.  Most
 * application servers provide some type of pooled connections to the database, usually thru 
 * a datasource.
 * <br><br>
 * If you are using an application server that cannot manage connections, you can alternatively
 * configure generic jdbc database information to create a dataSource where the pooling is managed
 * by the Medecision application.
 * <br><br>
 * <table border=1>
 * <tr><td colspan=2>
 * Note:  Multiple datasources can be configured, each requiring the following information; where {name}
 * represents the name of the datasource as referenced within the application.
 * </td></tr>
 * <tr><th>Key</th><th>Description</th></tr>
 * <tr><td>com.med.config.dataSource.list          </td><td>names of the datasources separated by comma (example default, oracle1) <br>One definition Per file</td></tr>
 * <tr><td>com.med.config.dataSource.{name}.type   </td><td>jndi [for container managed], or jdbc [for application managed].</td></tr>
 * <tr><td>com.med.config.dataSource.{name}.jndi.name     </td><td>if jndi, the jndi path & name used to retrieve the dataSource from jndi.</td></tr>
 * <tr><td>com.med.config.dataSource.{name}.jdbc.driver   </td><td>JDBC Driver used to connect to the database.</td></tr>
 * <tr><td>com.med.config.dataSource.{name}.jdbc.url      </td><td>JDBC URL used to connect to the database.</td></tr>
 * <tr><td>com.med.config.dataSource.{name}.jdbc.user     </td><td>UserId used to connect to the database.</td></tr>
 * <tr><td>com.med.config.dataSource.{name}.jdbc.password </td><td>Password used to connect to the database.</td></tr>
 * </table>
 * @author rrichard
 *
 */
public class DataSourceFactory implements Validator {
	
	/** Contains the default value to use for the c3p0 checkoutTimeout value. */
    private static final int C3PO_DEFAULT_CHECKOUT_TIMEOUT = 5000;

	private static Log logger = LogFactory.getLog(DataSourceFactory.class);
    
    private Configuration config = null;
    private static DataSourceFactory instance = new DataSourceFactory();

    private Set<String> configuredDataSourceNames = new HashSet<String>(4);
    private Map<String, DataSource> validDataSourceMap = new HashMap<String, DataSource>(4);
    
    private static final String RK_DATASOURCE_LIST = "com.med.config.dataSource.list";
    private static final String RK_DATASOURCE_TYPE = "com.med.config.dataSource.{0}.type";
    private static final String RK_DATASOURCE_JNDI_NAME = "com.med.config.dataSource.{0}.jndi.name";
    private static final String RK_DATASOURCE_JDBC_URL = "com.med.config.dataSource.{0}.jdbc.url";
    private static final String RK_DATASOURCE_JDBC_DRIVER = "com.med.config.dataSource.{0}.jdbc.driver";
    private static final String RK_DATASOURCE_JDBC_USER = "com.med.config.dataSource.{0}.jdbc.user";
    private static final String RK_DATASOURCE_JDBC_PASSWORD = "com.med.config.dataSource.{0}.jdbc.password";
    private static final String RK_DATASOURCE_JDBC_INITIAL_POOL_SIZE = "com.med.config.dataSource.{0}.jdbc.initialPoolSize";
    private static final String RK_DATASOURCE_JDBC_MAX_POOL_SIZE = "com.med.config.dataSource.{0}.jdbc.maxPoolSize";
    private static final String RK_DATASOURCE_JDBC_MAX_CONNECTION_WAIT = "com.med.config.dataSource.{0}.jdbc.maxConnectionWait";
    private static final String RK_DATASOURCE_JDBC_POOL_PREPARED_STATEMENTS = "com.med.config.dataSource.{0}.jdbc.poolPreparedStatements";

    /**
     * @throws ConfigurationException 
     * 
     */
    private DataSourceFactory() {
        initConfiguration();
        loadDataSources();
        
    }
    
    public static DataSourceFactory getInstance() {
        if (instance == null) {
            instance = new DataSourceFactory();
        }
        return instance;
    }
    
    /**
     * Forces DataSourceFactory to refresh its configuration.
     */
    public static void clearInstance() {
    	
    	// Clear data sources
    	for (Map.Entry<String, DataSource> entry : instance.validDataSourceMap.entrySet())
    	{
    		DataSource dataSource = entry.getValue();
    		if (dataSource instanceof ComboPooledDataSource)
			{
				ComboPooledDataSource c3p0DataSource  = (ComboPooledDataSource) dataSource;
				c3p0DataSource.close();
			}
    		else if (dataSource instanceof BasicDataSource)
			{
				BasicDataSource dbcpDataSource  = (BasicDataSource) dataSource;
				try
				{
					dbcpDataSource.close();
				}
				catch (SQLException e)
				{
					logger.error("Error closing Apache DBCP connection",e);
				}
			}
    	}
    	
    	// Clear instance
    	instance = null;
    }
    
    /**
     * Method initConfiguration. Initializes the factory from configuration information in the globals properties file.
     * @throws GlobalConfigurationException 
     * @return void
     */
    private void initConfiguration() throws GlobalConfigurationException 
    {
        CompositeConfiguration cc = new CompositeConfiguration();
        cc.addConfiguration(new SystemConfiguration());
        
        // customer global properties may be an XMLPropertiesConfiguration
        PropertiesConfiguration propertiesConfiguration = Globals.loadCustomerGlobalProperties();
        if (propertiesConfiguration != null) {

            cc.addConfiguration(propertiesConfiguration);
            this.config = cc;
        }
    }
    
    /**
     * Cycles thru the list of data Sources @see this{@link #RK_DATASOURCE_LIST} from SystemConfig.properties
     * 
     */
    private void loadDataSources() 
    {
        String[] dataSourceNames = this.config.getStringArray(RK_DATASOURCE_LIST);

        for (String currentDataSource : dataSourceNames)
        {
        	
        	logger.info("Starting Data Source:  " + currentDataSource);
        	
            // Update list of configured data sources
            configuredDataSourceNames.add(currentDataSource);
            
            DataSource currDS = null;
            if ("jndi".equalsIgnoreCase(this.config.getString(MessageFormat.format(RK_DATASOURCE_TYPE, currentDataSource))))
            {
                currDS = getJNDIDataSource(currentDataSource);
            }
            else
            {
                currDS = getJDBCDataSource(currentDataSource);
            }
            testConnection(currDS);
            this.validDataSourceMap.put(currentDataSource, currDS);
        	logger.info("Data Source:  " + currentDataSource + " seems to be up.");        	

        }
    }
    
    /** Test a connection to see if it is valid.  It uses DatabaseMetaData.getMetaData() to see if 
     * it can retrieve meta data against the database.  
     * @param currDS DataSource to test
     * @throws GlobalConfigurationException Thrown when the system cannot connect to the database.
     */
    public void testConnection(DataSource currDS) {
        Connection conn  = null;
        ResultSet rsSchema  = null;
        try{
            if (currDS == null) {
                logger.error(".testConnection:  DataSource is null");
                throw new GlobalConfigurationException("Invalid DataSource");
            }
            conn = currDS.getConnection();
            
            if(conn == null)  {
                logger.error(".testConnection:  Connection is null");
                throw new GlobalConfigurationException("Unable to Connect to database");
            }
            
            DatabaseMetaData dbmd = conn.getMetaData();
            
            rsSchema = dbmd.getSchemas();
            
          } catch(SQLException e) {
              logger.error(".testConnection:  " + e.getMessage());
            throw new GlobalConfigurationException("Unable to Connect to database: " + e.getMessage());
          } finally {
        	  if (rsSchema != null ){
        		  try {
        			  rsSchema.close();
        		  } catch (SQLException e) {
                  }
        	  }
              if (conn != null) {
                  try {
                    conn.close();
                } catch (SQLException e) {
                }
              }
          }
    }
    /**
     * This Methods creates a Datasource from org.apache.commons.dbcp.BasicDataSource.
     * @return Returns the Datasource.
     */
    private DataSource getJDBCDataSource(String dataSourceName) 
    {
    	// Determine type of JDBC data source.
    	String setting = MessageFormat.format(RK_DATASOURCE_TYPE, dataSourceName);
    	String jdbcType = this.config.getString(setting);
    	if (jdbcType == null)
    		throw new GlobalConfigurationException("JDBC type not specified for data source.  Missing global properties setting: " + setting);
    	jdbcType = jdbcType.toLowerCase();
    	
    	// Use default of Apache DBCP if generic "jdbc" is still  
    	if ("jdbc".equals(jdbcType))
   			jdbcType = "apache-dbcp";

    	// Setup data source
    	if (jdbcType.startsWith("apache"))
    		return getApacheDBCPDataSource(dataSourceName);
    	else if (jdbcType.startsWith("c3p"))
    		return getC3P0DataSource(dataSourceName);
    	else
    		throw new GlobalConfigurationException("Unexpected JDBC type specified at global properties setting '" + setting + "' supported values include 'apache-dbcp' and 'c3p0'.");    
    }
    
    /**
     * Gets the apache dbcp data source.
     * 
     * @param dataSourceName the data source name
     * 
     * @return the Apache DBCP data source
     */
    private DataSource getApacheDBCPDataSource(String dataSourceName)
    {
		logger.info("Initializing '"+dataSourceName+"' data source using Apache DBCP...");
		
        // Setup Apache DBCP basic data source with mandatory settings
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(validateAndReturnRequired(MessageFormat.format(RK_DATASOURCE_JDBC_DRIVER, dataSourceName)));
        basicDataSource.setUrl(validateAndReturnRequired(MessageFormat.format(RK_DATASOURCE_JDBC_URL, dataSourceName)));
        basicDataSource.setUsername(validateAndReturnRequired(MessageFormat.format(RK_DATASOURCE_JDBC_USER, dataSourceName)));
        basicDataSource.setPassword(validateAndReturnRequired(MessageFormat.format(RK_DATASOURCE_JDBC_PASSWORD, dataSourceName)));
        
        // Setup optional settings. If not set, then Apache Commons DBCP default settings are used.
        String initialPoolSize = MessageFormat.format(RK_DATASOURCE_JDBC_INITIAL_POOL_SIZE, dataSourceName);
        if (config.containsKey(initialPoolSize))
            basicDataSource.setInitialSize(config.getInt(initialPoolSize));    
        String maxPoolSize = MessageFormat.format(RK_DATASOURCE_JDBC_MAX_POOL_SIZE, dataSourceName);
        if (config.containsKey(maxPoolSize))
            basicDataSource.setMaxActive(config.getInt(maxPoolSize));    
        
        // Note: Do not use Apache default of (indefinite wait - System hangs)
        String maxConnectionWait = MessageFormat.format(RK_DATASOURCE_JDBC_MAX_CONNECTION_WAIT, dataSourceName);
        basicDataSource.setMaxWait(config.getInt(maxConnectionWait, 5000));    
        
        // Pool/cache prepared statements
        String poolPreparedStatements = MessageFormat.format(RK_DATASOURCE_JDBC_POOL_PREPARED_STATEMENTS, dataSourceName);
        basicDataSource.setPoolPreparedStatements(config.getBoolean(poolPreparedStatements, true));
        
        return basicDataSource;
    }
    
    /**
     * Gets the c3 p0 data source.
     * See @link http://www.mchange.com/projects/c3p0/index.html#configuration for complete documentation of
     * c3p0 settings. Additional properties may also be setup in "c3p0.properties" file at root of classpath.  
     * Properties set in this method will override properties set in "c3p0.properties" file.
     * 
     * @param dataSourceName the data source name
     * 
     * @return the C3P0 data source
     */
    private DataSource getC3P0DataSource(String dataSourceName)
    {
		logger.info("Initializing '"+dataSourceName+"' data source using C3P0 pooling...");
		
		// Setup primary C3P0 pooled data source
		ComboPooledDataSource c3p0PooledDataSource = new ComboPooledDataSource();
		String driverClassSetting = MessageFormat.format(RK_DATASOURCE_JDBC_DRIVER, dataSourceName);
		String driverClass = validateAndReturnRequired(driverClassSetting);
		try
		{
			c3p0PooledDataSource.setDriverClass(driverClass);
		}
		catch (PropertyVetoException e)
		{
			throw new GlobalConfigurationException("DataSourceFactory: C3P0 did not like the driver class '"+driverClass+"' specified in the global properties at '"+driverClass+"'.", e);
		} 
		c3p0PooledDataSource.setJdbcUrl(validateAndReturnRequired(MessageFormat.format(RK_DATASOURCE_JDBC_URL, dataSourceName)));
        c3p0PooledDataSource.setUser(validateAndReturnRequired(MessageFormat.format(RK_DATASOURCE_JDBC_USER, dataSourceName)));
        c3p0PooledDataSource.setPassword(validateAndReturnRequired(MessageFormat.format(RK_DATASOURCE_JDBC_PASSWORD, dataSourceName)));
        
        // Setup optional settings. If not set, then C3P0 defaults will be used.  
        String acquireIncrement = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.acquireIncrement", dataSourceName);
        if (config.containsKey(acquireIncrement))
        	c3p0PooledDataSource.setAcquireIncrement(config.getInt(acquireIncrement));
        String maxIdleTime = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.maxIdleTime", dataSourceName);
        if (config.containsKey(maxIdleTime))
        	c3p0PooledDataSource.setMaxIdleTime(config.getInt(maxIdleTime));
        String initialPoolSize = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.minPoolSize", dataSourceName);
        if (config.containsKey(initialPoolSize))
        	c3p0PooledDataSource.setInitialPoolSize(config.getInt(initialPoolSize));
        String maxPoolSize = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.maxPoolSize", dataSourceName);
        if (config.containsKey(maxPoolSize))
        	c3p0PooledDataSource.setMaxPoolSize(config.getInt(maxPoolSize));
        String maxConnectionAge = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.maxConnectionAge", dataSourceName);
        if (config.containsKey(maxConnectionAge))
        	c3p0PooledDataSource.setMaxConnectionAge(config.getInt(maxConnectionAge));
        String maxIdleTimeExcessConnections = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.maxIdleTimeExcessConnections", dataSourceName);
        if (config.containsKey(maxIdleTimeExcessConnections))
        	c3p0PooledDataSource.setMaxIdleTimeExcessConnections(config.getInt(maxIdleTimeExcessConnections));
        String maxStatements = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.maxStatements", dataSourceName);
        if (config.containsKey(maxStatements))
        	c3p0PooledDataSource.setMaxStatements(config.getInt(maxStatements));
        String maxStatementsPerConnection = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.maxStatementsPerConnection", dataSourceName);
        if (config.containsKey(maxStatementsPerConnection))
        	c3p0PooledDataSource.setMaxStatementsPerConnection(config.getInt(maxStatementsPerConnection));
        String numHelperThreads = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.numHelperThreads", dataSourceName);
        if (config.containsKey(numHelperThreads))
        	c3p0PooledDataSource.setNumHelperThreads(config.getInt(numHelperThreads));
        else
        	c3p0PooledDataSource.setNumHelperThreads(10);
        String acquireRetryAttempts = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.acquireRetryAttempts", dataSourceName);
        if (config.containsKey(acquireRetryAttempts))
        	c3p0PooledDataSource.setAcquireRetryAttempts(config.getInt(acquireRetryAttempts));
        String acquireRetryDelay = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.acquireRetryDelay", dataSourceName);
        if (config.containsKey(acquireRetryDelay))
        	c3p0PooledDataSource.setAcquireRetryDelay(config.getInt(acquireRetryDelay));
        String breakAfterAcquireFailure = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.breakAfterAcquireFailure", dataSourceName);
        if (config.containsKey(breakAfterAcquireFailure))
        	c3p0PooledDataSource.setBreakAfterAcquireFailure(config.getBoolean(breakAfterAcquireFailure));
        
        String checkoutTimeout = MessageFormat.format("com.med.config.dataSource.{0}.c3p0.checkoutTimeout.milliseconds", dataSourceName);
        if (config.containsKey(checkoutTimeout)){
        	c3p0PooledDataSource.setCheckoutTimeout(config.getInt(checkoutTimeout));
        	if (0 == config.getInt(checkoutTimeout)){
        		logger.trace("For c3p0 connection setting chekoutTimeout to 0.  If the database goes and a call to getConnection() is made then the system will hang.");
        	}
        } else {
        	c3p0PooledDataSource.setCheckoutTimeout(DataSourceFactory.C3PO_DEFAULT_CHECKOUT_TIMEOUT);
        }
        return c3p0PooledDataSource;
    }
    
    private DataSource getBoneCPDataSource(String dataSourceName){
    	BoneCPDataSource boneCPDataSource = new BoneCPDataSource();
    	//boneCPDataSource.set
    	return boneCPDataSource;
    }
    
    /**
     * Validate and return required setting.
     * 
     * @param setting the setting
     * 
     * @return the string
     */
    private String validateAndReturnRequired(String setting)
    {
    	String value = this.config.getString(setting);
    	if (value != null)
    		return value;
    	else
    		throw new GlobalConfigurationException("Required globals property setting '"+setting+"' is not defined.");
    	
    }

    
    /**
     * This Methods looks into the current context and retrieves the specified
     * Datasource.
     * 
     * @return Returns the Datasource from the Server Context.
     */
    private DataSource getJNDIDataSource(String dataSourceName) {
        DataSource dataSource = null;
        try 
        {
        	
        	String jndiName = config.getString(MessageFormat.format(RK_DATASOURCE_JNDI_NAME, dataSourceName));
    		logger.info("Initializing '"+dataSourceName+"' data source using JNDI with name '"+jndiName+"'...");
        	
            dataSource = (DataSource) new InitialContext().lookup(jndiName);
        } 
        catch (NamingException e) {
            throw new GlobalConfigurationException("Unable to Retrieve dataSource from Context["+ dataSourceName + "]", e);
        }
        return dataSource;
    }

    /**
     * This Method returns a cached DataSource configured at the time DataSourceFactory is 
     * created.  If the datasource could not be created, of the user entered a datasource that
     * was never configured, this method throws an exception.
     * @param dsName
     * @return
     * @throws ConfigurationException  Thrown if the DataSource could not be found, or if the Datasource
     * failed the connection test. 
     * @see this{@link #testConnection(DataSource)}
     */
    public DataSource getDataSource(String dsName)  
    {
        // Data source request is not configured
        if (!instance.configuredDataSourceNames.contains(dsName))
            throw new GlobalConfigurationException("Invalid DataSource["+ dsName + "]: This data source is not specified in the globals properties configuration or is otherwise not valid.");

        DataSource returnValue = instance.validDataSourceMap.get(dsName);
        
        return returnValue;
    }

    /**
     * @see com.med.configuration.validation.Validator#validate()
     */
    public ReturnStatus validate() 
    {
        //  ReturnStatus status = new ReturnStatus();
        CompositeGlobalConfigurationException errorList = null;
        
        for (String dsName : validDataSourceMap.keySet()) {
        	try {
                testConnection(getDataSource(dsName));
        	} catch ( GlobalConfigurationException error ){
        		
        		// Add error generated by testConnection
        		if ( errorList == null ){
        			errorList = new  CompositeGlobalConfigurationException(error);
        		} else {
        			errorList.addException(error);
        		}
        		
        		// Add error stating the name of the problem data source 
        		errorList.addException( new GlobalConfigurationException("Problem with dataSource [" + dsName +"]"));
        	}
        }
        
        if ( errorList != null ){
        	throw errorList;
        }
        
        // return status;
        return new ReturnStatus();
    }


}
