package com.joe.utilities.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

 

public class MyConnection {
	
	private static final Log log = LogFactory.getLog(MyConnection.class);
	
	public static java.sql.Connection getConnection(String driverClassName,String DB_URL , String DB_USER, String DB_PASSWORD) {
        java.sql.Connection conn = null;
		try {
			Class.forName(driverClassName);
			try {
				DriverManager.registerDriver(new net.sourceforge.jtds.jdbc.Driver());
				conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return conn;
	}
	
	
	
	
	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeStatement(Statement st) {
		try {
			if (st != null)
				st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeConnection(java.sql.Connection conn) {
		try {
			if (conn != null && !conn.isClosed() ) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void closePreparedStatement(java.sql.PreparedStatement pstmt) {
		try {
			if (pstmt != null ) {
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public   String[] getDBbaseParam(){
		String url = this.getClass().getResource("").getPath().replaceAll("%20", " ");   
	    String path = url.substring(0, url.indexOf("WEB-INF")) + "WEB-INF/jdbc.properties";   
		ConfigBundle configBundle = new ConfigBundle(path);
		String driverClassName = configBundle.getString("datasource.driverClassName");
		String DB_URL = configBundle.getString("datasource.url");
		String DB_USER = configBundle.getString("datasource.username");
		String DB_PASSWORD = configBundle.getString("datasource.password");
		return new String[]{driverClassName,DB_URL,DB_USER,DB_PASSWORD};
	}	
	
	
	//获取datasource

	public static com.mchange.v2.c3p0.ComboPooledDataSource getC3P0DS(HttpServletRequest request,String dataSourceName){
		try {
			ServletContext sc = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(sc);
			com.mchange.v2.c3p0.ComboPooledDataSource ds=(com.mchange.v2.c3p0.ComboPooledDataSource)wac.getBean(dataSourceName);
			return ds;
		} catch (Exception e) {
			return null ;
		}
	}
	
	public static BasicDataSource getBasicDS(HttpServletRequest request,String dataSourceName){
		try {
			ServletContext sc = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(sc);
			BasicDataSource ds=(BasicDataSource)wac.getBean(dataSourceName);
			return ds;
		} catch (Exception e) {
			return null ;
		}
	}
	
	
	
	public  java.sql.Connection getMyCon(int type){
			
			String url = this.getClass().getResource("").getPath().replaceAll("%20", " ");   
		    String path = url.substring(0, url.indexOf("WEB-INF")) + "WEB-INF/jdbc.properties";   
			ConfigBundle configBundle = new ConfigBundle(path);
			String driverClassName = configBundle.getString("datasource.driverClassName");
			String DB_URL = configBundle.getString("datasource.url");
			String DB_USER = configBundle.getString("datasource.username");
			String DB_PASSWORD = configBundle.getString("datasource.password");
			String driverClassName2 = configBundle.getString("datasource2.driverClassName");
			String DB_URL2 = configBundle.getString("datasource2.url");
			String DB_USER2 = configBundle.getString("datasource2.username");
			String DB_PASSWORD2 = configBundle.getString("datasource2.password");
			Connection conn = null ;
			try{
				if(type==1){
					//log.info("连接数据库：DB_URL: "+DB_URL+"DB_USER: "+DB_USER+"DB_PASSWORD:"+DB_PASSWORD);
					conn = MyConnection.getConnection(driverClassName, DB_URL, DB_USER,DB_PASSWORD);
				}else if (type==2){
					//log.info("连接数据库：DB_URL: "+DB_URL2+"DB_USER: "+DB_USER+"DB_PASSWORD:"+DB_PASSWORD2);
					conn = MyConnection.getConnection(driverClassName2, DB_URL2, DB_USER2,DB_PASSWORD2);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return conn;
		}

	
	
	public ResultSet getData(String sql,int startNo,int maxCount,Connection conn){
		ResultSet rs = null ;
		  try {
		//   conn.prepareStatement(sql,游标类型,能否更新记录);
//		      游标类型：
//		       ResultSet.TYPE_FORWORD_ONLY:只进游标
//		       ResultSet.TYPE_SCROLL_INSENSITIVE:可滚动。但是不受其他用户对数据库更改的影响。
//		       ResultSet.TYPE_SCROLL_SENSITIVE:可滚动。当其他用户更改数据库时这个记录也会改变。
//		      能否更新记录：
//		       ResultSet.CONCUR_READ_ONLY,只读
//		       ResultSet.CONCUR_UPDATABLE,可更新
		   PreparedStatement pstat = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		   //最大查询到第几条记录
		   pstat.setMaxRows(startNo+maxCount-1);
		   rs = pstat.executeQuery();
		   //将游标移动到第一条记录
		   rs.first();
		//   游标移动到要输出的第一条记录
		   rs.relative(startNo-2);
		   
		  } catch (SQLException e) {
		   e.printStackTrace();
		  }
		  return rs ;
		 }
	
		 /**
		  * 从数据库中查询所有记录，然后通过游标来获取所需maxCount条记录
		  * @param sql 传入的sql语句
		  * @param startNo 从哪一条记录开始
		  * @param maxCount 总共取多少条记录
		  */
		 public void getDataFromAll(String sql,int startNo,int maxCount,Connection conn){
		  
		  try {
		   PreparedStatement pstat = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		   ResultSet rs = pstat.executeQuery();
		   rs.first();
		   rs.relative(startNo-1);
		   int i = startNo-1;
		   while(i < startNo + maxCount-1 && !rs.isAfterLast()){
		    System.out.println(rs.getInt(1));
		    i++;
		    rs.next();
		   }
		  } catch (SQLException e) {
		   e.printStackTrace();
		  }
		 } 

	
		 public List<Object[]> getListByResultSet(ResultSet rs){
			 ResultSetMetaData rsmd = null ;
			 List<Object[]> list = new ArrayList<Object[]>();
			 try {
				rsmd = rs.getMetaData();
			 	int columnCount = rsmd.getColumnCount();
			 	while (rs.next()) {
			 		Object objList[] = new Object[columnCount];
					for (int i = 1; i <= columnCount; i++) {
						objList[i-1] = rs.getObject(i);
					}
					list.add(objList);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			 return list ;
		 }
		 
		 
		 public List getList(ResultSet rs,String[] columnArray){
				List<String[]> exportList = new ArrayList<String[]> ();
				
				try {
					while (rs.next()) {
						String[] s = new String[columnArray.length];
						for (int i = 0; i < columnArray.length; i++) {
							s[i] = rs.getObject(columnArray[i]).toString();
						}
						exportList.add(s);
					}
				} catch (Exception e) {
					System.out.println("getList ERROR:" + e);
				}finally{
					MyConnection.closeResultSet(rs);
				}
				return exportList ;
			}

		 

}
