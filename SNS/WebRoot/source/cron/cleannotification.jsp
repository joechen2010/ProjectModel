<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.jcenterhome.util.BeanFactory"%>
<%@page import="cn.jcenterhome.service.DataBaseService"%>
<%@page import="cn.jcenterhome.util.JavaCenterHome"%>
<%
Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");

int deltime = (Integer)sGlobal.get("timestamp") - 2*3600*24;
dataBaseService.execute("DELETE FROM "+JavaCenterHome.getTableName("notification")+" WHERE dateline < '"+deltime+"' AND new='0'");
dataBaseService.execute("OPTIMIZE TABLE "+JavaCenterHome.getTableName("notification"));
%>