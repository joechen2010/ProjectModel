package com.joe.utilities.core.jdbc.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

 
public class OperationCoreImpl implements IOperationCore {
   protected Connection aConnection = null;
   protected Statement aStatement = null;
   protected ResultSet aResultSet = null;
   protected ResultSetMetaData rsmd = null;
   protected static OperationCoreImpl m_instance = null;
   
  
   public static OperationCoreImpl createFactory() throws Exception {
   	if(m_instance==null)
   		m_instance=new OperationCoreImpl();
   	return m_instance;
   }
   
  
   public OperationCoreImpl() throws Exception {
	   init();
   }
   
   private void init() throws Exception{
	   aConnection=ConnectionFactory.getConnection();
   }
   
  
   public void dispose() {
   	try {
   		if(aResultSet!=null)
   			aResultSet.close();
   	} catch (SQLException e) {
   		
   		e.printStackTrace();
   	}
   	try {
   		if(aStatement!=null)
   			aStatement.close();
   	} catch (SQLException e) {
   		
   		e.printStackTrace();
   	}
   	try {
   		if(aConnection!=null)
   			aConnection.close();
   	} catch (SQLException e) {
   		e.printStackTrace();
   	}
   	
   }
   
  
   public ResultSet executeQuery(String queryString) {
      		try {
   			aStatement = aConnection.createStatement();
   			aResultSet = aStatement.executeQuery(queryString);
   		} catch (SQLException e) {
   			aResultSet=null;
   			e.printStackTrace();
   		}
      	return aResultSet;
   }
   
  
   public int executeUpdate(String updateString) 	{
   		int effectedRows=0;
   		try{
   			aConnection.setAutoCommit(false);
   			aStatement = aConnection.createStatement();
   			effectedRows = aStatement.executeUpdate(updateString);
   			aConnection.commit();
   		}catch(SQLException ex){
   			System.out.println("��ݿ�д����ʧ��!");
   			if(aConnection!=null)
   			{
   				try {
   					aConnection.rollback();
   					System.out.println("JDBC����ع�ɹ�");
   				} catch (SQLException e) {
   					System.out.println("JDBC����ع�ʧ��");
   					e.printStackTrace();
   				}	
   			}
   		}
   		return effectedRows;
   	}
   
  
   public Collection<String> getColumnNames(String queryString) {
   	
   	ArrayList<String> ColumnNames=new ArrayList<String>();
   	try {
   		aResultSet=executeQuery(queryString);
   		ResultSetMetaData rsmd=aResultSet.getMetaData();
   		int j=rsmd.getColumnCount();
   		for(int k=0;k<j;k++){
   			ColumnNames.add(rsmd.getColumnName(k+1));
   		}
   	} catch (SQLException e) {
   		ColumnNames=null;
   		e.printStackTrace();
   	}
   	return ColumnNames;
   }
    
   public Collection<String> getColumnTypeNames(String queryString) {
   		
   		ArrayList<String> ColumnNames=new ArrayList<String>();
   		try {
   			aResultSet=executeQuery(queryString);
   			ResultSetMetaData rsmd=aResultSet.getMetaData();
   			int j=rsmd.getColumnCount();
   			for(int k=0;k<j;k++){
   				ColumnNames.add(rsmd.getColumnTypeName(k+1));
   			}
   		} catch (SQLException e) {
   			ColumnNames=null;
   			e.printStackTrace();
   		}
   		return ColumnNames;
   	}
   
  
   public String getColumnName(int columIndex, String queryString) 	{
   	String columnName=null;
   	try {
   		aResultSet=executeQuery(queryString);
   		rsmd=aResultSet.getMetaData();
   		columnName=rsmd.getColumnName(columIndex + 1);
   	} catch (SQLException e) {
   		e.printStackTrace();
   	}
   	return columnName;
   }
   
   
   public int getColumnCount(String queryString) {
   	int columnCount=0;
   	try {
   		aResultSet=executeQuery(queryString);
   		ResultSetMetaData rsmd=aResultSet.getMetaData();
   		columnCount = rsmd.getColumnCount();
   	} catch (SQLException e) {
   		e.printStackTrace();
   	}
      return columnCount;
   }
   
 
   public int getRowCount(String queryString) {
   	int rowCount=0;
   	try {
   		aResultSet=executeQuery(queryString);
   		while(aResultSet.next())
   			rowCount=aResultSet.getInt(1);
   	} catch (SQLException e) {
   		e.printStackTrace();
   	}
   	 return rowCount;
   }
   
 
   public Object getValueAt(int rowIndex, int columnIndex, String queryString)    {
   	Object values=null;
   	try {
   		aResultSet=executeQuery(queryString);
   		aResultSet.absolute(rowIndex + 1);
   		values=aResultSet.getObject(columnIndex + 1);
   	} catch (SQLException e) {
   		e.printStackTrace();
   	}
   	return values;
   }

}