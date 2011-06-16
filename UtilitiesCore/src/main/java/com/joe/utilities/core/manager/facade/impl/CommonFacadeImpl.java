package com.joe.utilities.core.manager.facade.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joe.utilities.core.exception.DBException;
import com.joe.utilities.core.hibernate.repository.ICommonRepository;
import com.joe.utilities.core.manager.facade.ICommonFacade;
import com.joe.utilities.core.util.EvaluationException;


@Transactional(readOnly = true)
public class CommonFacadeImpl implements ICommonFacade {
	
	private Log log = LogFactory.getLog(CommonFacadeImpl.class);
	
	private ICommonRepository commonRepository;

	

	public CommonFacadeImpl(ICommonRepository commonRepository) {
		super();
		if (commonRepository == null)
			throw new IllegalArgumentException("commonRepository is null");
		this.commonRepository = commonRepository;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public void delete(Integer id, Class obj) throws DBException {
		try {
			this.getCommonRepository().delete(id, obj);
		} catch (Exception ex) {
			this.log.error("delete data by id error!", ex);
			throw new DBException(ex);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public void delete(Object object) throws DBException {
		try {
			this.getCommonRepository().delete(object);
		} catch (Exception ex) {
			this.log.error("delete data by object error!", ex);
			throw new DBException(ex);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public void deleteAll(List<?> list) throws DBException {
		try {
			this.getCommonRepository().deleteAll(list);
		} catch (Exception ex) {
			this.log.error("delete many data error!", ex);
			throw new DBException(ex);
		}
	}

	public Object get(Integer id, Class obj) throws DBException {
		try {
			return this.getCommonRepository().get(id, obj);
		} catch (Exception ex) {
			this.log.error("get data by id error!", ex);
			throw new DBException(ex);
		}
	}
	
	public Object get(Long id, Class obj) throws DBException {
		try {
			return this.getCommonRepository().get(id, obj);
		} catch (Exception ex) {
			this.log.error("get data error!", ex);
			throw new DBException(ex);
		}
	}
	
	public Object get(String id, Class obj) throws DBException {
		try {
			return this.getCommonRepository().get(id, obj);
		} catch (Exception ex) {
			this.log.error("get data error!", ex);
			throw new DBException(ex);
		}
	}

	public List<?> getAll(String hql, Object[] values) throws DBException {
		try {
			return this.getCommonRepository().getAll(hql, values);
		} catch (Exception ex) {
			this.log.error("get all data error!", ex);
			throw new DBException(ex);
		}
	}

	public List<?> getList(Integer onePageRows, Integer currentPage, String hql, Object[] values) throws DBException {
		try {
			return this.getCommonRepository().getList(onePageRows, currentPage, hql, values);
		} catch (Exception ex) {
			this.log.error("get many data by page error!", ex);
			throw new DBException(ex);
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public void save(Object object) throws DBException {
		try {
			this.getCommonRepository().saveOrUpdate(object);
		} catch (Exception ex) {
			this.log.error("saveOrUpdate data error!", ex);
			throw new DBException(ex);
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public void saveOrUpdate(Object object) throws DBException {
		try {
			this.getCommonRepository().saveOrUpdate(object);
		} catch (Exception ex) {
			this.log.error("saveOrUpdate data error!", ex);
			throw new DBException(ex);
		}
	}
	
	public List<?> getTopSize(Integer n ,String hql, Object values[]) throws DBException{
		try {
			return this.getCommonRepository().getTopSize(n, hql, values);
		} catch (Exception ex) {
			this.log.error("get top size data error!", ex);
			throw new DBException(ex);
		}
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public Boolean executeTransaction(Map map) throws DBException{
		try {
			return this.getCommonRepository().executeTransaction(map);
		} catch(Exception ex) {
			this.log.error("get data error at method executeQuery!", ex);
			throw new DBException(ex);
		}
	}
	
	public List<?> getAll(Map<Object,List>  map, Class object) throws DBException {
		try {
			return this.getCommonRepository().getAllByIn(map, object);
		} catch (Exception ex) {
			this.log.error("get all data error!", ex);
			throw new DBException(ex);
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public Object callProcedure(String procedure,Object[] values)throws DBException {
		try {
			 return this.getCommonRepository().callProcedure(procedure, values);
		} catch (Exception ex) {
			this.log.error(" call procedure  error!", ex);
			throw new DBException(ex);
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public Object executeSQLBatch(final Object[] sqlList) throws DBException  {
		try {
			 return this.getCommonRepository().executeSQLBatch(sqlList);
		} catch (Exception ex) {
			this.log.error(" executeBetchSQL data error!", ex);
			throw new DBException(ex);
		}
	}
	
	public List<?> getAllNoTran(String hql, Object[] values) throws DBException {
		try {
			return this.getCommonRepository().getAllNoTran(hql, values);
		} catch (Exception ex) {
			this.log.error("get all data by getAllNoTran error!", ex);
			throw new DBException(ex);
		}
	}

	public List<?> getListNoTran(Integer onePageRows, Integer currentPage, String hql, Object[] values) throws DBException {
		try {
			return this.getCommonRepository().getListNoTran(onePageRows, currentPage, hql, values);
		} catch (Exception ex) {
			this.log.error("get many data by getListNoTran error!", ex);
			throw new DBException(ex);
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = { EvaluationException.class })
	public Integer executeInsertBatchData(final String sql, final Object[][] values, final int batchSize) throws DBException{
		try {
			return this.getCommonRepository().executeInsertBatchData(sql, values, batchSize);
		} catch (Exception ex) {
			this.log.error(" executeInsertBatch  error!", ex);
			throw new DBException(ex);
		}
	}
	
	public List<?> getListBySql(Integer onePageRows, Integer currentPage, String sql, Object values[]) throws DBException{
		try {
			return this.getCommonRepository().getListBySql(onePageRows, currentPage, sql, values);
		} catch (Exception ex) {
			this.log.error(" getListBySql  error!", ex);
			throw new DBException(ex);
		}
	}
	
	public List<?> getAllBySql(String sql, Object[] values) throws DBException{
		try {
			return this.getCommonRepository().getAllBySql(sql, values);
		} catch (Exception ex) {
			this.log.error(" getAllBySql  error!", ex);
			throw new DBException(ex);
		}
	}

	public ICommonRepository getCommonRepository() {
		return commonRepository;
	}

	public void setCommonRepository(ICommonRepository commonRepository) {
		this.commonRepository = commonRepository;
	}

	
	
}
