<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.jcenterhome.service.CpService"%>
<%@page import="cn.jcenterhome.service.DataBaseService"%>
<%@page import="cn.jcenterhome.util.Common"%>
<%@page import="cn.jcenterhome.util.JavaCenterHome"%>
<%
Map<String,Object> task = (Map<String,Object>)request.getAttribute("task");
Map<String,Object> space = (Map<String,Object>)request.getAttribute("space");
Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
CpService cpService = (CpService)request.getAttribute("cpService");
boolean avatar_exists = cpService.ckavatar(sGlobal, sConfig, (Integer)space.get("uid"));

if(avatar_exists){
	DataBaseService dataBaseService = (DataBaseService)request.getAttribute("dataBaseService");
	Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
	task.put("done", 1);
	Map<String,Object> setData = new HashMap<String, Object>();
	setData.put("avatar", 1);
	Map<String,Object> whereData = new HashMap<String, Object>();
	whereData.put("uid", space.get("uid"));
	dataBaseService.updateTable("space", setData, whereData);
	String title;
	String[] wherearr = new String[4];
	wherearr[0] = "s.uid=sf.uid";
	wherearr[1] = "s.avatar='1'";
	if((Integer)space.get("sex") == 2){
		title = "帅哥";
		wherearr[2] = "sf.sex='1'";
	}else {
		title = "美女";
		wherearr[2] = "sf.sex='2'";
	}
	
	Object friendsOb = space.get("friends");
	String[] friends ;
	if(friendsOb != null){
		friends = (String[])friendsOb;
		int oldLen = friends.length;
		String[] tempSA = new String[oldLen+1];
		System.arraycopy(friends, 0, tempSA, 0, oldLen);
		friends = tempSA;
		friends[oldLen] = String.valueOf(space.get("uid"));
	}else{
		friends = new String[]{String.valueOf(space.get("uid"))};
	}
	space.put("friends", friends);
	String nouids = Common.implode(friends, ",");
	wherearr[3] = "s.uid NOT IN ("+nouids+")";
	
	List<Map<String,Object>> query = dataBaseService.executeQuery("SELECT s.uid,s.username,s.name,s.namestatus " +
			"FROM "+JavaCenterHome.getTableName("space")+" s, "+JavaCenterHome.getTableName("spacefield")+" sf " +
					"WHERE "+Common.implode(wherearr, " AND ")+" " +
							"ORDER BY s.friendnum DESC LIMIT 0,10");
	for(Map<String,Object> value : query){
		Common.realname_set(sGlobal, sConfig, sNames, (Integer)value.get("uid"), (String)value.get("username"), (String)value.get("name"), (Integer)value.get("namestatus"));
	}
	
	Common.realname_get(sGlobal, sConfig, sNames, space);
	
	int querySize = query.size();
	if(querySize > 0){
		StringBuilder result = new StringBuilder("<p>找到");
		result.append(title);
		result.append("朋友，推荐给您：</p><ul class=\"avatar_list\">");
		int vuid;
		for(int i = 0; i< querySize; i++ ){
			Map<String,Object> value = query.get(i);
			result.append("<li><div class=\"avatar48\"><a href=\"space.jsp?uid=");
			vuid = (Integer)value.get("uid");
			result.append(vuid);
			result.append("\" target=\"_blank\">");
			result.append(Common.avatar(vuid, "small", false, sGlobal, sConfig));
			result.append("</a></div><p><a href=\"space.jsp?uid=");
			result.append(vuid);
			result.append("\" target=\"_blank\" target=\"_blank\">");
			result.append(sNames.get(vuid));
			result.append("</a></p><p class=\"time\"><a href=\"cp.jsp?ac=friend&op=add&uid=");
			result.append(vuid);
			result.append("\" id=\"a_reside_friend_");
			result.append(i);
			result.append("\" onclick=\"ajaxmenu(event, this.id, 1)\">加为好友</a></p></li>");
		}
		result.append("</ul>");
		task.put("result", result.toString());
	}
} else {
	task.put("guide", "请按照以下的说明来参与本任务："+
			"<ul>"+
			"<li>1. <a href=\"cp.jsp?ac=avatar\" target=\"_blank\">新窗口打开个人头像设置页面</a>；</li>"+
			"<li>2. 在新打开的设置页面中，请选择您的照片进行上传编辑。</li>"+
			"</ul>");
}
%>