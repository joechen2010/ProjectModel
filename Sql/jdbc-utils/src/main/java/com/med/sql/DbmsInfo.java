package com.med.sql;

public class DbmsInfo {

	/**
	 * Supported database management systems.
	 */
	public static enum Product {CACHE, ORACLE, MSSQL};

	/* supported values for install4j dbms selection variables */
	public static final Integer CACHE_DBMS_INDEX = 0;
	public static final Integer ORACLE_DBMS_INDEX = 1;
	public static final Integer MSSQL_DBMS_INDEX = 2;

	/*
	 * Returns the DBMS product selected by install4j context variable.
	 */
	public static Product getSelectedProduct(Integer selectionIndex)
	throws IllegalArgumentException {
		if (CACHE_DBMS_INDEX.equals(selectionIndex))
			return Product.CACHE;
		else if (ORACLE_DBMS_INDEX.equals(selectionIndex))
			return Product.ORACLE;
		else if (MSSQL_DBMS_INDEX.equals(selectionIndex))
			return Product.MSSQL;
		else
			throw new IllegalArgumentException();
	}

	/*
	 * Returns the Hibernate dialect for the DBMS product.
	 */
	public static String getHibernateDialect(Product product) {
		String hibernateDialect = null;

		if (DbmsInfo.Product.CACHE.equals(product)) {
			hibernateDialect = "org.hibernate.dialect.Cache71Dialect";
		} else if (DbmsInfo.Product.ORACLE.equals(product)) {
			hibernateDialect = "org.hibernate.dialect.Oracle9Dialect";
		} else if (DbmsInfo.Product.MSSQL.equals(product)) {
			hibernateDialect = "org.hibernate.dialect.SQLServerDialect";
		}

		return hibernateDialect;
	}

}
