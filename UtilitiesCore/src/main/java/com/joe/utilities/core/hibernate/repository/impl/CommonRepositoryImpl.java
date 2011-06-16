package com.joe.utilities.core.hibernate.repository.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.joe.utilities.common.util.MyConnection;
import com.joe.utilities.core.hibernate.repository.ICommonRepository;



public class CommonRepositoryImpl extends HibernateDaoSupport implements ICommonRepository{
	
	private static final Log log = LogFactory.getLog(CommonRepositoryImpl.class);
	private JdbcTemplate jdbcTemplate;
    
	/**
     * Constructor with dependent hibernate session object.
     * @param hibernateTemplate the hibernate template object.
     */
    public CommonRepositoryImpl(HibernateTemplate hibernateTemplate)
    {
    	if (hibernateTemplate == null)
            throw new IllegalArgumentException("hibernateTemplate is null");

    	setHibernateTemplate(hibernateTemplate);
    }
	

	public void delete(Integer id, Class obj) {
		this.getHibernateTemplate().delete(this.getHibernateTemplate().get(obj, id) );
	}

	public void delete(Object object) {
		this.getHibernateTemplate().delete(object);
	}

	public Object get(Integer id, Class obj) {
		return this.getHibernateTemplate().get(obj, id);
	}
	
	public Object get(Long id, Class obj) {
		return this.getHibernateTemplate().get(obj, id);
	}
	
	public Object get(String id, Class obj) {
		return this.getHibernateTemplate().get(obj, id);
	}
	
	public void save(Object object){
		 this.getHibernateTemplate().saveOrUpdate(object);
	}
	
	
	public void saveOrUpdate(Object object) {
		this.getHibernateTemplate().saveOrUpdate(object);
	}

	public void deleteAll(List<?> list) {
		this.getHibernateTemplate().deleteAll(list);
	}

	public List<?> getAll(String hql, Object[] values) {
		return values == null ? this.getHibernateTemplate().find(hql) : this.getHibernateTemplate().find(hql, values);
	}

