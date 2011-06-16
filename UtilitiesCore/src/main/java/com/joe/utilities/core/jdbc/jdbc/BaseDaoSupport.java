package com.joe.utilities.core.jdbc.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.joe.utilities.core.exception.DBException;




/**
 * 
 * JDBC的数据封装类
 * 实现了增删改查的基本操作。
 */

public class BaseDaoSupport {
	private boolean result=false;
	Connection conn = DataBaseConnection.getConnection("","","","");
	Statement stmt = DataBaseConnection.createStatement(conn);
	ResultSet rs = null;
	/**
	 * 插入增加的方法
	 * @param entityTable 要操作的表的名称
	 * @param propertys 操作的表的属性名称
	 * @param params 增加的值
	 * @return 成功与否
	 * @throws SQLException
	 */
	public  boolean  addObject(String entityTable,String []propertys,Object[]params) throws SQLException {
		StringBuffer insertSql =new StringBuffer("");
		if(null !=entityTable &&!entityTable.equals("")) {
			insertSql.append("INSERT INTO  ");
		}else {
			return result;
		}
		if(null !=propertys && propertys.length >=1) {
			insertSql.append(entityTable.toLowerCase());
			insertSql.append("  (");
			 for(int i= 0 ; i<propertys.length;i++) {
				 insertSql.append(propertys[i]);
				 insertSql.append(",");
				 if(propertys[i].trim().toLowerCase().equals(entityTable.toLowerCase()+"id") && propertys[i]!=null) {
					 DBException oie =  new DBException("增加方法中的ObjectId的值必须是‘null’值causeby：（主键不能有重复值）");
					 
				 }
			 }
			 insertSql = new StringBuffer(insertSql.substring(0, insertSql.length()-1));
			 insertSql.append(" )");
		}
		if(null !=params) {
			insertSql.append(" VALUES (");
			 for(int i= 0 ; i<propertys.length;i++) {
				 if(params[i] != null) {
					 insertSql.append("'").append(params[i]).append("',");
				 }else {
					 insertSql.append(params[i]).append(",");
				 }
			 }
			 insertSql = new StringBuffer(insertSql.substring(0, insertSql.length()-1));
			 insertSql.append(" ) ;");
		}
		System.out.println(insertSql.toString());
		result =DataBaseConnection.executeUpdate(conn, insertSql.toString())>0 ? true :false;
		return result;
	}
	/**
	 * 删除操作
	 * @param entityTable
	 * @param objectId
	 * @return
	 * @throws SQLException
	 */
	public  boolean  delObject(String entityTable,String sqlWhere) throws SQLException {
		StringBuffer deleteSql = new StringBuffer(""); 
		deleteSql.append("DELETE FROM  ");
		deleteSql.append(entityTable.toLowerCase());
		if(null != sqlWhere && !sqlWhere.equals("") &&sqlWhere.length()>1) {
			if(sqlWhere.trim().split(" ")[0].toLowerCase().equals("where")){
				deleteSql.append(sqlWhere);
			}else {
				deleteSql.append(" WHERE " ).append(sqlWhere);
			}
		}
		System.out.println(deleteSql.toString());
		result =DataBaseConnection.executeUpdate(conn, deleteSql.toString())>0 ? true :false;
		return result;
	}
	/**
	 * 更新修改的方法
	 * @param entityTable 实体表名称
	 * @param propertys 更新的字段名称
	 * @param params 更新的值
	 * @param sqlWhere 条件语句
	 * @return 返回成功与否
	 * @throws SQLException
	 */
	public  boolean  modifyObject(String entityTable,String []propertys,Object[]params,String sqlWhere) throws SQLException {
		StringBuffer updateSql =new StringBuffer("");
		if(null !=entityTable &&!entityTable.equals("")) {
			updateSql.append("UPDATE ").append(entityTable.toLowerCase());
		}else {
			return result;
		}
		if(null !=propertys ) {
			if(null !=params) {
				if(propertys.length == params.length) {
					for(int i= 0 ; i<propertys.length;i++) {
						updateSql.append("  SET ");
						updateSql.append(propertys[i]).append("=").append("'").append(params[i]).append("',");
						 if(propertys[i].trim().toLowerCase().equals(entityTable.toLowerCase()+"id")) {
							 DBException oie =  new DBException("修改方法中的ObjectId的值不能被更新causeBy（主键唯一性）");
							 //throw oie;
						 }
					}
					
					updateSql = new StringBuffer(updateSql.substring(0, updateSql.length()-1));
					
					if(null != sqlWhere && !sqlWhere.equals("") &&sqlWhere.length()>1) {
						if(sqlWhere.trim().split(" ")[0].toLowerCase().equals("where")){
							updateSql.append(sqlWhere);
						}else {
							updateSql.append(" WHERE " ).append(sqlWhere);
						}
					}
				}else{
					return result;
				}
			}
		}
		result =DataBaseConnection.executeUpdate(conn, updateSql.toString())>0 ? true :false;
		System.out.println(updateSql.toString());
		return result;
	}
	
	/**
	 * 查询方法
	 * @param entityTable 要操作的表的名称
	 * @param propertys 表中所还有的属性，字段的名称,也就是主要关心的字段的名称
	 * @param sqlWhere 是附加的查询条件，可以含有where 关键字，也可以不含有where关键字均可
	 * @return 查到的ResultSet结果集
	 * @throws SQLException
	 */
	public  ResultSet  queryObject(String entityTable,Object []propertys,String sqlWhere) throws SQLException {
		StringBuffer querySql = new StringBuffer("");
		if(null !=entityTable &&!entityTable.equals("") &&(null == propertys ||propertys.length<1)) {
			querySql.append("SELECT * FROM ").append(entityTable.toLowerCase());
		}else if(null !=entityTable &&!entityTable.equals("") &&(null != propertys ||propertys.length>=1)) {
			if(null !=propertys && propertys.length >=1) {
				querySql.append("SELECT  ");
				 for(int i= 0 ; i<propertys.length;i++) {
					 querySql.append(propertys[i]).append(",");
				 }
				 querySql = new StringBuffer(querySql.substring(0, querySql.length()-1));
			}
			querySql.append(" FROM ").append(entityTable.toLowerCase());
		}
		if(null != sqlWhere && !sqlWhere.equals("") &&sqlWhere.length()>1) {
			if(sqlWhere.trim().split(" ")[0].toLowerCase().equals("where")){
				querySql.append(sqlWhere);
			}else {
				querySql.append(" WHERE " ).append(sqlWhere);
			}
		}
		System.out.println(querySql);
		rs = DataBaseConnection.executeQuery(stmt, querySql.toString());
		return rs ;
	}
}
