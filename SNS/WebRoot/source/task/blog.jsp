<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.jcenterhome.util.Common"%>
<%
Map<String,Object> space = (Map<String,Object>)request.getAttribute("space");
Map<String,Object> task = (Map<String,Object>)request.getAttribute("task");

Map<String, Object> whereArr = new HashMap<String, Object>();
whereArr.put("uid", space.get("uid"));
String testS = Common.getCount("blog", whereArr , null);
boolean blogcount = testS != null && Integer.parseInt(testS) != 0;
if(blogcount){
	task.put("done", 1);
}else{
	task.put("guide", "<strong>请按照以下的说明来参与本任务：</strong>" +
			"<ul>" +
			"<li>1. <a href=\"cp.jsp?ac=blog\" target=\"_blank\">新窗口打开发表日志页面</a>；</li>" +
			"<li>2. 在新打开的页面中，书写自己的第一篇日志，并进行发布。</li>" +
			"</ul>");
}
%>

