package com.joe.utilities.core.hibernate.repository;

import java.util.List;

public interface IGenericRepository {

	/**
	 * Load all.
	 * 
	 * @param entityClass the entity class
	 */
	public abstract <T> List<T> loadAll(Class<T> entityClass);

}