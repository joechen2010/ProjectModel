package com.joe.utilities.core.manager.facade;

import java.util.List;
import java.util.Map;

import com.joe.utilities.core.exception.DBException;





public interface ICommonFacade {
	
	public List<?> getList(Integer onePageRows, Integer currentPage, String hql, Object values[]) throws DBException;
	
	public List<?> getAll(String hql, Object[] values) throws DBException;
	
	public Object get(Integer id, Class obj) throws DBException;
	
	public Object get(Long id, Class obj) throws DBException;
	
	public Object get(String id, Class obj) throws DBException;
	
	public void delete(Integer id, Class obj) throws DBException;
	
	public void delete(Object object) throws DBException;
	
	public void save(Object object) throws DBException;
	
	public void saveOrUpdate(Object object) throws DBException;
	
	public void deleteAll(List<?> list) throws DBException;
	
	public List<?> getTopSize(Integer n ,String hql, Object values[]) throws DBException;
	
	public Boolean executeTransaction(Map map) throws DBException;
	
	public Object callProcedure(String procedure,Object[] values)throws DBException; 
	
	public List<?> getAll(Map<Object,List>  map, Class object) throws DBException ;
	
	public Object executeSQLBatch(final Object[] sqlList) throws DBException ;
	
	public List<?> getListNoTran(Integer onePageRows, Integer currentPage, String hql, Object values[])throws DBException;
	
	public List<?> getAllNoTran(String hql, Object[] values)throws DBException;
	
	public Integer executeInsertBatchData(final String sql, final Object[][] values, final int batchSize) throws DBException;
	
	public List<?> getListBySql(Integer onePageRows, Integer currentPage, String hql, Object values[]) throws DBException;
	
	public List<?> getAllBySql(String hql, Object[] values) throws DBException;
}
