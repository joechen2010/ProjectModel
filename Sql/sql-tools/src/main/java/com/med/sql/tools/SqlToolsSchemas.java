package com.med.sql.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import schemacrawler.schema.Schema;

import com.med.sql.SchemaCrawlerUtils;

/**
 * Supplies schema information for sql-tools applications based on sql-tools properties.
 * 
 * @author Jane Eisenstein
 */
public class SqlToolsSchemas implements SqlToolsConstants {

	SqlToolsProperties properties = null;
	String[] targetSchemas = null;
	boolean excludeTargetSchemas = false;
	
	SqlToolsDataSource dataSource = null;
	
	Set<String> targetSchemaSet = null;
	List<Schema> schemas = null;
	
	boolean initialized = false;

	/**
	 * Initialize from sql-tools properties.
	 * @param props
	 */
	public SqlToolsSchemas(SqlToolsProperties props) {
		this.properties = props;
	}
	
	/**
	 * Bypasses the need to construct a SqlToolsProperties.
	 * @param dataSource
	 * @param targetSchemaSet
	 * @param excludeTargetSchemas
	 */
	public SqlToolsSchemas(SqlToolsDataSource dataSource, Set<String> targetSchemaSet, boolean excludeTargetSchemas) {
		this.dataSource = dataSource;
		this.targetSchemaSet = targetSchemaSet;
		this.excludeTargetSchemas = excludeTargetSchemas;
		initialized = true;
	}
	
	private void initialize() {
		if (!initialized && properties != null) {
			dataSource = new SqlToolsDataSource(properties);
			targetSchemas = properties.getStringArray(targetSchemas_key);
			targetSchemaSet = new HashSet<String>();
			for (String schema : targetSchemas) {
				targetSchemaSet.add(schema);
			}
			excludeTargetSchemas = properties.getBoolean(excludeTargetSchemas_key);
			initialized = true;
		}
	}
	
	public List<Schema> getSchemas(boolean sortColumns) {
		if (schemas == null) {
			initialize();
			SchemaCrawlerUtils scu = new SchemaCrawlerUtils();
			schemas = scu.getSchemas(
					dataSource.getJdbcProperties(),
					dataSource.getDbms(), 
					dataSource.getDataSource(), 
					targetSchemaSet, 
					excludeTargetSchemas, 
					sortColumns, 
					false, null);
		}
		return schemas;
	}
	
	public SqlToolsDataSource getDataSource() {
		if (dataSource == null)
			initialize();
		return dataSource;
	}
}
