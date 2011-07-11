<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.jcenterhome.util.Common"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="cn.jcenterhome.service.DataBaseService"%>
<%@page import="cn.jcenterhome.util.JavaCenterHome"%>
<%
Map<String,Object> space = (Map<String,Object>)request.getAttribute("space");
Map<String,Object> task = (Map<String,Object>)request.getAttribute("task");
List<String> nones = new ArrayList<String>();
Map<String,String> profile_lang = new HashMap<String, String>();
profile_lang.put("name", "姓名");
profile_lang.put("sex", "性别");
profile_lang.put("birthyear", "生日(年)");
profile_lang.put("birthmonth", "生日(月)");
profile_lang.put("birthday", "生日(日)");
profile_lang.put("blood", "血型");
profile_lang.put("marry", "婚恋状态");
profile_lang.put("birthprovince", "家乡(省)");
profile_lang.put("birthcity", "家乡(市)");
profile_lang.put("resideprovince", "居住地(省)");
profile_lang.put("residecity", "居住地(市)");

String[] sa = {"name","sex","birthyear","birthmonth","birthday","marry","birthprovince","birthcity","resideprovince","residecity"};
for(String key : sa){
	Object value = space.get(key);
	if(value instanceof String){
		value = ((String)value).trim();
	}
	if(Common.empty(value)){
		nones.add(profile_lang.get(key));
	}
}
Map<Integer,Map<String,Object>> globalProfilefield = Common.getCacheDate(request, response, "/data/cache/cache_profilefield.jsp", "globalProfilefield");
Integer required;
for(Map.Entry<Integer, Map<String,Object>> entry : globalProfilefield.entrySet()){
	Integer field = entry.getKey();
	Map<String,Object> value = entry.getValue();
	required = (Integer)value.get("required");
	if(required != null && required != 0 && Common.empty(space.get("field_"+field))){
		nones.add((String)value.get("title"));
	}
}

if(Common.empty(nones)){
	Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
	Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
	Map<Integer, String> sNames = (Map<Integer, String>) request.getAttribute("sNames");
	
	DataBaseService dataBaseService = (DataBaseService)request.getAttribute("dataBaseService");
	
	task.put("done", 1);
	int findmaxnum = 10;
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
	List<Map<String,Object>> residelist;
	String[] warr = new String[2];
	warr[0] = "sf.resideprovince='"+Common.addSlashes((String)space.get("resideprovince"))+"'";
	warr[1] = "sf.residecity='"+Common.addSlashes((String)space.get("residecity"))+"'";
	List<Map<String,Object>> query = dataBaseService.executeQuery("SELECT s.uid,s.username,s.name,s.namestatus FROM "+JavaCenterHome.getTableName("spacefield")+" sf " +
			"LEFT JOIN "+JavaCenterHome.getTableName("space")+" s ON s.uid=sf.uid " +
			"WHERE "+Common.implode(warr, " AND ")+" AND sf.uid NOT IN ("+nouids+")" +
			"LIMIT 0,"+findmaxnum);
	for(Map<String,Object> value : query){
		Common.realname_set(sGlobal, sConfig, sNames, (Integer)value.get("uid"), (String)value.get("username"), (String)value.get("name"), (Integer)value.get("namestatus"));
	}
	residelist = query;
	List<Map<String,Object>> sexlist;
	List<String> warrList = new ArrayList<String>();
	Integer marry = (Integer)space.get("marry");
	if(Common.empty(marry) || marry < 2){
		warrList.add("sf.marry='1'");
	}
	Integer sex = (Integer)space.get("sex");
	if(Common.empty(sex) || sex < 2 ){
		warrList.add("sf.sex='2'");
	}else{
		warrList.add("sf.sex='1'");
	}
	query = dataBaseService.executeQuery("SELECT s.uid,s.username,s.name,s.namestatus FROM "+JavaCenterHome.getTableName("spacefield")+" sf " +
			"LEFT JOIN "+JavaCenterHome.getTableName("space")+" s ON s.uid=sf.uid " +
			"WHERE "+Common.implode(warrList, " AND ")+" AND sf.uid NOT IN ("+nouids+") " +
			"LIMIT 0,"+findmaxnum);
	for(Map<String,Object> value : query){
		Common.realname_set(sGlobal, sConfig, sNames, (Integer)value.get("uid"), (String)value.get("username"), (String)value.get("name"), (Integer)value.get("namestatus"));
	}
	sexlist = query;
	
	Common.realname_get(sGlobal, sConfig, sNames, space);
	
	int listSize = residelist.size();
	if(listSize > 0){
		String tempS = (String)task.get("result");
		StringBuilder result = tempS != null ? new StringBuilder(tempS) : new StringBuilder();
		result.append("<p>为您找到同城的会员，赶快加为好友吧：</p><ul class=\"avatar_list\">");
		Integer vuid;
		for(int i = 0;i<listSize;i++){
			Map<String,Object> value = residelist.get(i);
			vuid = (Integer)value.get("uid");
			result.append("<li><div class=\"avatar48\"><a href=\"space.jsp?uid=");
			result.append(vuid);
			result.append("\" target=\"_blank\">");
			result.append(Common.avatar(vuid, "small",false,sGlobal, sConfig));
			result.append("</a></div><p><a href=\"space.jsp?uid=");
			result.append(vuid);
			result.append("\" target=\"_blank\">");
			result.append(sNames.get(vuid));
			result.append("</a></p><p><a href=\"cp.jsp?ac=friend&op=add&uid=");
			result.append(vuid);
			result.append("\" id=\"a_reside_friend_");
			result.append(i);
			result.append("\" onclick=\"ajaxmenu(event, this.id, 1)\">加为好友</a></p></li>");
		}
		result.append("</ul>");
		task.put("result", result.toString());
	}
	listSize = sexlist.size();
	if(listSize > 0){
		String tempS = (String)task.get("result");
		StringBuilder result = tempS != null ? new StringBuilder(tempS) : new StringBuilder();
		result.append("<p>为您找到异性热门会员，赶快加为好友吧：</p><ul class=\"avatar_list\">");
		Integer vuid;
		for(int i = 0 ;i<listSize;i++){
			Map<String,Object> value = sexlist.get(i);
			vuid = (Integer)value.get("uid");
			result.append("<li><div class=\"avatar48\"><a href=\"space.jsp?uid=");
			result.append(vuid);
			result.append("\" target=\"_blank\">");
			result.append(Common.avatar(vuid, "small",false,sGlobal, sConfig));
			result.append("</a></div><p><a href=\"space.jsp?uid=");
			result.append(vuid);
			result.append("\" target=\"_blank\">");
			result.append(sNames.get(vuid));
			result.append("</a></p><p><a href=\"cp.jsp?ac=friend&op=add&uid=");
			result.append(vuid);
			result.append("\" id=\"a_sex_friend_");
			result.append(i);
			result.append("\" onclick=\"ajaxmenu(event, this.id, 1)\">加为好友</a></p></li>");
		}
		result.append("</ul>");
		task.put("result", result.toString());
	}
}else{
	task.put("guide", "<strong>您还有以下个人资料项需要补充完整：</strong><br>" +
			"<span style=\"color:red;\">"+Common.implode(nones, "<br>")+"</span><br><br>" +
			"<strong>请按照以下的说明来完成本任务：</strong>" +
			"<ul>" +
			"<li><a href=\"cp.jsp?ac=profile\" target=\"_blank\">新窗口打开个人资料设置页面</a>；</li>" +
			"<li>在新打开的设置页面中，将上述个人资料补充完整。</li>" +
			"</ul>");
}
%>