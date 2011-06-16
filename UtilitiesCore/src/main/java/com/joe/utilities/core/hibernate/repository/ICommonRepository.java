package com.joe.utilities.core.hibernate.repository;

import java.util.List;
import java.util.Map;

public interface ICommonRepository {
	
	public List<?> getList(Integer onePageRows, Integer currentPage, String hql, Object values[]);
	
	public List<?> getAll(String hql, Object[] values);
	
	public Object get(Integer id, Class obj);
	
	public Object get(Long id, Class obj);
	
	public Object get(String id, Class obj);
	
	public void delete(Integer id, Class obj);
	
	public void delete(Object object);
	
	public void save(Object object);
	
	public void saveOrUpdate(Object object);
	
	public void deleteAll(List<?> list);
	
	public List<?> getTopSize(Integer n ,String hql, Object values[]);
	
	
	public Boolean executeTransaction(Map map);
	
	public Object callProcedure(String procedure,Object[] values); 
	
	public List<?> getAllByIn(Map<Object,List>  map, Class object);
	
	public Object executeSQLBatch(final Object[] sqlList);
	
	public List<?> getListNoTran(Integer onePageRows, Integer currentPage, String hql, Object values[]);
	
	public List<?> getAllNoTran(String hql, Object[] values);

	public Integer executeInsertBatchData(final String sql, final Object[][] values, final int batchSize) ;
	
	public List<?> getListBySql(Integer onePageRows, Integer currentPage, String sql, Object values[]);
	
	public List<?> getAllBySql(String sql, Object[] values);
}
