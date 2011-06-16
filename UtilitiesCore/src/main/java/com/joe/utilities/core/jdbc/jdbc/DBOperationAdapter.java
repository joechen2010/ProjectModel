package com.joe.utilities.core.jdbc.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

 
public class DBOperationAdapter extends ConnectionFactory {
   private static IOperationCore objIOperationCore = null;
   private static DBOperationAdapter m_instance = null;
   
   private DBOperationAdapter() {
   	try {
   		objIOperationCore=OperationCoreImpl.createFactory();
   	} catch (Exception e) {
   		e.printStackTrace();
   	}
   }
   
   public static DBOperationAdapter getInstance() {
   	if(m_instance==null)
   		m_instance=new DBOperationAdapter();
   	return m_instance;
   }
   
 
   public ResultSet executeQuery(String queryString) throws SQLException {
   	return objIOperationCore.executeQuery(queryString);
   }
   
 
   public int executeUpdate(String updateString) throws SQLException {
    return objIOperationCore.executeUpdate(updateString);
   }
   
 
   public int executeDelete(String deleteString) throws SQLException {
    return objIOperationCore.executeUpdate(deleteString);
   }
   
 
   public int executeInsert(String insertString) throws SQLException {
    return objIOperationCore.executeUpdate(insertString);
   }
   
 
   public int getRowCount(String queryString) throws SQLException {
   	return objIOperationCore.getRowCount(queryString);
   }
   
 
   public int getColumnCount(String queryString) throws SQLException {
    return objIOperationCore.getColumnCount(queryString);
   }
   
 
   public String getColumnName(int columIndex, String queryString) throws SQLException {
      return objIOperationCore.getColumnName(columIndex, queryString);
     }
   
 
   public Collection<?> getColumnTypeNames(String queryString) throws SQLException {
    return objIOperationCore.getColumnTypeNames(queryString);
   }
   
 
   public Collection<String> getColumnNames(String queryString) throws SQLException {
    return objIOperationCore.getColumnNames(queryString);
   }
   
 
   public Object getValueAt(int rowIndex, int columnIndex, String queryString) throws SQLException {
    return objIOperationCore.getValueAt(rowIndex, columnIndex,queryString);
   }
   
    
   public void dispose() throws SQLException {
    objIOperationCore.dispose();
   }

}