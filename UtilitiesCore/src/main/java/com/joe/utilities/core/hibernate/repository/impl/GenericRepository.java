package com.joe.utilities.core.hibernate.repository.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.joe.utilities.core.hibernate.repository.IGenericRepository;

/**
 * The Class GenericRepository. Provides repository-level access to generic
 * Hibernate template functions. This can then be called from service layer
 * instead of creating simplistic feature-specific repository methods that do
 * the same thing.
 */
public class GenericRepository extends HibernateDaoSupport implements IGenericRepository
{

	/** Commons logger. */
	private static Log log = LogFactory.getLog(GenericRepository.class);

	/**
	 * The Constructor.
	 * 
	 * @param template
	 *            the template
	 */
	public GenericRepository(HibernateTemplate template)
	{
		setHibernateTemplate(template);
	}

	/**
	 * Save object.
	 * 
	 * @param entity
	 *            the entity
	 */
	public void saveObject(Object entity)
	{
		getHibernateTemplate().save(entity);
	}
	
	/**
	 * Save all.
	 * @param entities the entities
	 */
	public void saveAll(Collection entities)
	{
		for (Object entity : entities)
		{
			getHibernateTemplate().save(entity);
		}
	}
	
	/**
	 * Update all.
	 * @param entities the entities
	 */
	public void updateAll(Collection entities)
	{
		for (Object entity : entities)
		{
			getHibernateTemplate().update(entity);
		}
	}

	/**
	 * Save or update object.
	 * 
	 * @param entity
	 *            the entity
	 */
	public void saveOrUpdateObject(Object entity)
	{
		getHibernateTemplate().saveOrUpdate(entity);
	}

	/**
	 * Delete object.
	 * 
	 * @param entity
	 *            the entity
	 */
	public void deleteObject(Object entity)
	{
		getHibernateTemplate().delete(entity);
	}

	/**
	 * Delete objects.
	 * 
	 * @param entities
	 *            the entities
	 */
	public void deleteObjects(Collection<Object> entities)
	{
		getHibernateTemplate().deleteAll(entities);
	}
	
	/**
	 * Flush hibernate session.
	 */
	public void flushHibernateSession()
	{
		getHibernateTemplate().flush();
	}

	/**
	 * Save or update all.
	 * 
	 * @param entities
	 *            the entities
	 */
	public void saveOrUpdateAll(Collection<Object> entities)
	{
		getHibernateTemplate().saveOrUpdateAll(entities);
	}

	/* (non-Javadoc)
	 * @see com.med.utilities.repository.hibernate.IGenericRepository#loadAll(java.lang.Class)
	 */
	public <T> List<T> loadAll(Class<T> entityClass)
	{
		return getHibernateTemplate().loadAll(entityClass);
	}
	
	/**
	 * @param entity
	 */
	public void evict(Object entity)
	{
		getHibernateTemplate().evict(entity);
	}
	
	/**
	 * @param evictedEntities
	 */
	public void evictAll(Collection evictedEntities)
	{
		for (Object entity : evictedEntities)
		{
			getHibernateTemplate().evict(entity);
		}
	}

	
	
}
