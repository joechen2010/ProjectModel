/**
 * Copyright (c) http://www.hao-se.cn Ltd.,2007 All  rights  reserved.
 */
package com.joe.utilities.core.hibernate.entrydao.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

import com.joe.utilities.core.data.PageRequest;
import com.joe.utilities.core.data.PageResponse;
import com.joe.utilities.core.hibernate.entrydao.GenericDAO;




/**
 * 泛型DAO父类
 * 
 * 
 * DAO异常处理原则: DAO 方法应该抛出有意义的异常。 DAO 方法不应该抛出 java.lang.Exception。<br>
 * java.lang.Exception 太一般化了。它不传递关于底层问题的任何信息。 DAO 方法不应该抛出 java.sql.SQLException 。<br>
 * SQLException 是一个低级别的 JDBC 异常。 一个 DAO 应该力争封装 JDBC 而不是将 JDBC公开给应用程序的其余部分。<br>
 * 只有在可以合理地预期调用者可以处理异常时，DAO 接口中的方法才应该抛出检查过的异常。<br>
 * 如果调用者不能以有意义的方式处理这个异常，那么考虑抛出一个未检查的(运行时)异常。 <br>
 * 如果数据访问代码捕获了一个异常，不要忽略它。 忽略捕获的异常的DAO是很难进行故障诊断的。 <br>
 * 使用链接的异常将低级别的异常转化为高级别的异常。 考虑定义标准DAO异常类。 <br>
 * Spring Framework提供了很好的一套预定义的 DAO 异常类
 * 
 * @author hao-se.cn（好·色）
 */
