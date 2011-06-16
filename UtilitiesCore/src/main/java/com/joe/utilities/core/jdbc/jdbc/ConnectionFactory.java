package com.joe.utilities.core.jdbc.jdbc; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;

 
public abstract class ConnectionFactory {
 
   private static final String propertiesName = "using_which_db_system";
   
 
   public static Connection getConnection() throws Exception {
   	String dbSystem=null;
   	
   	Connection aConnection;  //����Ҫ���ص�aConnection����
   	
   	ResourceBundle db = ResourceBundle.getBundle("dbsystem");  //��ȡ�����ļ�
   	
   	dbSystem=db.getString(propertiesName);
   	
   	ResourceBundle rb = ResourceBundle.getBundle(dbSystem);  //��ȡ�����ļ�
   	
   	Class.forName(rb.getString("db.driver")).newInstance();  //����JDBC�������
   	
   	aConnection = DriverManager.getConnection(rb.getString("db.url"),rb.getString("db.user"),rb.getString("db.pass"));  //����aConnection����
   	
   	aConnection.setAutoCommit(false);  //���ò��Զ��ύ����
   	//aConnection.setAutoCommit(true);
   	return aConnection;  //����aConnection����
   }
   
 
   public static String getCurrentDataBaseSystem(boolean echoable) {
   	String dbSystem=null;
   	ResourceBundle rb = ResourceBundle.getBundle("dbsystem");  //��ȡ�����ļ�
   	dbSystem=rb.getString(propertiesName);
   	if (echoable)System.out.println("the database system what you using are "+dbSystem);
   	return dbSystem;
   }

}