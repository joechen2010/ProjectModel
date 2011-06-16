/**
 * Copyright (c) http://www.hao-se.cn Ltd.,2007 All  rights  reserved.
 */
package com.joe.utilities.core.hibernate.entrydao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.joe.utilities.core.data.PageRequest;
import com.joe.utilities.core.data.PageResponse;

/**
 * 泛型DAO接口。
 * 
 * 定义了泛型的保存、删除、查询方法。
 * 
 *  
 */
public interface GenericDAO<T> {

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
	public List<T> findByDetachedCriteria(final DetachedCriteria dc);
	
	
	List<T> findByProperty(String propertyName, Object value);

	PageResponse<T> findByProperty1(String propertyName, Object value,PageRequest pageRequest);
	
	PageResponse<T> findAll(PageRequest pageRequest);

	List<T> findAll();

}
