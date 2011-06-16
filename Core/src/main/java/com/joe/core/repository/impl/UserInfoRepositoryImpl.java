package com.joe.core.repository.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.joe.core.domain.UserInfo;
import com.joe.core.repository.IUserInfoRepository;

/**
 * Data access object (DAO) for domain model class UserMaster.
 * 
 * @see demo.hibernate.UserMaster
 * @author MyEclipse Persistence Tools
 */

public class UserInfoRepositoryImpl extends HibernateDaoSupport implements IUserInfoRepository{
	private static final Log log = LogFactory.getLog(UserInfoRepositoryImpl.class);
	
	/**
     * Constructor for the class
     * @param hibernateTemplate
     */
	public UserInfoRepositoryImpl(HibernateTemplate hibernateTemplate)
	{
		super();
        if (hibernateTemplate == null)
            throw new IllegalArgumentException("hibernateTemplate is null");
        setHibernateTemplate(hibernateTemplate);
	}

    @SuppressWarnings("unchecked")
    public List<UserInfo> findPage(String sortColumnName, boolean sortAscending, int startRow, int maxResults) {
        try {
            String queryString = "select c from UserInfo c order by c." + sortColumnName + " " + (sortAscending ? "asc" : "desc");
            return getSession().createQuery(queryString).setFirstResult(startRow).setMaxResults(maxResults).list();
        } catch (RuntimeException re) {
            throw re;
        }
    }
	
	
	public List getPagedData(int start, int page) {
		  try {
		    Criteria criteria = getSession().createCriteria(UserInfo.class);
		    //Build Criteria object here
		    criteria.setFirstResult(start);
		    criteria.setMaxResults(page);
		    return criteria.list();
		  } catch (HibernateException hibernateException) {
		    //do something here with the exception
		  }
		  return null;
		}      
		  
		    
		    
	  public int getDataCount() {
	    Criteria criteria = getSession().createCriteria(UserInfo.class);
	    criteria.setProjection(Projections.rowCount());

	    // Build Criteria object here
	    Number nuofRecords = ((Number) criteria.uniqueResult());
	    return nuofRecords == null ? 0 : nuofRecords.intValue();
	  }      

	
}