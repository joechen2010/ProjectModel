/**
 * Copyright (c) http://www.hao-se.cn Ltd.,2007 All  rights  reserved.
 */
package com.joe.utilities.core.hibernate.entrydao.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.CriteriaImpl.OrderEntry;
import org.springframework.util.Assert;

import com.joe.utilities.common.util.Page;
import com.joe.utilities.core.hibernate.entrydao.BaseEntityDAO;

/**
 * 泛型实体DAO
 * 
 * 
 */
public class BaseEntityDAOImpl<T> extends BaseGenericDAO<T> implements
		BaseEntityDAO<T> {

	/**
	 * spring配置中通过构造函数注射入具体类
	 * 
	 * @param type
	 */
	public BaseEntityDAOImpl(Class<T> type) {
		super(type);
		this.type = type;
	}

	/**
	 * 取得Entity的Criteria.
	 */
	protected DetachedCriteria getEntityDetachedCriteria() {
		return DetachedCriteria.forClass(type);
	}

	/*
	 * @see cn.haose.dao.BaseEntityDAO#findBy(java.lang.String,java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<T> findBy(String name, Object value) {
		Assert.hasText(name);
		DetachedCriteria dc = getEntityDetachedCriteria();
		if (value == null) {
			dc.add((Restrictions.isNull(name)));
		} else {
			dc.add(Restrictions.eq(name, value));
		}
		return (List<T>) getHibernateTemplate().findByCriteria(dc);
	}

	/*
	 * @see cn.haose.dao.BaseEntityDAO#pagedQuery(org.hibernate.criterion.DetachedCriteria，int,int)
	 */
	@SuppressWarnings("unchecked")
	public Page pagedQuery(final DetachedCriteria dc, int pageNo, int pageSize) {

		Assert.notNull(dc, "DetachedCriteria must not be null");
		CriteriaImpl c = (CriteriaImpl) dc.getExecutableCriteria(getSession());
		// 先把Projection和OrderBy条件取出来,清空两者来执行Count操作
		Projection projection = c.getProjection();
		List<OrderEntry> orderEntries;
		Field field = null;
		boolean accessible = false;
		try {
			field = c.getClass().getDeclaredField("orderEntries");
			accessible = field.isAccessible();
			field.setAccessible(true);
			orderEntries = (List<OrderEntry>) field.get(c);
			field.set(c, new ArrayList());
			field.setAccessible(accessible);
		} catch (Exception e) {
			logger.error("BaseGenericDAO.findByCriteria方法中获得CriteriaImpl属性值时出错");
			throw new RuntimeException("服务器繁忙！");
		}
		int totalRow = ((Integer) c.setProjection(Projections.rowCount())
				.uniqueResult()).intValue();
		// 返回分页对象
		if (totalRow < 1) {
			return new Page();
		}

		// 将之前的Projection和OrderBy条件重新设回去
		c.setProjection(projection);
		if (projection == null) {
			c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		try {
			field.setAccessible(true);
			field.set(c, orderEntries);
			field.setAccessible(accessible);
		} catch (Exception e) {
			logger.error("BaseGenericDAO.findByCriteria方法中设置CriteriaImpl属性值时出错");
			throw new RuntimeException("服务器繁忙！");
		}

		int startIndex = Page.getStartOfPage(pageNo, pageSize);
		List list = c.setFirstResult(startIndex).setMaxResults(pageSize).list();
		return new Page(startIndex, totalRow, pageSize, list);
	}
}
