<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.jcenterhome.util.JavaCenterHome"%>
<%@page import="cn.jcenterhome.service.DataBaseService"%>
<%
DataBaseService dataBaseService = (DataBaseService)request.getAttribute("dataBaseService");
Map<String,Object> task = (Map<String,Object>)request.getAttribute("task");
Map<String,Object> space = (Map<String,Object>)request.getAttribute("space");

int space_uid = (Integer)space.get("uid");
List<Map<String,Object>> query = dataBaseService.executeQuery("SELECT COUNT(*) AS cont FROM "+JavaCenterHome.getTableName("invite")+" WHERE uid='"+space_uid+"' AND fuid>'0'");
int count = 0;
if(query.size() > 0){
	count = (Integer)query.get(0).get("cont");
}

if(count >= 10){
	task.put("done", 1);
}else{
	String oldGuide = (String)task.get("guide");
	StringBuilder guide;
	if(oldGuide == null){
		guide = new StringBuilder();
	}else{
		guide = new StringBuilder(oldGuide);
	}
	if(count != 0){
		guide.append("<p style=\"color:red;\">哇，厉害，您现在已经邀请了 ");
		guide.append(count);
		guide.append(" 个好友了。继续努力！</p><br>");
	}
	guide.append("<strong>请按照以下的说明来完成本任务：</strong>" +
			"<ul class=\"task\">" +
			"<li>在新窗口中打开<a href=\"cp.jsp?ac=invite\" target=\"_blank\">好友邀请页面</a>；</li>" +
			"<li>通过QQ、MSN等IM工具，或者发送邮件，把邀请链接告诉你的好友，邀请他们加入进来吧；</li>" +
			"<li>您需要邀请10个好友才算完成。</li>" +
			"</ul>");
	task.put("guide", guide.toString());
}
%>

