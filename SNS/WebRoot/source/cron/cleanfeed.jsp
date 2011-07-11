<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.jcenterhome.service.DataBaseService"%>
<%@page import="cn.jcenterhome.util.BeanFactory"%>
<%@page import="cn.jcenterhome.util.JavaCenterHome"%>
<%
Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
double feedday = Double.parseDouble(sConfig.get("feedday").toString());
if(feedday < 3){
	feedday = 3;
	sConfig.put("feedday", feedday);
}
int timestamp = (Integer)sGlobal.get("timestamp");
double deltime = timestamp - feedday * 3600*24;
int f_deltime = timestamp - 3*3600*24;
dataBaseService.execute("DELETE FROM "+JavaCenterHome.getTableName("feed")+" WHERE (dateline < '"+deltime+"' AND hot=0) OR (dateline < '"+f_deltime+"' AND appid=0)");
dataBaseService.execute("OPTIMIZE TABLE "+JavaCenterHome.getTableName("feed"));
%>