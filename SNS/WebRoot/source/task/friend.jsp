<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
Map<String,Object> space = (Map<String,Object>)request.getAttribute("space");
Map<String,Object> task = (Map<String,Object>)request.getAttribute("task");
int friendnum = (Integer)space.get("friendnum");
if(friendnum >=5 ){
	task.put("done", 1);
}else {
	task.put("guide","<strong>请按照以下的说明来参与本任务：</strong>" +
			"<ul>" +
			"<li>1. <a href=\"cp.jsp?ac=friend&op=find\" target=\"_blank\">新窗口打开寻找好友页面</a>；</li>" +
			"<li>2. 在新打开的页面中，可以将系统自动给你找到的推荐用户加为好友，也可以自己设置条件寻找并添加为好友；</li>" +
			"<li>3. 接下来，您还需要等待对方批准您的好友申请。</li>" +
			"</ul>");
}
%>