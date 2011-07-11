<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.jcenterhome.util.Common"%>
<%@page import="cn.jcenterhome.service.DataBaseService"%>
<%@page import="cn.jcenterhome.util.JavaCenterHome"%>
<%
Map<String,Object> task = (Map<String,Object>)request.getAttribute("task");
Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");

int supe_uid = (Integer)sGlobal.get("supe_uid");
if(supe_uid != 0){
	Map<String,Object> space = (Map<String,Object>)request.getAttribute("space");
	Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
	DataBaseService dataBaseService = (DataBaseService)request.getAttribute("dataBaseService");
	task.put("done", 1);
	StringBuilder builder = new StringBuilder("<p>给您送上一份 《热门日志导读》 看看吧：</p><br><ul class=\"line_list\">");
	Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");

	List<Map<String,Object>> query = dataBaseService.executeQuery("SELECT * FROM "+JavaCenterHome.getTableName("blog")+" WHERE hot>='3' AND friend='0' ORDER BY dateline DESC LIMIT 0,20");
	
	for(Map<String,Object> value : query){
		Common.realname_set(sGlobal, sConfig, sNames, (Integer)value.get("uid"), (String)value.get("username"), "", 0);
	}
	Common.realname_get(sGlobal, sConfig, sNames, space);
	int uid;
	for(Map<String,Object> value : query){
		uid = (Integer) value.get("uid");
		builder.append("<li><a href=\"space.jsp?uid=");
		builder.append(uid);
		builder.append("\" target=\"_blank\"><strong>");
		builder.append(sNames.get(uid));
		builder.append("</strong></a>：<a href=\"space.jsp?uid=");
		builder.append(uid);
		builder.append("&do=blog&id=");
		builder.append(value.get("blogid"));
		builder.append("\" target=\"_blank\">");
		builder.append(value.get("subject"));
		builder.append("</a> <span class=\"gray\">(");
		builder.append(value.get("hot"));
		builder.append("人推荐)</span></li>");
	}
	builder.append("</ul>");
	task.put("result", builder.toString());
}else{
	task.put("guide", "");
}
%>