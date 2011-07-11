<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
Map<String,Object> space = (Map<String,Object>)request.getAttribute("space");
Map<String,Object> task = (Map<String,Object>)request.getAttribute("task");
int emailcheck = (Integer)space.get("emailcheck");
if(emailcheck !=0){
	task.put("done", 1);
}else {
	task.put("guide", "<strong>请按照以下的说明来参与本任务：</strong>" +
			"<ul>" +
			"<li><a href=\"cp.jsp?ac=profile&op=contact\" target=\"_blank\">新窗口打开账号设置页面</a>；</li>" +
			"<li>在新打开的设置页面中，将自己的邮箱真实填写，并点击“验证邮箱”按钮；</li>" +
			"<li>几分钟后，系统会给你发送一封邮件，收到邮件后，请按照邮件的说明，访问邮件中的验证链接就可以了。</li>" +
			"</ul>");
}
%>