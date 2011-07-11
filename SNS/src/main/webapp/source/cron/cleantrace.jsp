<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.jcenterhome.util.BeanFactory"%>
<%@page import="cn.jcenterhome.util.JavaCenterHome"%>
<%@page import="cn.jcenterhome.service.DataBaseService"%>
<%
Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");

int maxday = 90;
int deltime = (Integer)sGlobal.get("timestamp") - maxday*3600*24;;
dataBaseService.execute("DELETE FROM "+JavaCenterHome.getTableName("clickuser")+" WHERE dateline < '"+deltime+"'");
dataBaseService.execute("DELETE FROM "+JavaCenterHome.getTableName("visitor")+" WHERE dateline < '"+deltime+"'");
%>