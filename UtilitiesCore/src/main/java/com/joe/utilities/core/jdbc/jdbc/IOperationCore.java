/**
 * ��ݿⳣ�ò����װ
 */
package com.joe.utilities.core.jdbc.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

 
public interface IOperationCore {

   ResultSet executeQuery(String queryString) throws SQLException;
 
   int executeUpdate(String updateString) throws SQLException;
  
   int getRowCount(String queryString) throws SQLException;
 
   int getColumnCount(String queryString) throws SQLException;
 
   String getColumnName(int columIndex, String queryString) throws SQLException;
 
   Collection<String> getColumnNames(String queryString) throws SQLException;
 
   Collection<?> getColumnTypeNames(String queryString) throws SQLException;
 
   Object getValueAt(int rowIndex, int columnIndex, String queryString) throws SQLException;
 
   void dispose() throws SQLException;

}