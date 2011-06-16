package com.med.sql;

public class JdbcPropertiesFactory {

	/**
	 * Return JDBC properties based on install4j DBMS selection context variable.
	 * @param dbmsSelection
	 * @return JdbcProperties
	 */
	public static JdbcProperties getJdbcProperties(Integer dbmsSelection) {
		return getJdbcProperties(DbmsInfo.getSelectedProduct(dbmsSelection));
	}

	/**
	 * Return JDBC properties for the selected DBMS product.
	 * @param dbmsSelection
	 * @return JdbcProperties
	 */
	public static JdbcProperties getJdbcProperties(
			DbmsInfo.Product product) {
		JdbcProperties jdbcProperties = null;

		if (DbmsInfo.Product.CACHE.equals(product)) {
			jdbcProperties = new CacheJdbcProperties();
		} else if (DbmsInfo.Product.ORACLE.equals(product)) {
			jdbcProperties = new OracleJdbcProperties();
		} else if (DbmsInfo.Product.MSSQL.equals(product)) {
			jdbcProperties = new MssqlJdbcProperties();
		}

		return jdbcProperties;
	}

}