@SuppressWarnings("unchecked")
public class BaseGenericDAO<T> extends HibernateDaoSupport implements
		GenericDAO<T> {

	
	
	/** 日志 */
	protected final Log log = LogFactory.getLog(getClass());

	/** 型别 */
	protected Class<T> type;

	/**
	 * 提供给spring初始化的构造函数
	 */
	public BaseGenericDAO() {
		super();
	}

	/**
	 * 用于初始化对应model的构造函数
	 * 
	 * @param type
	 */
	public BaseGenericDAO(Class<T> type) {
		super();
		this.type = type;
	}


	protected String getDefalutOrderFileds() {
		return null;
	}
	
	/*
	 * @see cn.haose.dao.GenericDAO#saveEntity(java.lang.Object)
	 */
	public void saveEntity(T o) {
		getHibernateTemplate().saveOrUpdate(o);
	}

	/*
	 * @see cn.haose.dao.GenericDAO#deleteEntity(java.lang.Object)
	 */
	public void deleteEntity(T o) {
		getHibernateTemplate().delete(o);
	}

	/*
	 * @see cn.haose.dao.GenericDAO#getByID(java.lang.String)
	 */
	public T getByID(String id) {
		Object o = this.getHibernateTemplate().get(type, id);
		if (o == null) {
			throw new ObjectRetrievalFailureException(type, id);
		}
		return (T) o;
	}
	
	/*
	 * @see cn.haose.dao.GenericDAO#getByID(java.lang.String)
	 */
	public T getByID(Long id) {
		Object o = this.getHibernateTemplate().get(type, id);
		if (o == null) {
			throw new ObjectRetrievalFailureException(type, id);
		}
		return (T) o;
	}

	/*
	 * @see cn.haose.dao.GenericDAO#findByDetachedCriteria(org.hibernate.criterion.DetachedCriteria)
	 */
	public List findByDetachedCriteria(final DetachedCriteria dc) {
		return getHibernateTemplate().findByCriteria(dc);
	}
	
	
	
	public List<T> findByProperty(String propertyName, Object value) {
		if (value != null) {
			String queryString = "from " + getClass().getName()
					+ " as model where model." + propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} else {
			String queryString = "from " + getClass().getName()
					+ " as model where model." + propertyName + " is null";
			return getHibernateTemplate().find(queryString);
		}
	}

	public PageResponse<T> findByProperty1(String propertyName, Object value,
			PageRequest pageRequest) {
		PageResponse<T> pageResponse = new PageResponse<T>();
		if (value != null) {
			String queryString = "from " + getClass().getName()
					+ " as model where model." + propertyName + "= :value";
			Query query = getSession().createQuery(queryString);
			query.setEntity("value", value);

			// 取总数
			Long totalCount = getHQLReulstCount(queryString);
			pageResponse.setTotalCount(totalCount);

			query.setFirstResult(pageRequest.getStart());
			query.setMaxResults(pageRequest.getLimit());
			pageResponse.setList(query.list());

			pageResponse.setStart(pageRequest.getStart() + 1);
			pageResponse.setEnd(pageResponse.getStart()+pageResponse.getList().size() - 1);

			return pageResponse;
		} else {
			String queryString = "from " + getClass().getName()
					+ " as model where model." + propertyName + " is null";

			Query query = getSession().createQuery(queryString);

			// 取总数
			Long count = getHQLReulstCount(queryString.toString());
			pageResponse.setTotalCount(count);

			query.setFirstResult(pageRequest.getStart());
			query.setMaxResults(pageRequest.getLimit());
			
			pageResponse.setList(query.list());
			pageResponse.setStart(pageRequest.getStart() + 1);
			pageResponse.setEnd(pageResponse.getStart()+pageResponse.getList().size() - 1);
			return pageResponse;
		}
	}
	
	
	
	
	
	public PageResponse<T> findAll(PageRequest pageRequest) {
		PageResponse<T> pageResponse = new PageResponse<T>();
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM ").append(getClass().getSimpleName());
		String defalutOrderFileds = getDefalutOrderFileds();
		if (StringUtils.isNotEmpty(defalutOrderFileds)) {
			hql.append(" order by " + defalutOrderFileds);
		}

		String queryString = hql.toString();
		Query query = getSession().createQuery(queryString);

		// 取总数
		Long count = getHQLReulstCount(hql.toString());
		pageResponse.setTotalCount(count);

		// 取结果
		query.setFirstResult(pageRequest.getStart());
		query.setMaxResults(pageRequest.getLimit());
		pageResponse.setList(query.list());

		pageResponse.setStart(pageRequest.getStart() + 1);
		pageResponse.setEnd(pageResponse.getStart()+pageResponse.getList().size() - 1);
		return pageResponse;
	}

	public List<T> findAll() {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM ").append(getClass().getName());
		String defalutOrderFileds = getDefalutOrderFileds();
		if (StringUtils.isNotEmpty(defalutOrderFileds)) {
			hql.append(" order by " + defalutOrderFileds);
		}

		String queryString = hql.toString();
		return getHibernateTemplate().find(queryString);
	}

	
	/**
	 * 返回给定HQL语句的记录总条数
	 * 
	 * @param hql
	 * @return
	 */
	protected Long getHQLReulstCount(String hql) {
		String countSql = removeSelect(hql);
		countSql = removeOrders(countSql);
		countSql = removeFetchKeyword(countSql);
		countSql = " select count(*) " + countSql;
		Query query = getSession().createQuery(countSql);

		Long count = (Long) query.uniqueResult();
		return count;
	}

	/**
	 * 返回给定SQL语句的记录总条数
	 * 
	 * @param hql
	 * @return
	 */
	protected Long getSQLReulstCount(String hql) {
		String countSql = removeSelect(hql);
		countSql = removeOrders(countSql);
		countSql = removeFetchKeyword(countSql);
		countSql = " select count(*) " + countSql;
		Query query = getSession().createSQLQuery(countSql);

		Long count = (Long) query.uniqueResult();
		return count;
	}

	/**
	 * 去除select 子句，未考虑union的情况
	 * 
	 * @param hql
	 * @return
	 */
	private static String removeSelect(String hql) {
		Assert.hasText(hql);
		int beginPos = hql.toLowerCase().indexOf("from");
		Assert.isTrue(beginPos != -1, " hql : " + hql
				+ " must has a keyword 'from'");
		return hql.substring(beginPos);
	}

	/**
	 * 去除orderby 子句
	 * 
	 * @param hql
	 * @return
	 */
	private static String removeOrders(String hql) {
		Assert.hasText(hql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private static String removeFetchKeyword(String hql) {
		return hql.replaceAll("(?i)fetch", "");
	}
	
}
