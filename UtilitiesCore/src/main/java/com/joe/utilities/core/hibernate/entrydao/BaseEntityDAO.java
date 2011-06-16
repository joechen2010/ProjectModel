/**
 * Copyright (c) http://www.hao-se.cn Ltd.,2007 All  rights  reserved.
 */
package com.joe.utilities.core.hibernate.entrydao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.joe.utilities.common.util.Page;
import com.joe.utilities.core.data.PageRequest;
import com.joe.utilities.core.data.PageResponse;



/**
 * 泛型实体DAO接口。
 * 
 * 定义了泛型的保存、删除、查询方法。
 * 
 * 
 */
public interface BaseEntityDAO<T> {

	/**
	 * 保存实体
	 * 
	 * @param o
	 */
	public void saveEntity(T o);

	/**
	 * 删除实体
	 * 
	 * @param article
	 */
	public void deleteEntity(T o);

	/**
	 * 通过唯一id进行查询
	 * 
	 * @param id
	 * @return 返回ID与参数id一致的实体信息
	 */
	public T getByID(String id);
	
	/**
	 * 通过唯一id进行查询
	 * 
	 * @param id
	 * @return 返回ID与参数id一致的实体信息
	 */
	public T getByID(Long id);

	/**
	 * 根据DetachedCriteria查询
	 * 
	 * @param dc(查询条件)
	 * @return
	 */
	public List findByDetachedCriteria(final DetachedCriteria dc);

	/**
	 * 查找属性值相等的对象
	 * 
	 * @param name(属性名)
	 * @param value(属性值)
	 * @return
	 */
	public List<T> findBy(String name, Object value);

	/**
	 * 通过DetachedCriteria对象设置查询条件传入此方法进行分页数据查询,并返回分页显示需要的页面对象
	 * 
	 * @param dc(已经包装好的查询条件,分离投影和排序)
	 * @param pageNo 页号,从1开始.
	 * @param pageSize 一页的数据量
	 * @return 返回符合分页属性的信息集合
	 */
	public Page pagedQuery(final DetachedCriteria dc, int pageNo, int pageSize);
	
	List<T> findByProperty(String propertyName, Object value);

	PageResponse<T> findByProperty1(String propertyName, Object value,PageRequest pageRequest);
	
	PageResponse<T> findAll(PageRequest pageRequest);

	List<T> findAll();
}
