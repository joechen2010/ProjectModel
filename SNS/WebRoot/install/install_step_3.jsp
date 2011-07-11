<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="install_method.jsp"%>
<%
	String message=null;
	try{
		Map<String, String> jchConfig=JavaCenterHome.jchConfig;
		if(!mysql_connect(jchConfig.get("dbHost"),jchConfig.get("dbPort"),jchConfig.get("dbName"),jchConfig.get("dbUser"),jchConfig.get("dbPw"),jchConfig.get("dbCharset"))){
			message=getErrorMessage();
		}
		String sqlFile=(String)request.getAttribute("sqlFile");
		String newSQL = sreadFile(new File(sqlFile));
		if(Common.empty(newSQL)) {
			showMessage("安装SQL语句获取失败，请确认SQL文件 "+sqlFile+" 是否存在", 0, 0, request,response);
			return;
		}
		if(!"jchome_".equals(jchConfig.get("tablePre"))){
			newSQL =newSQL.replace("jchome_",jchConfig.get("tablePre").toString());
		}
		int step=(Integer)request.getAttribute("step");
		String tableName=createTable(newSQL,jchConfig.get("dbCharset").toString());
		if(tableName==null){
			showMessage("数据表已经全部安装完成，进入下一步操作", step+1, 1, request,response);
			return;
		}else{
			showMessage("<font color=\"blue\">数据表 ("+tableName+") 自动安装失败</font><br />反馈: "+getErrorMessage()+"<br /><br />请参照 "+sqlFile+" 文件中的SQL文，自己手工安装数据库后，再继续进行安装操作<br /><br /><a href=\"?step="+step+"\">重试</a>", 0, 0, request,response);
			return;
		}
	}catch(Exception e){
		if(message==null){
			message=getErrorMessage();
			if(message==null){
				message=e.getMessage();
			}
		}
		showMessage(message, 0, 0, request,response);
	}finally{
		mysql_close();
	}
%>