	public List<?> getList(final Integer onePageRows, final Integer currentPage, final String hql, final Object[] values) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				if (values != null) {
					for (int i = 0; i < values.length; i++) 
						query.setParameter(i, values[i]);
				}
				return query.setFirstResult((currentPage - 1) * onePageRows).setMaxResults(onePageRows).list();
			}
		});
	}
	
	public List<?> getTopSize(final Integer n ,final String hql, final Object values[]){
		return this.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				if (values != null) {
					for (int i = 0; i < values.length; i++) 
						query.setParameter(i, values[i]);
				}
				return query.setFirstResult(0).setMaxResults(n).list();
			}
		}); 
	}
	

	public Object executeSQLBatch(final Object[] sqlList){
		return getHibernateTemplate().execute( new HibernateCallback(){
			public Object doInHibernate(Session session)
			throws HibernateException, SQLException {
					Boolean result = false ;
					Connection CurConn = session.connection();
					CurConn.setAutoCommit(false);
					String sql ="";
					
					try{
						for(int i=0;i<sqlList.length ; i ++){
							sql = sqlList[i]== null ? "" : sqlList[i].toString();
							System.out.println("第"+i+"条sql:>>"+sql);
							if(sql.equals(""))
								continue ;
							PreparedStatement ps = CurConn.prepareStatement(sql);
							ps.execute();
							ps.close();
						}
						 CurConn.commit();
						 session.flush();
						 result = true;
					}catch(Exception e){
						System.out.println("执行["+sql+"]出错...");
						System.out.println("执行回滚.....");
						log.info("执行["+sql+"]出错...");
						log.info("执行回滚.....");
						CurConn.rollback();
						result = false ; 
						log.info(e.toString());
						e.printStackTrace();
						//throw e;
						
					}finally{
						if (CurConn != null) {
							CurConn.close();
							CurConn = null ;
						}
					}
					return result ;
					}
			} );
	}

	
	
	public Boolean executeTransaction(Map map){
		
		 boolean tranResult = false ;
		 try{
			 Iterator it = map.entrySet().iterator(); 
			 while   (it.hasNext()) {   
				  Map.Entry   entry   =   (Map.Entry)   it.next()   ;   
				  Object   key   =   entry.getKey();   
				  Object   value   =   entry.getValue();  
				  if(value.equals("save")){
					  this.getHibernateTemplate().save(key);
				  }else if( value.equals("update")){
					  this.getHibernateTemplate().update(key);
				  }else if( value.equals("delete")){
					  this.getHibernateTemplate().delete(key);
				  }
			  }
			 tranResult = true ;
		 }catch(Exception e){
			 tranResult = false ;
			 e.printStackTrace();
		 }
		  return tranResult ;
	}
	
	public List<?> getAllByIn(Map<Object,List>  map, Class object) {
		Iterator it = map.entrySet().iterator(); 
		DetachedCriteria dc = DetachedCriteria.forClass(object);
		 while   (it.hasNext()) {   
			  Map.Entry   entry   =   (Map.Entry)   it.next()   ;   
			  String   key   =   entry.getKey().toString();   
			  List   value   =(List)entry.getValue();  
			  dc.add(Restrictions.in(key, value));
		 }
		 return this.getHibernateTemplate().findByCriteria(dc);
	}
		
	public Object callProcedure(String procedure,Object[] values){

		Boolean tranResult = false ;
		SessionFactory sf  =null;
		Session session =null;
		ResultSet rs = null ;
		CallableStatement cstmt = null ;
		Connection con = null ;
		String key = "";
		String value = "";
		List<Object[]> list = new ArrayList<Object[]>();
		List resultList = new ArrayList();
		try{			
			 	sf = this.getHibernateTemplate().getSessionFactory();  
			 	session = sf.openSession();  
			 	session.beginTransaction(); 
			 	con=session.connection();
			 	cstmt = con.prepareCall(procedure);
			 	if(values != null){
			 		
			 		for(int i = 0 ; i< values.length; i++){
			 			
			 			ArrayList tempList = (ArrayList<Object>)values[0];
			 			for (int j = 0; j < tempList.size(); j++) {
			 				Object[] arrStr =(Object[])tempList.get(j);
			 				key = arrStr[0] == null ? "" : arrStr[0].toString();
			 				value = arrStr[1] == null ? "" : arrStr[1].toString();
			 				if (key.equals("int")) {
								cstmt.setInt(j+1, Integer.parseInt(value));
							}else if( key.equals("string")){
								  cstmt.setString(j+1, value.toString());
							  }else if( key.equals("long")){
								  cstmt.setLong(j+1, Long.parseLong(value.toString()));
							  }
						}
			 		}
			 	}
			 	
			 	rs = cstmt.executeQuery();
			 	java.sql.ResultSetMetaData rsmd = rs.getMetaData();
			 	int columnCount = rsmd.getColumnCount();
			 	while (rs.next()) {
			 		Object objList[] = new Object[columnCount];
					for (int i = 1; i <= columnCount; i++) {
						objList[i-1] = rs.getObject(i);
					}
					list.add(objList);
				}
			    session.getTransaction().commit();
			    tranResult = true;
		}catch(Exception e){
			session.beginTransaction().rollback();
			tranResult = false;
			list = null ;
			log.info(e.toString());
		}finally{
			try {
				if(rs != null){
					rs.close();
					rs = null ;
				}
				if (cstmt != null) {
					cstmt.close();
					cstmt = null ;
				}
				if (con !=null) {
					con.close();
					con = null ;
				}
				if (session != null) {
					session.flush();
					session.close();
				}
				if (sf != null) {
					sf.close();
				}
			} catch (Exception e) {
				tranResult = false;
			}
			
		}
		resultList.add(tranResult);
		resultList.add(list);
		return resultList;
	}
	
	
	public List<?> getAllNoTran(String hql, Object[] values) {
		return values == null ? this.getHibernateTemplate().find(hql) : this.getHibernateTemplate().find(hql, values);
	}

	public List<?> getListNoTran(final Integer onePageRows, final Integer currentPage, final String hql, final Object[] values) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				if (values != null) {
					for (int i = 0; i < values.length; i++) 
						query.setParameter(i, values[i]);
				}
				return query.setFirstResult((currentPage - 1) * onePageRows).setMaxResults(onePageRows).list();
			}
		});
	}
	

	
	public Integer executeInsertBatchData(final String sql, final Object[][] values, final int batchSize) {
		return Integer.parseInt(this.getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						int swayRowsCount = 0;
						Connection conn = null;
						PreparedStatement ps = null;
						try {
							conn = session.connection();
							conn.setAutoCommit(false);
							ps = conn.prepareStatement(sql);
							if (values != null) {
								for (int i = 0; i < values.length; i++) {
									for (int j = 0; j < values[i].length; j++) {
										ps.setObject(j + 1, values[i][j]);
									}
									ps.addBatch();
									if ((i + 1) % batchSize == 0) {
										swayRowsCount += ps.executeBatch().length;
									}
								}
								if (values.length % batchSize != 0) {
									swayRowsCount += ps.executeBatch().length;
									
								}
							}
							else {
								swayRowsCount = ps.executeBatch().length;
							}
							conn.commit();
						} catch (Exception ex) {
								conn.rollback();
								throw new SQLException(ex.toString());
						} finally {
							if (ps != null) ps.close();
							if (conn != null && !conn.isClosed()) conn.close();
							if (session != null) session.clear();
							if (session != null && session.isOpen()) session.close();
						}
						return swayRowsCount;
					}
				}).toString());
	}
	
	
	public List<?> getListBySql(Integer onePageRows, Integer currentPage, String sql, Object values[]){
		  List list = null ;
		  Session session = getSession(); 
	      Connection connection = null; 
	      ResultSet rs = null ;
	      try{  
	    	  connection = session.connection();
	    	  MyConnection m = new MyConnection();
		      Statement stmt = connection.createStatement(); 
		      System.out.println(sql);
		      rs = stmt.executeQuery(sql);  
		      rs =  m.getData(sql, onePageRows*(currentPage-1)+1, onePageRows, connection);
		      list = m.getListByResultSet(rs);
		      session.flush() ;
	      }catch(Exception e){ 
	    	  
	      } finally{
	    	  try {
	    		  rs.close(); 
	    		  connection.close();
		    	  rs = null ;
		    	  connection = null ;
			  } catch (Exception e) {
				// TODO: handle exception
			 }
	    	  
	      }
	      return list; 
	}
	
	
	public List<?> getAllBySql(String sql, Object[] values){
		List list = null ;
		  Session session = getSession(); 
	      Connection connection = null; 
	      ResultSet rs = null ;
	      try{  
	    	  connection = session.connection();
	    	  MyConnection m = new MyConnection();
		      Statement stmt = connection.createStatement(); 
		      rs = stmt.executeQuery(sql);  
		      list =  m.getListByResultSet(rs);
		      session.flush() ;
	      }catch(Exception e){ 
	    	  
	      } finally{
	    	  try {
	    		  rs.close(); 
	    		  connection.close();
		    	  rs = null ;
		    	  connection = null ;
			  } catch (Exception e) {
				// TODO: handle exception
			 }
	    	  
	      }
	      return list; 
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	

}
